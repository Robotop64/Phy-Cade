package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.AdvColliding;
import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.games.classic.pacman.game.objects.AdvGameObject;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class PelletItem extends Item implements AdvRendered, AdvColliding
{

  public static BufferedImage classIcon;

  public PelletItem (AdvGameState gameState, Vector2d mapPos)
  {
    super();
    this.gameState = gameState;

    this.mapPos = mapPos;
    this.type = ItemType.PELLET;
    this.worth = 100;
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
    gameState.env.updateLayer.set(AdvGameState.Layer.ITEMS.ordinal(), true);
    //TODO: Spawn score number
  }

  @Override
  public void onCollision (AdvGameObject collider)
  {
    consumeAction();
  }
}
