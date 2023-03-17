package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.util.Vector2d;

public class Item extends AdvPlacedObject
{
  public ItemType type;
  public int      worth;

  public static Item createItem (AdvGameState gameState, ItemType type, Vector2d mapPos)
  {
    return switch (type)
        {
          case PELLET -> new PelletItem(gameState, mapPos);
          case PPELLET -> new PPelletItem(gameState, mapPos);
          case FRUIT -> new FruitItem(gameState, mapPos);
          default -> null;
        };
  }

  public void consumeAction ()
  {
    if (this instanceof PelletItem)
      gameState.pelletsEaten++;

    gameState.checkFruit();
    gameState.checkProgress();

    gameState.env.updateLayer.set(AdvGameState.Layer.ITEMS.ordinal(), true);
  }

  public void expire ()
  {
    this.gameState.layers.get(AdvGameState.Layer.ITEMS.ordinal()).remove(this);
  }

  public enum ItemType
  {
    PELLET, PPELLET, FRUIT
  }
}
