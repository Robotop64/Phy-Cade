package kn.uni.games.classic.pacman.game;


import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ClassicPacmanMap.TotalPosition;
import kn.uni.games.classic.pacman.game.ghosts.Ghost;
import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.games.classic.pacman.screens.GameOverScreen;
import kn.uni.ui.InputListener;
import kn.uni.util.Direction;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants.levelFruit;
import static kn.uni.util.Util.round;
import static kn.uni.util.Util.sin;


@SuppressWarnings("NonAsciiCharacters")
public class PacmanObject extends CollidableObject implements Rendered, Ticking
{
  public double               tilesPerSecond = ClassicPacmanGameConstants.pacmanSpeed;
  public int                  r;
  public Vector2d             v;
  public Direction            currentDirection;
  public boolean              canUseDoor;
  public boolean              playerDead;
  public boolean              isVulnerable;
  public boolean              isPoweredUp;
  public InputListener.Player player;
  long   powerUpStart;
  long   powerUpDuration   = 10 * 120;
  long   deadAnimStartTick = 0;
  double deadAnimDuration;

  public PacmanObject (int r, Vector2d pos, ClassicPacmanGameState gameState, InputListener.Player player)
  {
    this.pos = pos;
    this.r = r;
    this.currentDirection = Direction.up;
    this.player = player;
    this.playerDead = false;
    this.isVulnerable = true;
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
    pos.rounded().use(g::translate);

    double animationDuration;
    int    θ;

    if (!playerDead)
    {
      animationDuration = gameState.tps / 2.;

      θ = getΘ(currentDirection);
      g.rotate(Math.toRadians(θ));

      int angle = (int) Math.round(20 + 40 * sin(( gameState.currentTick % animationDuration ) / animationDuration * 360.));

      g.setColor(Color.orange.darker());
      g.setStroke(new BasicStroke(Math.round(r / 3.)));
      g.drawArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
      IntStream.of(-1, 1).forEach(i -> new Vector2d().polar(r, i * angle).use((x, y) -> g.drawLine(0, 0, x, y)));
      if (!isPoweredUp)
      {
        g.setColor(Color.yellow);
      }
      else
      {
        g.setColor(Color.blue);
      }
      g.fillArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
    }
    else
    {

      animationDuration = deadAnimDuration;

      θ = getΘ(Direction.up);
      g.rotate(Math.toRadians(θ));

      int angle = (int) Math.round(180 * sin(( ( gameState.currentTick - deadAnimStartTick ) % animationDuration ) / animationDuration * 360.));

      g.setColor(Color.orange.darker());
      g.setStroke(new BasicStroke(Math.round(r / 3.)));
      g.drawArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
      IntStream.of(-1, 1).forEach(i -> new Vector2d().polar(r, i * angle).use((x, y) -> g.drawLine(0, 0, x, y)));
      g.setColor(Color.yellow);
      g.fillArc(-r, -r, 2 * r, 2 * r, angle, 360 - 2 * angle);
    }
    g.rotate(Math.toRadians(-θ));

    pos.rounded().multiply(-1).use(g::translate);
  }

  @SuppressWarnings("unused")
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
   * @param gameState the current game state
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    TotalPosition tp          = gameState.map.splitPosition(pos);
    PacmanMapTile currentTile = gameState.map.tiles.get(tp.ex());
    List <PacmanMapTile> possibleTiles = Arrays.stream(Direction.values())
                                               .map(Direction::toVector)
                                               .map(vec -> currentTile.neighbors.get(vec))
                                               .filter(Objects::nonNull)
                                               .filter(tile -> PacmanMapTile.walkable.contains(tile.type))
                                               .filter(tile -> !tile.type.equals(PacmanMapTile.Type.door) || canUseDoor)
                                               .toList();

    //visual feedback of possible tiles
    if (DebugDisplay.getDebugDisplay(gameState).enabled)
    {
      gameState.map.tiles.forEach((vec, tile) -> tile.color = Color.black);
      possibleTiles.forEach(tile -> tile.color = Color.blue);
    }

    //movement of PacMan
    if (movable)
    {
      //set velocity
      if (v == null) v = new Vector2d().cartesian(tilesPerSecond, 0).multiply(gameState.map.tileSize).divide(gameState.tps);

      //check if requested direction is possible
      if (possibleTiles.contains(currentTile.neighbors.get(gameState.playerDirection.toVector())) && round(currentDirection.toVector().scalar(tp.in())) == 0)
        currentDirection = gameState.playerDirection;

      //next tile is valid
      if (possibleTiles.contains(currentTile.neighbors.get(currentDirection.toVector())) || round(currentDirection.toVector().scalar(tp.in())) < 0)
        pos = pos.add(currentDirection.toVector().multiply(v.x));
    }

