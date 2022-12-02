package kn.uni.games.classic.pacman.game;

import kn.uni.Gui;
import kn.uni.util.Direction;
import kn.uni.util.TextureEditor;
import kn.uni.util.Vector2d;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TeleporterObject extends CollidableObject implements Rendered, Ticking
{
  public TeleporterObject pair;
  private Direction outDirection;
  private BufferedImage texture;

    public TeleporterObject(ClassicPacmanGameState gameState, ClassicPacmanMap map, Vector2d pos, Direction outDirection) {
        super();
        this.pos = pos;
        this.movable = false;
        this.outDirection = outDirection;


      this.texture = TextureEditor.getInstance().loadTexture("Objects", "Teleporter.png");
      this.texture = TextureEditor.getInstance().scale(this.texture, map.tileSize, map.tileSize);
      this.hitbox = new Vector2d().cartesian(texture.getWidth(), texture.getHeight());
      this.collideAction = () -> teleport(gameState);
    }


    @Override
    public void paintComponent(Graphics2D g, ClassicPacmanGameState gameState) {
        pos.use(g::translate);
        g.drawOval((int) (-hitbox.x/2), (int) (-hitbox.y/2), (int) hitbox.x, (int) hitbox.y);
        Vector2d topLeft = new Vector2d().cartesian(texture.getWidth(), texture.getHeight()).multiply(-.5).add(pos);
        topLeft.use(g::translate);
        g.drawImage(texture, 0, 0, Gui.getInstance().frame);
        topLeft.multiply(-1).use(g::translate);
        pos.multiply(-1).use(g::translate);
    }

    @Override
    public int paintLayer() {
        return Integer.MAX_VALUE - 110;
    }

    @Override
    public void tick(ClassicPacmanGameState gameState) {

    }

    private void teleport(ClassicPacmanGameState gameStat){
      this.collider.pos = pair.pos.add(new Vector2d().cartesian(gameStat.map.tileSize/2., gameStat.map.tileSize/2.));
      gameStat.playerDirection= outDirection;
        System.out.println("Teleport");
    }

    public void pair(TeleporterObject a){
      a.pair = this;
      this.pair = a;
    }

}


