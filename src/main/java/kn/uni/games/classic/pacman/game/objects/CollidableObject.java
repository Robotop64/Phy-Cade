package kn.uni.games.classic.pacman.game.objects;

import kn.uni.util.Vector2d;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@SuppressWarnings("SpellCheckingInspection")
public class CollidableObject extends PlacedObject
{
  public Vector2d         hitbox;
  public Runnable         collideAction;
  public CollidableObject collider;

  public CollidableObject ()
  {
    super();
    this.hitbox = new Vector2d();
  }

  /**
   * Returns a list containing all objects, that collide with this object.
   *
   * @param target  the object the collision is checked for
   * @param objects the objects to check for collision
   * @return a list containing all objects, that collide with this object
   */
  public List <CollidableObject> getCollisions (CollidableObject target, ConcurrentLinkedDeque <AdvGameObject> objects)
  {
    List <CollidableObject> collidables = objects.stream()
                                                 .filter(o -> o instanceof CollidableObject)
                                                 .map(o -> (CollidableObject) o)
                                                 .toList();

    return collidables.stream()
                      .filter(o -> o != target)
                      .filter(o -> o.pos.subtract(target.pos).length() < o.hitbox.x / 2 + target.hitbox.x / 2)
                      .toList();
  }

  /**
   * executes the action assigned to the object
   */
  public void collide ()
  {
    if (collideAction != null)
    {
      collideAction.run();
    }
  }

  /**
   * used to assign a collider to the object / trigger of the collision
   *
   * @param collider the object that triggers the collision
   */
  public void setCollider (CollidableObject collider) { this.collider = collider; }
}
