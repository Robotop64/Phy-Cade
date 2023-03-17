//package kn.uni.games.classic.pacman.game.entities;
//
//import kn.uni.Gui;
//import kn.uni.games.classic.pacman.game.archive.ghostAi.GhostAI;
//import kn.uni.games.classic.pacman.game.graphics.Rendered;
//import kn.uni.games.classic.pacman.game.graphics.Ticking;
//import kn.uni.games.classic.pacman.game.hud.DebugDisplay;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants;
//import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
//import kn.uni.games.classic.pacman.game.objects.CollidableObject;
//import kn.uni.games.classic.pacman.game.objects.PacManMap.TotalPosition;
//import kn.uni.games.classic.pacman.game.objects.PacManMapTile;
//import kn.uni.games.classic.pacman.game.objects.TeleporterObject;
//import kn.uni.util.Direction;
//import kn.uni.util.Util;
//import kn.uni.util.Vector2d;
//import kn.uni.util.fileRelated.TextureEditor;
//
//import javax.imageio.ImageIO;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.util.Objects;
//
//public class GhostEntity extends CollidableObject implements Ticking, Rendered
//{
//  //general & character variables
//  public final  ClassicPacmanGameConstants.ghostNames name;
//  public final  int                                   respawnTime        = 5 * 120;
//  @SuppressWarnings("FieldCanBeLocal")
//  private final int                                   animationFrequency = 8;
//  @SuppressWarnings("FieldCanBeLocal")
//  //path to animation sources
//  private final String                                dirPath            = "pacman/textures/ghosts/";
//  public        GhostAI                               ai;
//  public        long                                  respawnTick;
//  //movement variables
//  public        Direction                             direction;
//  public        PacManMapTile                         currentTile;
//  //state variables
//  public        ClassicPacmanGameConstants.mode       currentMode;
//  public        boolean                               free;
//  public        boolean                               canUseDoor;
//  public        boolean                               vulnerable;
//  public        boolean                               isDead;
//  //animation images
//  private       BufferedImage                         opened;
//  private       BufferedImage                         openedOutline;
//  private       BufferedImage                         closed;
//  private       BufferedImage                         closedOutline;
//  private       BufferedImage                         downedA;
//  private       BufferedImage                         downedB;
//  private       BufferedImage                         downedOutline;
//
//  public GhostEntity (String profName, Vector2d pos, GhostAI ghostAI, ClassicPacmanGameConstants.ghostNames name)
//  {
//    //general & character variables
//    this.name = name;
//    ai = ghostAI;
//    this.hitbox = new Vector2d().cartesian(ClassicPacmanGameConstants.ghostRadius * 2, ClassicPacmanGameConstants.ghostRadius * 2);
//    loadImages("%s%s/%s-".formatted(dirPath, profName, profName));
//    //movement variables
//    this.pos = pos;
//    movable = true;
//    this.direction = Direction.up;
//    //state variables
//    free = false;
//    canUseDoor = false;
//    vulnerable = false;
//    isDead = false;
//    ai.setMode(ClassicPacmanGameConstants.mode.EXIT, this);
//  }
//
//  private void loadImages (String path)
//  {
//    try
//    {
//      closed = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path + "closed.png")));
//      opened = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path + "opened.png")));
//      downedA = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path + "downed-1.png")));
//      downedB = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource(path + "downed-2.png")));
//
//      closedOutline = TextureEditor.getInstance().createOutline(closed, 3, ai.borderColor);
//      openedOutline = TextureEditor.getInstance().createOutline(opened, 3, ai.borderColor);
//      downedOutline = TextureEditor.getInstance().createOutline(downedA, 3, ai.borderColor);
//    }
//    catch (IOException e)
//    {
//      System.out.printf("[ERROR] failed to load images for %s%n", path);
//      throw new RuntimeException(e);
//    }
//
//  }
//
//  @Override
//  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
//  {
//    int           duration = gameState.tps / animationFrequency;
//    BufferedImage im;
//    BufferedImage imOutline;
//
//    //draw normal texture if alive
//    if (!isDead)
//    {
//      im = ( ( gameState.currentTick % duration ) * 1. / duration > .5 ) ? closed : opened;
//      imOutline = ( ( gameState.currentTick % duration ) * 1. / duration > .5 ) ? closedOutline : openedOutline;
//    }
//    else
//    //draw downed texture if dead
//    {
//      im = ( ( gameState.currentTick % ( duration * 4 ) ) * 1. / ( duration * 4 ) > .5 ) ? downedA : downedB;
//      imOutline = downedOutline;
//    }
//
//    //draw texture
//    Vector2d topLeft = new Vector2d().cartesian(im.getWidth(), im.getHeight()).multiply(-.5).add(pos);
//    topLeft.use(g::translate);
//    g.drawImage(im, 0, 0, Gui.getInstance().frame);
//    g.drawImage(imOutline, 0, 0, Gui.getInstance().frame);
//    topLeft.multiply(-1).use(g::translate);
//
//    //debug visualisation
//    if (DebugDisplay.getDebugDisplay(gameState).enabled)
//    {
//      g.translate(pos.x, pos.y);
//      //set color to character color
//      g.setColor(ai.borderColor);
//      //draw debug outline
//      g.drawOval((int) -ClassicPacmanGameConstants.ghostRadius, (int) -ClassicPacmanGameConstants.ghostRadius, (int) ( ClassicPacmanGameConstants.ghostRadius * 2 ), (int) ( ClassicPacmanGameConstants.ghostRadius * 2 ));
//      Vector2d d = direction.toVector().multiply(gameState.map.tileSize);
//      //draw debug looking direction
//      g.drawLine(0, 0, (int) d.x, (int) d.y);
//      g.translate(-pos.x, -pos.y);
//
//      g.translate(ai.activeTarget.x, ai.activeTarget.y);
//      //draw debug target location
//      g.fillOval(-10, -10, 20, 20);
//      g.translate(-ai.activeTarget.x, -ai.activeTarget.y);
//    }
//
//
//  }
//
//  @Override
//  public int paintLayer ()
//  {
//    return Integer.MAX_VALUE - 99;
//  }
//
//  private double getSpeedScale (ClassicPacmanGameState gameState)
//  {
//    return switch (gameState.level)
//        {
//          case 1, 2, 3, 4 -> .75;
//          default -> .85;
//        };
//  }
//
//  @Override
//  public void tick (ClassicPacmanGameState gameState)
//  {
//    //player velocity      = tileSpeed * speedScale * tileToPixel * pixelPerTick
//    double        velocity = ClassicPacmanGameConstants.pacmanSpeed * getSpeedScale(gameState) * gameState.map.tileSize * 1. / gameState.tps;
//    TotalPosition tp       = gameState.map.splitPosition(pos);
//    currentTile = gameState.map.tiles.get(tp.ex());
//
//    //ghost movement
//    if (movable)
//    {
//      //retrieve the position of the targets if BLINKYs pos is available
//      if (ai.getBlinkyPos(gameState).size() > 0) ai.setCasePos(gameState, this);
//
//      //skip check if current tile center has not been reached
//      if (currentTile.center.subtract(this.pos).length() < 1) ai.nextDirection2(gameState, this, currentMode);
//
//      //move the ghost
//      pos = pos.add(direction.toVector().multiply(velocity));
//
//      //change mode if the reached tile identifies as the following
//      //initial exit of ghostSpawn
//      if (currentTile.type.equals(PacManMapTile.Type.ghostExit) && currentMode == ClassicPacmanGameConstants.mode.EXIT)
//      {
//        ai.setMode(ClassicPacmanGameConstants.mode.CHASE, this);
//        this.canUseDoor = false;
//      }
//
//      //target exit of ghostSpawn if eaten
//      if (currentTile.type.equals(PacManMapTile.Type.ghostExit) && currentMode == ClassicPacmanGameConstants.mode.RETREAT)
//      {
//        ai.setMode(ClassicPacmanGameConstants.mode.ENTER, this);
//        this.canUseDoor = true;
//      }
//
//      //if exit of ghostSpawn after getting eaten has been reached enter ghostSpawn
//      if (currentTile.type.equals(PacManMapTile.Type.ghostSpawn) && currentMode == ClassicPacmanGameConstants.mode.ENTER)
//      {
//        ai.setMode(ClassicPacmanGameConstants.mode.EXIT, this);
//        this.canUseDoor = false;
//        respawnTick = gameState.currentTick;
//      }
//    }
//
//    //check collision
//    if (getCollisions(this, gameState.gameObjects).stream().toList().size() > 0)
//    {
//      //execute action for each collision
//      getCollisions(this, gameState.gameObjects)
//          .forEach(collidable ->
//          {
//            //collision with teleporter
//            if (collidable instanceof TeleporterObject teleporter && teleporter.enabled)
//            {
//              teleporter.setCollider(this);
//              teleporter.collide();
//              teleporter.setCollider(null);
//            }
//          });
//    }
//
//    //ghost recovers and exits spawnPen
//    if (gameState.currentTick == respawnTick + respawnTime && free)
//    {
//      this.isDead = false;
//      this.canUseDoor = true;
//    }
//
//    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostName, this, String.valueOf(name));
//    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostPosition, this, "(" + Util.roundTo(pos.x, 0.1) + "," + Util.roundTo(pos.y, 0.1) + ")");
//    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostDirection, this, String.valueOf(direction));
//    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostSpeed, this, String.valueOf(velocity));
//    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostAI, this, ai.getClass().getSimpleName());
//    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostState, this, String.valueOf(currentMode));
//    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostVulnerable, this, String.valueOf(this.vulnerable));
//    DebugDisplay.setGhostData(gameState, DebugDisplay.DebugSubType.GhostTargetDist, this, "(" + Util.roundTo(this.pos.subtract(ai.activeTarget).length(), 0.1) + ")");
//  }
//}
