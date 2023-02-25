package kn.uni.games.classic.pacman.game.objects;

import java.awt.Dimension;

public class AdvColliderObject extends AdvPlacedObject
{
  public Dimension         hitBox;
  public Runnable          action;
  public AdvColliderObject partner;

  public AdvColliderObject (Dimension hitBox)
  {
    super();
    this.hitBox = hitBox;
  }

  public void setPartner (AdvColliderObject partner)
  {
    this.partner = partner;
  }

  public void setAction (Runnable action)
  {
    this.action = action;
  }

  public void collide ()
  {
    if (action != null)
      action.run();
  }

  public boolean isColliding (AdvColliderObject target)
  {
    return Math.abs(target.absPos.x - absPos.x) < hitBox.width / 2. + target.hitBox.width / 2. &&
        Math.abs(target.absPos.y - absPos.y) < hitBox.height / 2. + target.hitBox.height / 2.;
  }

}
