package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.PacPhiConfig;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvCollider extends AdvGameObject implements AdvTicking, AdvRendered
{

  List <Entity> entities;

  List <List <AdvPlacedObject>> filteredColliders;


  public AdvCollider (AdvGameState gameState)
  {
    super();
    this.gameState = gameState;

    entities = new ArrayList <>();
    filteredColliders = new ArrayList <>();
  }

  public void checkCollisions ()
  {
    entities = gameState.layers.get(AdvGameState.Layer.ENTITIES.ordinal()).stream()
                               .filter(o -> o instanceof AdvColliding)
                               .map(o -> (Entity) o)
                               .toList();

    List <AdvPlacedObject> colliders = new ArrayList <>();
    Arrays.stream(AdvGameState.Layer.values())
          .filter(l -> l != AdvGameState.Layer.ENTITIES)
          .forEach(l ->
              gameState.layers.get(l.ordinal()).stream()
                              .filter(o -> o instanceof AdvColliding)
                              .filter(o -> o instanceof AdvPlacedObject)
                              .map(o -> (AdvPlacedObject) o)
                              .forEach(colliders::add)
          );

    filteredColliders = entities.stream()
                                //get the mapPos of the entity
                                .map(e -> (Vector2d) e.getMapPos())
                                //get the absPos of the tile
                                .map(t -> ( (AdvPacManMap) gameState.layers.get(AdvGameState.Layer.MAP.ordinal()).getFirst() ).tilesPixel.get(t).absPos)
                                //create a rectangle around the tile containing the surrounding tiles
                                .map(t -> new Rectangle((int) ( t.x - AdvGameConst.tileSize ), (int) ( t.y - AdvGameConst.tileSize ), AdvGameConst.tileSize * 3, AdvGameConst.tileSize * 3))
                                //filter colliders to only those that are in the rectangle
                                .map(t -> colliders.stream().filter(o -> pointInRect(o.absPos, t)).toList())
                                .toList();

    entities.forEach(e ->
    {
      filteredColliders.get(entities.indexOf(e)).forEach(c ->
      {
        if (colliding(e, c))
        {
          ( (AdvColliding) e ).onCollision(c);
          ( (AdvColliding) c ).onCollision(e);
        }
      });
    });
  }

  private boolean colliding (AdvPlacedObject a, AdvPlacedObject b)
  {
    double radA = AdvGameConst.hitBoxes.get(a.getClass().getSimpleName());
    double radB = AdvGameConst.hitBoxes.get(b.getClass().getSimpleName());

    double dist = a.absPos.subtract(b.absPos).length();

    return dist <= ( radA + radB ) * AdvGameConst.tileSize;
  }

  private boolean pointInRect (Vector2d point, Rectangle2D rect)
  {
    return point.x >= rect.getMinX() && point.x <= rect.getMaxX() && point.y >= rect.getMinY() && point.y <= rect.getMaxY();
  }

  @Override
  public void tick ()
  {
    checkCollisions();
    if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
      gameState.env.updateLayer.set(AdvGameState.Layer.PHYSICS.ordinal(), true);
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    if (PacPhiConfig.getInstance().settings.get("Debugging").get("-").get("Enabled").setting().current().equals(true))
    {
      filteredColliders.forEach(
          l -> l.forEach(o ->
          {
            g.setColor(Color.GREEN);
            g.drawRect((int) o.absPos.x - 10, (int) o.absPos.y - 10, 20, 20);
          })
      );

      List <Rectangle> rect = entities.stream()
                                      //get the mapPos of the entity
                                      .map(e -> (Vector2d) e.getMapPos())
                                      //get the absPos of the tile
                                      .map(t -> ( (AdvPacManMap) gameState.layers.get(AdvGameState.Layer.MAP.ordinal()).getFirst() ).tilesPixel.get(t).absPos)
                                      //create a rectangle around the tile containing the surrounding tiles
                                      .map(t -> new Rectangle((int) ( t.x - AdvGameConst.tileSize ), (int) ( t.y - AdvGameConst.tileSize ), AdvGameConst.tileSize * 3, AdvGameConst.tileSize * 3))
                                      .toList();
      g.setColor(Color.RED);
      rect.forEach(r -> g.drawRect((int) r.getMinX(), (int) r.getMinY(), (int) r.getWidth(), (int) r.getHeight()));
    }
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  @Override
  public void render ()
  {

  }
}
