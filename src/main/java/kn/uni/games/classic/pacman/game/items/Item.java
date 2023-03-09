package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.games.classic.pacman.game.objects.AdvPlacedObject;
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
  }

  public void expire ()
  {
    this.gameState.layers.get(2).remove(this);
  }

  public enum ItemType
  {
    PELLET, PPELLET, FRUIT
  }
}
