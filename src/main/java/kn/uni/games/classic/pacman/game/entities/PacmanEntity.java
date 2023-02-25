package kn.uni.games.classic.pacman.game.entities;


import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.graphics.Particle;
import kn.uni.games.classic.pacman.game.graphics.Rendered;
import kn.uni.games.classic.pacman.game.graphics.Ticking;
import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.items.ItemPH;
import kn.uni.games.classic.pacman.game.objects.PacManMap;
import kn.uni.games.classic.pacman.game.objects.PacManMap.TotalPosition;
import kn.uni.games.classic.pacman.game.objects.PacManMapTile;
import kn.uni.games.classic.pacman.game.objects.PlacedObject;
import kn.uni.games.classic.pacman.game.objects.TeleporterObject;
import kn.uni.games.classic.pacman.screens.GameOverScreen;
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

import static kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants.levelFruit;
import static kn.uni.util.Util.round;
import static kn.uni.util.Util.sin;


@SuppressWarnings("NonAsciiCharacters")
public class PacmanEntity extends Entity implements Rendered, Ticking
{
  //general
  public int       r;
  public Vector2d  v;
  //state
  public Direction currentDirection;
  public boolean   canUseDoor;
  public boolean   playerDead;
  public boolean   isVulnerable;
  public boolean   isPoweredUp;
  //animation timings
  long   powerUpStart;
  long   powerUpDuration   = 10 * 120;
  long   deadAnimStartTick = 0;
  double deadAnimDuration;

  public PacmanEntity (int r, Vector2d pos, ClassicPacmanGameState gameState)
  {
    //inherited
    this.pos = pos;
    movable = true;
    //general
    this.r = r;
    //state
    this.currentDirection = Direction.up;
    this.playerDead = false;
    this.isVulnerable = true;
    //animation timings
    deadAnimDuration = gameState.tps / .1;
  }

  /**
   * converts a direction to degrees
   *
   * @param direction the direction to convert
   * @return the degrees
   */
  private static int getΘ (Direction direction)
  {
    return switch (direction)
        {
          case up -> 270;
          case down -> 90;
          case left -> 180;
          case right -> 0;
          case topLeft -> 225;
          case topRight -> 315;
          case bottomLeft -> 135;
          case bottomRight -> 45;
        };
  }

  @Override
  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    pos.rounded().use(g::translate);

    double animationDuration;
    int    θ = getΘ(currentDirection);

