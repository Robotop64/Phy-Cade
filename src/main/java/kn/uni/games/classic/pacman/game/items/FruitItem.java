package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.games.classic.pacman.game.objects.AdvGameObject;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class FruitItem extends Item implements AdvRendered, AdvColliding
{
  AdvGameConst.FruitType fruitType;

  public FruitItem (AdvGameState gameState, Vector2d mapPos)
  {
    super();
    this.gameState = gameState;

    this.mapPos = mapPos;
    this.type = ItemType.FRUIT;

    this.fruitType = AdvGameConst.FruitType.values()[( gameState.level - 1 ) % AdvGameConst.FruitType.values().length];
    this.worth = AdvGameConst.fruitScore[fruitType.ordinal()];
  }


  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    g.drawImage((Image) cachedImg, (int) ( absPos.x - cachedImg.getWidth() / 2. ), (int) ( absPos.y - cachedImg.getHeight() / 2. ), null);
    g.setColor(Color.GREEN);
    g.drawOval((int) ( absPos.x - AdvGameConst.hitBoxes.get("FruitItem") * AdvGameConst.tileSize ), (int) ( absPos.y - AdvGameConst.hitBoxes.get("FruitItem") * AdvGameConst.tileSize ), (int) ( AdvGameConst.hitBoxes.get("FruitItem") * 2 * AdvGameConst.tileSize ), (int) ( AdvGameConst.hitBoxes.get("FruitItem") * 2 * AdvGameConst.tileSize ));
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  @Override
  public void render ()
  {
    BufferedImage raw = TextureEditor.getInstance().loadTexture("items", fruitType.name() + ".png");
    cachedImg = TextureEditor.getInstance().scale(raw, iconSize, iconSize);
  }

  @Override
  public void consumeAction ()
  {
    super.consumeAction();
    gameState.addScore(worth);
    gameState.layers.get(AdvGameState.Layer.ITEMS.ordinal()).remove(this);
    System.out.println("Fruit consumed");
    //TODO: Spawn score number
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    consumeAction();
  }
}
