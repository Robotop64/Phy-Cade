package kn.uni.games.classic.pacman.game;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ghosts.Ghost;
import kn.uni.util.TextureEditor;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants.collectionPoints;

public class ClassicPacmanItemObject extends CollidableObject implements Rendered, Ticking
{
  private final BufferedImage                           icon;
  private final Color                                   scoreColor;
  public        ClassicPacmanGameConstants.Collectables type;
  public        boolean                                 eatable;
  public        long                                    deSpawnTicks = 125;

  public ClassicPacmanItemObject (Vector2d pos, ClassicPacmanGameState gameState, ClassicPacmanGameConstants.Collectables type, boolean eatable, Color scoreColor)
  {
    super();
    this.pos = pos;
    this.type = type;
    this.movable = false;
    this.eatable = eatable;
    this.scoreColor = scoreColor;

    this.collideAction = () -> eat(gameState);

    //load icon texture
    icon = TextureEditor.getInstance().loadTexture("items", type.name() + ".png");
    this.hitbox = new Vector2d().cartesian(icon.getWidth() - 10, icon.getHeight() - 10);
  }

  public void paintComponent (Graphics2D g, ClassicPacmanGameState gameState)
  {
    //draw icon
    Vector2d topLeft = new Vector2d().cartesian(icon.getWidth(), icon.getHeight()).multiply(-.5).add(pos);
    topLeft.use(g::translate);
    g.drawImage(icon, 0, 0, Gui.getInstance().frame);
    topLeft.multiply(-1).use(g::translate);
  }

  public int paintLayer ()
  {
    return Integer.MAX_VALUE - 120;
  }

  @Override
  public void tick (ClassicPacmanGameState gameState) { }

  private void eat (ClassicPacmanGameState gameState)
  {

    gameState.score += collectionPoints.get(this.type);

    gameState.gameObjects.remove(this);

    if (type == ClassicPacmanGameConstants.Collectables.powerUp)
    {
      gameState.gameObjects.stream()
                           .filter(o -> o instanceof PacmanObject)
                           .map(o -> (PacmanObject) o)
                           .filter(o -> o.player == gameState.player)
                           .forEach(o -> o.powerUp(gameState));

      gameState.gameObjects.stream()
                           .filter(o -> o instanceof Ghost)
                           .map(o -> (Ghost) o)
                           .filter(o -> o.currentMode != ClassicPacmanGameConstants.mode.EXIT)
                           .forEach(o ->
                           {
                             o.ai.setMode(ClassicPacmanGameConstants.mode.FRIGHTENED, o);
                             o.vulnerable = true;
                             o.direction = o.direction.opposite();
                           });
    }

    gameState.gameObjects.add(new Particle(Particle.Type.Number, String.valueOf(collectionPoints.get(this.type)), pos, gameState.currentTick, deSpawnTicks, scoreColor));

  }
}
