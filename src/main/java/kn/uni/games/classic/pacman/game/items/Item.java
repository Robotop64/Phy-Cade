package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.util.Vector2d;

public class Item extends AdvPlacedObject implements AdvColliding
{
  public AdvGameConst.ItemType type;
  public int                   worth;

  public static Item createItem (AdvGameState gameState, AdvGameConst.ItemType type, Vector2d mapPos)
  {
    return switch (type)
    {
      case PELLET -> new PelletItem(gameState, mapPos);
      case PPELLET -> new PPelletItem(gameState, mapPos);
      case FRUIT -> new FruitItem(gameState, mapPos);
    };
  }

  public void consumeAction ()
  {
    gameState.objects.remove(AdvGameState.Layer.ITEMS, this);
    gameState.checkFruit();
    gameState.checkProgress();

    gameState.env.updateLayer.set(AdvGameState.Layer.ITEMS.ordinal(), true);
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
  }
}