    if (!playerDead)
    {
      animationDuration = gameState.tps / 2.;

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

  @Override
  public int paintLayer () { return Integer.MAX_VALUE - 100; }

  /**
   * Event that happens on every tick, ruling of PacMans interactions with the running game
   *
   * @param gameState the current game state
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Override
  public void tick (ClassicPacmanGameState gameState)
  {
    //movement of PacMan
    if (movable)
    {
      TotalPosition tp          = gameState.map.splitPosition(pos);
      PacManMapTile currentTile = gameState.map.tiles.get(tp.ex());
      //get allowed tiles
      List <PacManMapTile> possibleTiles = Arrays.stream(Direction.valuesCardinal())
                                                 .map(Direction::toVector)
                                                 .map(vec -> currentTile.neighbors.get(vec))
                                                 .filter(Objects::nonNull)
                                                 .filter(tile -> PacManMapTile.walkable.contains(tile.type))
                                                 .filter(tile -> !tile.type.equals(PacManMapTile.Type.door) || canUseDoor)
                                                 .toList();

      //visual feedback of possible tiles
      if (DebugDisplay.getDebugDisplay(gameState).enabled)
      {
        gameState.map.tiles.forEach((vec, tile) -> tile.color = Color.black);
        possibleTiles.forEach(tile -> tile.color = Color.blue);
      }

      //set velocity
      if (v == null) v = new Vector2d().cartesian(ClassicPacmanGameConstants.pacmanSpeed, 0).multiply(gameState.map.tileSize).divide(gameState.tps);

      //check if requested direction is possible
      if (possibleTiles.contains(currentTile.neighbors.get(gameState.playerDirection.toVector())) && round(currentDirection.toVector().scalar(tp.in())) == 0)
        currentDirection = gameState.playerDirection;

      //next tile is valid or center has not been reached yet
      if (possibleTiles.contains(currentTile.neighbors.get(currentDirection.toVector())) || round(currentDirection.toVector().scalar(tp.in())) < 0)
        pos = pos.add(currentDirection.toVector().multiply(v.x));
    }

    //add lives according to score
    if (gameState.score % 10000 == 0 && gameState.score != 0 && gameState.score / 10000 > gameState.livesGained)
    {
      gameState.lives += 1;
      gameState.livesGained += 1;
    }

    //spawn fruit after eating half of the pills
    if (!gameState.fruitSpawned && gameState.map.getPillCount() <= 122)
    {
      ClassicPacmanGameConstants.Collectables nextFruit = getFruit(gameState.level);
      gameState.map.tiles.forEach((vec, tile) ->
      {
        if (tile.type == PacManMapTile.Type.playerSpawn)
        {
          gameState.gameObjects.add(
              new ItemPH(
                  tile.pos.add(new Vector2d().cartesian(gameState.map.tileSize / 2., gameState.map.tileSize / 2.)),
                  gameState,
                  nextFruit,
                  true,
                  ClassicPacmanGameConstants.collectionColor.get(nextFruit)));
        }
      });
      gameState.fruitSpawned = true;
    }

    //check level progress
    checkLevelStatus(gameState);

    //check collision
    if (getCollisions(this, gameState.gameObjects).stream().toList().size() > 0)
    {
      //execute action for each collision
      getCollisions(this, gameState.gameObjects)
          .forEach(collidable ->
          {
            //collision with item
            if (collidable instanceof ItemPH item && item.eatable)
            {
              item.setCollider(this);
              item.collide();
              item.setCollider(null);
            }
            //collision with ghost
            if (collidable instanceof GhostEntity ghast && !playerDead && !ghast.isDead)
            {
              //get all ghosts
              final List <GhostEntity> ghostList = new ArrayList <>();
              gameState.gameObjects.stream()
                                   .filter(ghost -> ghost instanceof PlacedObject)
                                   .filter(ghost -> ghost instanceof GhostEntity)
                                   .map(ghost -> (GhostEntity) ghost)
                                   .forEach(ghostList::add);

              //if pacman can die
              if (isVulnerable)
              {
                death(gameState);

                //freeze pacman and the ghosts
                movable = false;
                ghostList.forEach(ghost2 -> ghost2.movable = false);
              }

              //if pacman is poweredUp
              if (isPoweredUp && !ghast.isDead && ghast.vulnerable)
              {
                //add score
                int ghostScore = ( 4 / ghostList.size() ) * 200;
                gameState.score += ghostScore;
                //make ghost flee
                ghast.ai.setMode(ClassicPacmanGameConstants.mode.RETREAT, ghast);
                ghast.isDead = true;
                //spawn particel
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

    //return pacman to normal after powerup runs out
    if (gameState.currentTick > powerUpStart + powerUpDuration && isPoweredUp)
    {
      isPoweredUp = false;
      isVulnerable = true;
      gameState.gameObjects.stream()
                           .filter(o -> o instanceof GhostEntity)
                           .map(o -> (GhostEntity) o)
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
   * Used to powerup the player if he eats a powerup item
   *
   * @param gameState current gameState
   */
  public void powerUp (ClassicPacmanGameState gameState)
  {
    isPoweredUp = true;
    isVulnerable = false;
    powerUpStart = gameState.currentTick;
  }

  /**
   * Event of a player dying to a ghost
   *
   * @param gameState the current game state
   */
  private void death (ClassicPacmanGameState gameState)
  {
    gameState.lives -= 1;

    this.playerDead = true;

    deadAnimStartTick = gameState.currentTick + 1;

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
      //spawn new items
      gameState.map.setItems(new ArrayList <>());
    }
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

  /**
   * Reloads the level by reinitializing the map, player, ghosts and teleporters
   *
   * @param gameState the current game state
   */
  private void reloadLevel (ClassicPacmanGameState gameState)
  {
    //mark objects for removal
    gameState.gameObjects.stream()
                         .filter(gameObject -> gameObject instanceof PacmanEntity || gameObject instanceof GhostEntity || gameObject instanceof PacManMap || gameObject instanceof TeleporterObject)
                         .map(gameObject -> (PlacedObject) gameObject)
                         .forEach(PlacedObject::markExpired);

    //create new Map
    PacManMap map = new PacManMap(gameState, new Vector2d().cartesian(gameState.mapOffset, gameState.mapOffset), 1000, 1000);
    gameState.gameObjects.add(map);
    gameState.map = map;
    gameState.size = new Vector2d().cartesian(map.width, map.height);
    map.addEntities(gameState);
    //allow ghosts to exit
    unlockGhosts(gameState);
  }

  /**
   * returns the next fruit to be spawned
   *
   * @param level the current level
   * @return the next fruit to be spawned
   */
  private ClassicPacmanGameConstants.Collectables getFruit (int level)
  {
    level = ( level - 1 ) % levelFruit.length;
    return levelFruit[level];
  }

  /**
   * Unlocks the ghosts depending on the current level
   *
   * @param gameState the current game state
   */
  private void unlockGhosts (ClassicPacmanGameState gameState)
  {
    if (gameState.level < 4)
    {
      gameState.gameObjects.stream()
                           .filter(ghost -> ghost instanceof GhostEntity)
                           .map(ghost -> (GhostEntity) ghost)
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
                           .filter(ghost -> ghost instanceof GhostEntity)
                           .map(ghost -> (GhostEntity) ghost)
                           .forEach(ghost ->
                           {
                             ghost.canUseDoor = true;
                             ghost.free = true;
                           });
    }
  }
}
