package kn.uni.games.classic.pacman.game;

import kn.uni.util.Vector2d;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CollidableObject extends PlacedObject
{
  public CollidableObject ()
  {
    super();
  }


  public List <GameObject> getCollisions (CollidableObject target, ConcurrentLinkedDeque <GameObject> objects)
  {
    objects.stream()
           .filter(o -> o instanceof CollidableObject)
           .filter(o -> o != target)
           .forEach(o -> System.out.println(( (CollidableObject) o ).pos.subtract(target.pos).lenght()));
    //TODO works but origin of item is in top left and not on the fruit
    return objects.stream()
                  .filter(o -> o instanceof CollidableObject)
                  .filter(o -> o != target)
                  .filter(o -> ( (CollidableObject) o ).pos.subtract(target.pos).lenght() < ( (CollidableObject) o ).getSize().x / 2 + target.getSize().x / 2)
                  .toList();
  }


  public Vector2d getSize ()
  {
    return null;
  }
}
