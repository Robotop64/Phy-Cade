package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Graphics2D;
import java.awt.Image;

public class FruitItem extends Item implements AdvRendered
{
  AdvGameConst.FruitType fruitType;

  public FruitItem (AdvGameState gameState, Vector2d pos)
  {
    super();
    this.gameState = gameState;

    this.mapPos = pos;
    this.type = ItemType.FRUIT;

    this.fruitType = AdvGameConst.FruitType.values()[( gameState.level + 1 ) % AdvGameConst.FruitType.values().length];
    this.worth = AdvGameConst.fruitScore[fruitType.ordinal()];
  }


  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    g.drawImage((Image) cachedImg, (int) absPos.x, (int) absPos.y, iconSize, iconSize, null);
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  @Override
  public void render ()
  {
    cachedImg = TextureEditor.getInstance().loadTexture("items", fruitType.name() + ".png");
  }

  @Override
  public void consumeAction ()
  {
    super.consumeAction();
    gameState.addScore(worth);
    gameState.layers.get(3).remove(this);
    //TODO: Spawn score number
  }
}
