package kn.uni.games.classic.pacman.game;

import kn.uni.games.classic.pacman.game.ghosts.Ghost;
import kn.uni.util.Vector2d;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CollidableObject extends PlacedObject
{
  public Vector2d hitbox;

  public CollidableObject ()
  {
    super();
    this.hitbox = new Vector2d();
  }


  public List <CollidableObject> getCollisions (CollidableObject target, ConcurrentLinkedDeque <GameObject> objects)
  {
    List<CollidableObject> collidables = objects.stream().filter(o -> o instanceof CollidableObject).map(o -> (CollidableObject) o).toList();

    List<CollidableObject> colliding = collidables.stream()
            .filter(o -> o != target)
            .filter(o -> o.pos.subtract(target.pos).lenght() <  o.getSize().x / 2 + target.getSize().x / 2)
            .toList();

    return colliding;
  }


  public Vector2d getSize ()
  {
    return hitbox;
  }
}