package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class PPelletItem extends Item implements AdvRendered
{
  public static BufferedImage classIcon;

  public PPelletItem (AdvGameState gameState, Vector2d mapPos)
  {
    super();
    this.gameState = gameState;

    this.mapPos = mapPos;
    this.type = ItemType.PPELLET;
    this.worth = 100;
  }

  @Override
  public void paintComponent (Graphics2D g)
  {
    if (classIcon == null)
      render();

    g.drawImage((Image) classIcon, (int) absPos.x, (int) absPos.y, null);
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
}
