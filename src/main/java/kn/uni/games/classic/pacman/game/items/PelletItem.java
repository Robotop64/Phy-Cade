package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.internal.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.physics.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameConst;
import kn.uni.games.classic.pacman.game.internal.tracker.AdvGameState;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PelletItem extends Item implements AdvRendered, AdvColliding
{

  public static BufferedImage classIcon;

  public PelletItem (AdvGameState gameState, Vector2d mapPos)
  {
    super();
    this.gameState = gameState;

    this.mapPos = mapPos;
    this.type = AdvGameConst.ItemType.PELLET;
    this.worth = AdvGameConst.itemWorth.get(type);
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    if (classIcon == null)
      render();

    g.drawImage(classIcon, (int) ( absPos.x - classIcon.getWidth() / 2. ), (int) ( absPos.y - classIcon.getHeight() / 2. ), null);
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
    gameState.pelletsEaten++;
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
