package kn.uni.games.classic.pacman.game.items;

import kn.uni.util.Vector2d;

public class Item
{
  public ItemType type;

  public static Item createItem (ItemType type, Vector2d pos)
  {
    return switch (type)
        {
          case PELLET -> new PelletItem(pos);
          case POWER_PELLET -> new PPelletItem(pos);
          case FRUIT -> new FruitItem(pos);
          default -> null;
        };
  }

  public enum ItemType
  {
    PELLET, POWER_PELLET, FRUIT
  }
}
