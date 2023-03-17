package kn.uni.games.classic.pacman.game.internal.physics;

import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;

public interface AdvColliding
{
  void onCollision (AdvGameObject collider);
}
