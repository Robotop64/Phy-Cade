package kn.uni.games.classic.pacman.game.items;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.entities.GhostEntity;
import kn.uni.games.classic.pacman.game.entities.PacmanEntity;
import kn.uni.games.classic.pacman.game.graphics.Particle;
import kn.uni.games.classic.pacman.game.graphics.Rendered;
import kn.uni.games.classic.pacman.game.graphics.Ticking;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants;
import kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.objects.CollidableObject;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static kn.uni.games.classic.pacman.game.internal.ClassicPacmanGameConstants.collectionPoints;

public class ItemPH extends CollidableObject implements Rendered, Ticking
{
  //general variables
  private final BufferedImage                           icon;
  private final Color                                   scoreColor;
  public        ClassicPacmanGameConstants.Collectables type;
  public        long                                    deSpawnTicks = 125;
  //state variables
  public        boolean                                 eatable;

  public ItemPH (Vector2d pos, ClassicPacmanGameState gameState, ClassicPacmanGameConstants.Collectables type, boolean eatable, Color scoreColor)
  {
    super();
    //inherited
    this.pos = pos;
    this.movable = false;
    this.collideAction = () -> eat(gameState);
    //generals
    this.type = type;
    this.scoreColor = scoreColor;
    //load icon texture
    icon = TextureEditor.getInstance().loadTexture("items", type.name() + ".png");
    this.hitbox = new Vector2d().cartesian(icon.getWidth() - 10, icon.getHeight() - 10);
    //state variables
    this.eatable = eatable;
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

  /**
   * action to be executed when the player collides with this item
   *
   * @param gameState the game state
   */
  private void eat (ClassicPacmanGameState gameState)
  {
    //special effects of item
    if (type == ClassicPacmanGameConstants.Collectables.powerUp)
    {
      //empower pacman
      gameState.gameObjects.stream()
                           .filter(o -> o instanceof PacmanEntity)
                           .map(o -> (PacmanEntity) o)
                           .forEach(o -> o.powerUp(gameState));

      //frighten ghosts
      gameState.gameObjects.stream()
                           .filter(o -> o instanceof GhostEntity)
                           .map(o -> (GhostEntity) o)
                           .filter(o -> o.currentMode != ClassicPacmanGameConstants.mode.EXIT)
                           .filter(o -> o.currentMode != ClassicPacmanGameConstants.mode.RETREAT)
                           .forEach(o ->
                           {
                             o.ai.setMode(ClassicPacmanGameConstants.mode.FRIGHTENED, o);
                             o.vulnerable = true;
                             o.direction = o.direction.opposite();
                           });
    }
    //add score
    gameState.score += collectionPoints.get(this.type);
    //add score particle
    gameState.gameObjects.add(new Particle(Particle.Type.Number, String.valueOf(collectionPoints.get(this.type)), pos, gameState.currentTick, deSpawnTicks, scoreColor));
    //remove this
    gameState.gameObjects.remove(this);
  }
}
