package kn.uni.games.classic.pacman.game;

import kn.uni.util.Vector2d;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CollidablePlacedObject extends PlacedObject implements Collidable
{
  public CollidablePlacedObject ()
  {
    super();
  }


  public List <GameObject> getCollisions (CollidablePlacedObject target, ConcurrentLinkedDeque <GameObject> objects)
  {
    objects.stream()
           .filter(o -> o instanceof CollidablePlacedObject)
           .filter(o -> o != target)
           .forEach(o -> System.out.println(( (CollidablePlacedObject) o ).pos.subtract(target.pos).lenght()));
    //TODO works but origin of item is in top left and not on the fruit
    return objects.stream()
                  .filter(o -> o instanceof CollidablePlacedObject)
                  .filter(o -> o != target)
                  .filter(o -> ( (CollidablePlacedObject) o ).pos.subtract(target.pos).lenght() < ( (CollidablePlacedObject) o ).getSize().x / 2 + target.getSize().x / 2)
                  .toList();
  }

  @Override
  public Vector2d getSize ()
  {
    return null;
  }
}
