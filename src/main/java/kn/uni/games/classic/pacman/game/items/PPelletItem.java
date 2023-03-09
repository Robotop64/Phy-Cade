package kn.uni.games.classic.pacman.game.items;

import kn.uni.games.classic.pacman.game.graphics.AdvRendered;
import kn.uni.games.classic.pacman.game.internal.AdvGameState;
import kn.uni.util.Vector2d;
import kn.uni.util.fileRelated.TextureEditor;

import java.awt.Graphics2D;
import java.awt.Image;

public class PPelletItem extends Item implements AdvRendered
{

  public PPelletItem (AdvGameState gameState, Vector2d mapPos)
  {
    super();
    this.gameState = gameState;

    this.mapPos = mapPos;
    this.type = ItemType.PPELLET;
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
    cachedImg = TextureEditor.getInstance().loadTexture("items", type.name() + ".png");
  }

  @Override
  public void consumeAction ()
  {
    gameState.addScore(100);
    gameState.layers.get(3).remove(this);
    //TODO: Add power pellet effect
    //TODO: Spawn score number
  }
}
