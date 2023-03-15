package kn.uni.games.classic.pacman.game.objects;

import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.graphics.AdvTicking;
import kn.uni.games.classic.pacman.game.internal.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvCollider extends AdvGameObject implements AdvTicking, AdvRendered
{
  BufferedImage cachedImg;

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
                                //get entity mapPos
                                .map(e -> (Vector2d) e.getMapPos())
                                //get tile of entity mapPos
                                .map(t -> ( (AdvPacManMap) gameState.layers.get(AdvGameState.Layer.MAP.ordinal()).getFirst() ).tilesPixel.get(t))
                                //get all tiles around entity tile
                                .map(t -> t.neighbors.values())
                                //create set of mapPos of all tiles around entity tile
                                .map(t -> t.stream().map(tile -> tile.mapPos).toList())
                                //filter colliders to only those that are in the set of mapPos
                                .map(t -> colliders.stream().filter(o -> t.contains(o.mapPos)).toList())
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

  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
    {
      render();
    }

    g.drawImage(cachedImg, 0, 0, null);
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  @Override
  public void render ()
  {
    cachedImg = new BufferedImage(gameState.env.display.getWidth(), gameState.env.display.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = cachedImg.createGraphics();

    if (entities.size() > 0)
    {
      entities.forEach(e ->
      {
        g.setColor(Color.RED);
        g.fillOval((int) e.absPos.x - 10, (int) e.absPos.y - 10, 20, 20);
      });


      filteredColliders.forEach(l ->
      {
        l.forEach(c ->
        {
          g.setColor(Color.GREEN);
          g.fillOval((int) c.absPos.x - 10, (int) c.absPos.y - 10, 20, 20);
        });
      });
    }
  }

  @Override
  public void tick ()
  {
    checkCollisions();
  }
}
