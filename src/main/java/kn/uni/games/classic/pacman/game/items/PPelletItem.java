package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class PPelletItem extends Item implements AdvRendered, AdvColliding
{
  public static BufferedImage classIcon;

  public PPelletItem (AdvGameState gameState, Vector2d mapPos)
  {
    super();
    this.gameState = gameState;

    this.mapPos = mapPos;
    this.type = AdvGameConst.ItemType.PPELLET;
    this.worth = AdvGameConst.itemWorth.get(type);
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    if (classIcon == null)
      render();

    g.drawImage((Image) classIcon, (int) ( absPos.x - classIcon.getWidth() / 2. ), (int) ( absPos.y - classIcon.getHeight() / 2. ), null);
  }

  @Override
  public int paintLayer ()
  {
    return 0;
  }

  @Override
  public void render ()
  {
    BufferedImage raw = TextureEditor.getInstance().loadTexture("items", type.name() + ".png");
    classIcon = TextureEditor.getInstance().scale(raw, iconSize, iconSize);
  }

  @Override
  public void consumeAction ()
  {
    super.consumeAction();
    gameState.addScore(worth);
    gameState.layers.get(AdvGameState.Layer.ITEMS.ordinal()).remove(this);
    //TODO: Add power pellet effect
    //TODO: add task to remove power pellet effect to timer
    //TODO: Spawn score number
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    consumeAction();
  }
}
