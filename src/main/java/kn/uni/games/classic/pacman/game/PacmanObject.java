package kn.uni.games.classic.pacman.game;


import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ClassicPacmanMap.TotalPosition;
import kn.uni.games.classic.pacman.game.ghosts.Ghost;
import kn.uni.games.classic.pacman.screens.GameOverScreen;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static kn.uni.util.Util.round;
import static kn.uni.util.Util.sin;


public class PacmanObject extends PlacedObject implements Rendered, Ticking
{
  public double tilesPerSecond = ClassicPacmanGameConstants.pacmanSpeed;
  int      r;
  Vector2d v;

  public PacmanObject (int r, Vector2d pos)
  {
    this.pos = pos;
    this.r = r;
  }

  private static int getΘ (Direction direction)
  {
    return switch (direction)
      {
        case up -> 270;
        case down -> 90;
        case left -> 180;
        case right -> 0;
      };
  }

  /**
   * Used to spawn a Pacman at his spawn
   *
   * @param gameState
   */
  public static void spawnPacman (ClassicPacmanGameState gameState)
  {
    //search all tiles for a pacman spawn
    gameState.map.tiles.forEach((vec, tile) ->
    {
      if (tile.type == PacmanMapTile.Type.playerSpawn)
        //create new instance of Pacman
        gameState.gameObjects.add(new PacmanObject((int)(gameState.map.tileSize * 2. / 3.), vec.multiply(gameState.map.tileSize).add(new Vector2d().cartesian(4, 4))));
    });
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    TotalPosition tp = gameState.map.splitPosition(pos);
    pos.rounded().use(g::translate);

    int θ = getΘ(gameState.playerDirection);
    g.rotate(Math.toRadians(θ));
    double animationDuration = gameState.tps / 2.;
    int    angle             = (int)Math.round(20 + 40 * sin((gameState.currentTick % animationDuration) / animationDuration * 360.));

    g.setColor(Color.orange.darker());
    g.setStroke(new BasicStroke(Math.round(r / 3.)));
    g.drawArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
    IntStream.of(-1, 1).forEach(i -> new Vector2d().polar(r, i * angle).use((x, y) -> g.drawLine(0, 0, x, y)));
    g.setColor(Color.yellow);
    g.fillArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
    g.rotate(Math.toRadians(-θ));
    pos.rounded().multiply(-1).rounded().use(g::translate);
  }

  private PacmanMapTile getTile (ClassicPacmanGameState gameState)
  {
    return gameState.map.tiles.get(gameState.map.splitPosition(pos).ex());
  }

  @Override
  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 100;
  }

  /**
   * Event that happens on every tick, ruling of PacMans interactions with the running game
   *
   * @param gameState
   */
  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    if (v == null)
    {
      v = new Vector2d().cartesian(tilesPerSecond, 0).multiply(gameState.map.tileSize).divide(gameState.tps);
      //      System.out.println(v);
    }

    TotalPosition tp          = gameState.map.splitPosition(pos);
    PacmanMapTile currentTile = gameState.map.tiles.get(tp.ex());
    PacmanMapTile nextTile    = currentTile.neighbors.get(gameState.playerDirection.toVector());

    //    System.out.println(gameState.playerDirection.toVector());
    //    System.out.println(tp.in());

    //moving towards centre of current tile
    if (round(gameState.playerDirection.toVector().scalar(tp.in())) < 0)
    {
      //      System.out.println("moving towards centre");
      // far away from some axis
      if (Math.min(pos.x, pos.y) > gameState.map.tileSize / 20.)
      {
        //        System.out.println("far away from axis");
        pos = pos.add(tp.in().multiply(-1).orthogonalTo(gameState.playerDirection.toVector()).unitVector().multiply(v.lenght()));
      }
      pos = pos.add(v.rotate(getΘ(gameState.playerDirection)));
    }
    // walking away from centre
    else
    {
      //      System.out.println("try moving away from centre");
      if (tp.in().x + tp.in().y < gameState.map.tileSize / .3)
      {
        //        System.out.println("close to centre");
        //        System.out.println(nextTile);
        if (nextTile != null && PacmanMapTile.walkable.contains(nextTile.type))
        {
          //          System.out.println("next tile is good");
          // far away from some axis
          if (Math.min(pos.x, pos.y) > gameState.map.tileSize / 20.)
          {
            //            System.out.println("far away from axis");
            pos = pos.add(tp.in().multiply(-1).orthogonalTo(gameState.playerDirection.toVector()).unitVector().multiply(v.lenght()));
          }
          pos = pos.add(v.rotate(getΘ(gameState.playerDirection)));
        }
      }
    }

    //eat item
    if (currentTile.heldItem != null)
    {
      //add score depending on item points
      gameState.score += gameState.collectionPoints.get(currentTile.heldItem);

      if (gameState.score % 10000 == 0)
      {
        gameState.lives += 1;
      }

      if (currentTile.heldItem == ClassicPacmanGameState.Collectables.coin || currentTile.heldItem == ClassicPacmanGameState.Collectables.powerUp)
      {
        gameState.eatenPills += 1;
        gameState.pillsLeft -= 1;
      }


      //deletes the item held by the tile
      currentTile.heldItem = null;

      checkLevelStatus(gameState);


      // check if die
      if (gameState.gameObjects.stream()
                               .filter(obj -> obj instanceof Ghost)
                               .map(ghost -> ((Ghost)ghost).pos)
                               .map(vec -> vec.subtract(pos).magnitude())
                               .anyMatch(mag -> mag <= ClassicPacmanGameConstants.ghostRadius + r))
      {
        death(gameState);
      }
    }
  }

  /**
   * Used to determine if a level has been completed
   *
   * @param gameState
   */
  private void checkLevelStatus (ClassicPacmanGameState gameState)
  {
    if (gameState.pillsLeft == 0)
    {
      gameState.pillsLeft = 244;
      gameState.level += 1;
      //resets PacMans position
      this.pos = ClassicPacmanMap.playerSpawn.pos;
      //resets the coins and powerups
      ClassicPacmanMap.setItems(ClassicPacmanMap.tiles);
    }
  }

  /**
   * Event of a playersdying to a ghost
   *
   * @param gameState
   */
  private void death (ClassicPacmanGameState gameState)
  {
    System.out.println("DEATHHHHHHH");
    gameState.gameObjects.remove(this);
    gameState.lives -= 1;
    checkGameOver(gameState);
  }

  /**
   * Event of a players lives reaching 0
   *
   * @param gameState
   */
  private void checkGameOver (ClassicPacmanGameState gameState)
  {
    if (gameState.lives == 0)
    {
      gameState.running = false;

      List <Integer> score = new ArrayList <>();
      score.add((int)gameState.score);
      List <LocalTime> time = new ArrayList <>();
      //      time.add(gameState.time);


      GameOverScreen gameOverScreen = new GameOverScreen(1, "pacman", score, time);
      Gui.getInstance().content.add(gameOverScreen);
    }
  }
}
