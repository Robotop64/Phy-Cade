package kn.uni.games.classic.pacman.game;


import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ClassicPacmanMap.TotalPosition;
import kn.uni.games.classic.pacman.game.ghosts.Ghost;
import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.games.classic.pacman.screens.GameOverScreen;
import kn.uni.util.Direction;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants.levelFruit;
import static kn.uni.util.Util.round;
import static kn.uni.util.Util.sin;


public class PacmanObject extends CollidableObject implements Rendered, Ticking
{
  public double   tilesPerSecond = ClassicPacmanGameConstants.pacmanSpeed;
  public int      r;
  public Vector2d v;
  public boolean  playerDead;
  public boolean  isVulnerable;
  long   deadAnimStartTick = 0;
  double deadAnimDuration;

  public PacmanObject (int r, Vector2d pos, ClassicPacmanGameState gameState)
  {
    this.pos = pos;
    this.r = r;
    this.playerDead = false;
    this.isVulnerable = false;
    deadAnimDuration = gameState.tps / .1;
    movable = true;
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

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    TotalPosition tp = gameState.map.splitPosition(pos);
    pos.rounded().use(g::translate);


    if (!playerDead)
    {
      double animationDuration = gameState.tps / 2.;

      int θ = getΘ(gameState.playerDirection);
      g.rotate(Math.toRadians(θ));

      int angle = (int) Math.round(20 + 40 * sin(( gameState.currentTick % animationDuration ) / animationDuration * 360.));
      //

      g.setColor(Color.orange.darker());
      g.setStroke(new BasicStroke(Math.round(r / 3.)));
      g.drawArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
      IntStream.of(-1, 1).forEach(i -> new Vector2d().polar(r, i * angle).use((x, y) -> g.drawLine(0, 0, x, y)));
      g.setColor(Color.yellow);
      g.fillArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
      g.rotate(Math.toRadians(-θ));
    }
    else
    {

      double animationDuration = deadAnimDuration;

      int θ = getΘ(Direction.up);
      g.rotate(Math.toRadians(θ));

      int angle = (int) Math.round(180 * sin(( ( gameState.currentTick - deadAnimStartTick ) % animationDuration ) / animationDuration * 360.));

      g.setColor(Color.orange.darker());
      g.setStroke(new BasicStroke(Math.round(r / 3.)));
      g.drawArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
      IntStream.of(-1, 1).forEach(i -> new Vector2d().polar(r, i * angle).use((x, y) -> g.drawLine(0, 0, x, y)));
      g.setColor(Color.yellow);
      g.fillArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
      g.rotate(Math.toRadians(-θ));
    }

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
    TotalPosition tp          = gameState.map.splitPosition(pos);
    PacmanMapTile currentTile = gameState.map.tiles.get(tp.ex());
    PacmanMapTile nextTile    = currentTile.neighbors.get(gameState.playerDirection.toVector());

    //movement of PacMan
    if (movable)
    {
      if (v == null)
      {
        v = new Vector2d().cartesian(tilesPerSecond, 0).multiply(gameState.map.tileSize).divide(gameState.tps);
        //      System.out.println(v);
      }

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
    }


    //eat items
    if (gameState.score % 10000 == 0)
    {
      gameState.lives += 1;
    }

    //spawn fruit after eating half of the pills
    if (!gameState.fruitSpawned && gameState.map.getPillCount() <= 122)
    {
      ClassicPacmanGameConstants.Collectables nextFruit = getFruit(gameState.level);
      gameState.map.tiles.forEach((vec, tile) ->
      {
        if (tile.type == PacmanMapTile.Type.playerSpawn)
        {
          gameState.gameObjects.add(
              new ClassicPacmanItemObject(
                  tile.pos.add(new Vector2d().cartesian(gameState.map.tileSize / 2., gameState.map.tileSize / 2.)),
                  gameState,
                  nextFruit,
                  true,
                  ClassicPacmanGameConstants.collectionColor.get(nextFruit)));
        }
      });
      gameState.fruitSpawned = true;
    }

    checkLevelStatus(gameState);


    //check collision
    //there are collisions
    if (getCollisions(this, gameState.gameObjects).stream().toList().size() > 0)
    {
      //execute action for each collision
      getCollisions(this, gameState.gameObjects).stream()
                                                .forEach(collidable ->
                                                {
                                                  //collision with item
                                                  if (collidable instanceof ClassicPacmanItemObject item && item.eatable)
                                                  {
                                                    item.collide();
                                                  }
                                                  //collision with ghost
                                                  //TODO implement later (make ghost eatable)
                                                  if (collidable instanceof Ghost && !playerDead)
                                                  {
                                                    //get all ghosts
                                                    final List <Ghost> ghostList = new ArrayList <>();
                                                    gameState.gameObjects.stream()
                                                                         .filter(ghost -> ghost instanceof PlacedObject)
                                                                         .filter(ghost -> ghost instanceof Ghost)
                                                                         .forEach(ghost -> ghostList.add((Ghost) ghost));

                                                    if (isVulnerable)
                                                    {
                                                      death(gameState);

                                                      //freeze pacman and the ghosts
                                                      movable = false;
                                                      ghostList.forEach(ghost2 -> ghost2.movable = false);
                                                    }
                                                  }
                                                });
    }

    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerPosition, "[Pos: " + this.pos.toString() + "]");
    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerDirection, "[Dir: " + gameState.playerDirection + "]");
    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerSpeed, "[Speed: " + Util.roundTo(this.v.x, 0.1) + "]");
    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerState, "[Alive: " + !this.playerDead + "]");
    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerVulnerable, "[Vul: " + this.isVulnerable + "]");


    //check for reset and wait for death animation to end
    if (playerDead && ( gameState.currentTick - deadAnimStartTick ) / ( deadAnimDuration / 4 ) > 1)
    {
      this.playerDead = false;

      reloadLevel(gameState);

      checkGameOver(gameState);
    }
  }

  /**
   * Used to determine if a level has been completed
   *
   * @param gameState
   */
  private void checkLevelStatus (ClassicPacmanGameState gameState)
  {
    if (gameState.map.getPillCount() <= 0)
    {
      gameState.level += 1;
      //resets the coins and powerups
      reloadLevel(gameState);
      gameState.fruitSpawned = false;
      gameState.map.setItems(new ArrayList <>());

      if (gameState.level < 4)
      {
        gameState.gameObjects.stream()
                             .filter(ghost -> ghost instanceof Ghost)
                             .map(ghost -> (Ghost) ghost)
                             .filter(ghost -> ghost.name.ordinal() < gameState.level % 4)
                             .forEach(ghost -> ghost.canUseDoor = true);
      }
      else
      {
        gameState.gameObjects.stream()
                             .filter(ghost -> ghost instanceof Ghost)
                             .map(ghost -> (Ghost) ghost)
                             .forEach(ghost -> ghost.canUseDoor = true);
      }


    }
  }

  /**
   * Event of a playersdying to a ghost
   *
   * @param gameState
   */
  public void death (ClassicPacmanGameState gameState)
  {
    gameState.lives -= 1;

    this.playerDead = true;

    deadAnimStartTick = gameState.currentTick + 1;

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
      //stops the game and removes the GameScreen
      gameState.running = false;

      //TODO Move lists out of here, for mp compatibility
      List <Integer>   score  = new ArrayList <>();
      List <LocalTime> time   = new ArrayList <>();
      List <Integer>   levels = new ArrayList <>();

      score.add((int) gameState.score);
      time.add(gameState.gameDuration);
      levels.add(gameState.level);

      GameOverScreen gameOverScreen = new GameOverScreen(1, "pacman", score, time, levels);
      Gui.getInstance().content.add(gameOverScreen);
    }
  }

  private void reloadLevel (ClassicPacmanGameState gameState)
  {
    gameState.gameObjects.stream()
                         .filter(gameObject -> gameObject instanceof PlacedObject)
                         .filter(gameObject -> gameObject instanceof PacmanObject || gameObject instanceof Ghost || gameObject instanceof ClassicPacmanMap)
                         .forEach(gameObject -> ( (PlacedObject) gameObject ).expired = true);

    //create new Map
    {
      ClassicPacmanMap map = new ClassicPacmanMap(gameState, new Vector2d().cartesian(gameState.mapOffset, gameState.mapOffset), 1000, 1000);
      gameState.gameObjects.add(map);
      gameState.map = map;
      gameState.size = new Vector2d().cartesian(map.width, map.height);
      map.addEntities(gameState);
    }
  }

  private ClassicPacmanGameConstants.Collectables getFruit (int level)
  {
    level = ( level - 1 ) % levelFruit.length;
    return levelFruit[level];
  }

  @Override
  public Vector2d getSize ()
  {
    return new Vector2d().cartesian(2 * r, 2 * r);
  }
}
