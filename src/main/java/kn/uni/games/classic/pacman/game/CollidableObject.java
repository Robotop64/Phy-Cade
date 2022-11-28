package kn.uni.games.classic.pacman.game;

import kn.uni.util.Vector2d;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@SuppressWarnings("SpellCheckingInspection")
public class CollidableObject extends PlacedObject {
    public Vector2d hitbox;
    public Runnable collideAction;

    public CollidableObject() {
        super();
        this.hitbox = new Vector2d();
    }


    public List<CollidableObject> getCollisions(CollidableObject target, ConcurrentLinkedDeque<GameObject> objects) {
        List<CollidableObject> collidables = objects.stream().filter(o -> o instanceof CollidableObject).map(o -> (CollidableObject) o).toList();

        return collidables.stream()
                .filter(o -> o != target)
                .filter(o -> o.pos.subtract(target.pos).length() < o.getSize().x / 2 + target.getSize().x / 2)
                .toList();
    }


    public Vector2d getSize() {
        return hitbox;
    }

    public void collide() {
        if (collideAction != null) {
            collideAction.run();
        }
    }
}
