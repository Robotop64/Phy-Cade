package kn.uni.games.classic.pacman.game;

import kn.uni.util.Vector2d;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CollidableObject extends PlacedObject
{
  Vector2d hitbox;

  public CollidableObject ()
  {
    super();
    this.hitbox = new Vector2d();
  }


  public List <CollidableObject> getCollisions (CollidableObject target, ConcurrentLinkedDeque <GameObject> objects)
  {
    List<CollidableObject> collidables = objects.stream().filter(o -> o instanceof CollidableObject).map(o -> (CollidableObject) o).toList();

    collidables.stream()
            .filter(o -> o != target)
           .forEach(o -> System.out.println(o.pos.subtract(target.pos).lenght()));
    //TODO works but origin of item is in top left and not on the fruit
    return collidables.stream()
                  .filter(o -> o != target)
                  .filter(o -> o.pos.subtract(target.pos).lenght() <  o.getSize().x / 2 + target.getSize().x / 2)
                  .toList();
  }


  public Vector2d getSize ()
  {
    return hitbox;
  }
}