    //eat items
    if (gameState.score % 10000 == 0 && gameState.score != 0)
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
      getCollisions(this, gameState.gameObjects)
          .forEach(collidable ->
          {
            //collision with item
            if (collidable instanceof ClassicPacmanItemObject item && item.eatable)
            {
              item.setCollider(this);
              item.collide();
              item.setCollider(null);
            }
            //collision with ghost
            //TODO implement later (make ghost eatable)
            if (collidable instanceof Ghost ghast && !playerDead)
            {
              //get all ghosts
              final List <Ghost> ghostList = new ArrayList <>();
              gameState.gameObjects.stream()
                                   .filter(ghost -> ghost instanceof PlacedObject)
                                   .filter(ghost -> ghost instanceof Ghost)
                                   .map(ghost -> (Ghost) ghost)
                                   .forEach(ghostList::add);

              if (isVulnerable)
              {
                death(gameState);

                //freeze pacman and the ghosts
                movable = false;
                ghostList.forEach(ghost2 -> ghost2.movable = false);
              }

              if (isPoweredUp && !ghast.isDead)
              {
                int ghostScore = ( 4 / ghostList.size() ) * 200;
                gameState.score += ghostScore;

                ghast.ai.setMode(ClassicPacmanGameConstants.mode.RETREAT, ghast);
                ghast.isDead = true;

                gameState.gameObjects.add(new Particle(Particle.Type.Number, String.valueOf(ghostScore), ghast.pos, gameState.currentTick, 200, Color.cyan));
              }

            }
            //collision with teleporter
            if (collidable instanceof TeleporterObject teleporter && teleporter.enabled)
            {
              teleporter.setCollider(this);
              teleporter.collide();
              teleporter.setCollider(null);
            }

          });
    }

    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerPosition, "[Pos: " + this.pos.toString() + "]");
    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerDirection, "[Dir: " + gameState.playerDirection + "]");
    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerSpeed, "[Speed: " + Util.roundTo(this.v.x, 0.1) + "]");
    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerState, "[Alive: " + !this.playerDead + "]");
    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerVulnerable, "[Vul: " + this.isVulnerable + "]");
    //    DebugDisplay.setData(gameState, DebugDisplay.DebugType.Player, DebugDisplay.DebugSubType.PlayerPowered, "[Pow: " + this.isPoweredUp + "]");

    if (gameState.currentTick > powerUpStart + powerUpDuration && isPoweredUp)
    {
      isPoweredUp = false;
      isVulnerable = true;
      gameState.gameObjects.stream()
                           .filter(o -> o instanceof Ghost)
                           .map(o -> (Ghost) o)
                           .filter(ghost -> ghost.currentMode == ClassicPacmanGameConstants.mode.FRIGHTENED)
                           .forEach(ghost ->
                           {
                             ghost.currentMode = ClassicPacmanGameConstants.mode.CHASE;
                             ghost.vulnerable = false;
                           });
    }

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
   * @param gameState the current game state
   */
  private void checkLevelStatus (ClassicPacmanGameState gameState)
  {
    if (gameState.map.getPillCount() <= 0)
    {
      gameState.level += 1;
      //resets the coins and powerUps
      reloadLevel(gameState);
      gameState.fruitSpawned = false;
      gameState.map.setItems(new ArrayList <>());


    }
  }

  private void unlockGhosts (ClassicPacmanGameState gameState)
  {
    if (gameState.level < 4)
    {
      gameState.gameObjects.stream()
                           .filter(ghost -> ghost instanceof Ghost)
                           .map(ghost -> (Ghost) ghost)
                           .filter(ghost -> ghost.name.ordinal() < gameState.level % 4)
                           .forEach(ghost ->
                           {
                             ghost.canUseDoor = true;
                             ghost.free = true;
                           });
    }
    else
    {
      gameState.gameObjects.stream()
                           .filter(ghost -> ghost instanceof Ghost)
                           .map(ghost -> (Ghost) ghost)
                           .forEach(ghost ->
                           {
                             ghost.canUseDoor = true;
                             ghost.free = true;
                           });
    }
  }

  /**
   * Event of a player dying to a ghost
   *
   * @param gameState the current game state
   */
  public void death (ClassicPacmanGameState gameState)
  {
    gameState.lives -= 1;

    this.playerDead = true;

    deadAnimStartTick = gameState.currentTick + 1;

  }

  public void powerUp (ClassicPacmanGameState gameState)
  {
    isPoweredUp = true;
    isVulnerable = false;
    powerUpStart = gameState.currentTick;
  }

  /**
   * Event of a players lives reaching 0
   *
   * @param gameState the current game state
   */
  private void checkGameOver (ClassicPacmanGameState gameState)
  {
    if (gameState.lives <= 0)
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
                         .filter(gameObject -> gameObject instanceof PacmanObject || gameObject instanceof Ghost || gameObject instanceof ClassicPacmanMap || gameObject instanceof TeleporterObject)
                         .map(gameObject -> (PlacedObject) gameObject)
                         .forEach(PlacedObject::markExpired);

    //create new Map
    {
      ClassicPacmanMap map = new ClassicPacmanMap(gameState, new Vector2d().cartesian(gameState.mapOffset, gameState.mapOffset), 1000, 1000);
      gameState.gameObjects.add(map);
      gameState.map = map;
      gameState.size = new Vector2d().cartesian(map.width, map.height);
      map.addEntities(gameState);
    }
    unlockGhosts(gameState);
  }

  private ClassicPacmanGameConstants.Collectables getFruit (int level)
  {
    level = ( level - 1 ) % levelFruit.length;
    return levelFruit[level];
  }
}
