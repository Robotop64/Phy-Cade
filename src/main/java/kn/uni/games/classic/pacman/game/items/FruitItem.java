package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.Config.Config;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class FruitItem extends Item implements AdvRendered, AdvColliding
{
  AdvGameConst.FruitType fruitType;

  public FruitItem (AdvGameState gameState, Vector2d mapPos)
  {
    super();
    this.gameState = gameState;

    this.mapPos = mapPos;
    this.type = AdvGameConst.ItemType.FRUIT;

    int fruitIndex = AdvGameConst.fruitSpawn[( gameState.level - 1 ) % AdvGameConst.fruitSpawn.length];
    System.out.println("Fruit index: " + fruitIndex);
    this.fruitType = AdvGameConst.FruitType.values()[fruitIndex - 1];

    this.worth = AdvGameConst.fruitScore[fruitType.ordinal()];
  }


  @Override
  public void paintComponent (Graphics2D g)
  {
    if (cachedImg == null)
      render();

    g.drawImage((Image) cachedImg, (int) ( absPos.x - cachedImg.getWidth() / 2. ), (int) ( absPos.y - cachedImg.getHeight() / 2. ), null);

    if (Objects.equals(Config.getCurrent("Debugging/-/Enabled"), true))
    {
      g.setColor(Color.GREEN);
      g.drawOval(
          (int) ( absPos.x - AdvGameConst.hitBoxes.get("FruitItem") * AdvGameConst.tileSize ),
          (int) ( absPos.y - AdvGameConst.hitBoxes.get("FruitItem") * AdvGameConst.tileSize ),
          (int) ( AdvGameConst.hitBoxes.get("FruitItem") * 2 * AdvGameConst.tileSize ),
          (int) ( AdvGameConst.hitBoxes.get("FruitItem") * 2 * AdvGameConst.tileSize ));
    }
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
    gameState.addScore(worth);
    gameState.layers.get(AdvGameState.Layer.ITEMS.ordinal()).remove(this);
    //TODO: Spawn score number
    super.consumeAction();
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    consumeAction();
  }
}
