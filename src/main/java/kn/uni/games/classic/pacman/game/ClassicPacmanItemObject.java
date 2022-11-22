package kn.uni.games.classic.pacman.game;

import kn.uni.Gui;
import kn.uni.games.classic.pacman.game.ghosts.Ghost;
import kn.uni.util.TextureLoader;
import kn.uni.util.Util;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants.collectionColor;
import static kn.uni.games.classic.pacman.game.ClassicPacmanGameConstants.collectionPoints;

public class ClassicPacmanItemObject extends CollidableObject implements Rendered, Ticking {
    public ClassicPacmanGameConstants.Collectables type;
    public boolean eatable;
    public boolean eaten;
    public long eatenTick;
    public long despawnTicks = 125;
    public long powerDownTicks = 20 * 120;
    private BufferedImage icon;
    private Color scoreColor;

    public ClassicPacmanItemObject(Vector2d pos, ClassicPacmanGameState gameState, ClassicPacmanGameConstants.Collectables type, boolean eatable, Color scoreColor) {
        super();
        this.pos = pos;
        this.type = type;
        this.movable = false;
        this.eatable = eatable;
        this.eaten = false;
        this.scoreColor = scoreColor;

        this.collideAction = () ->
        {
            eat(gameState);
        };

        //load icon texture
        icon = TextureLoader.getInstance().loadTexture("items", type.name() + ".png");
        this.hitbox = new Vector2d().cartesian(icon.getWidth() - 10, icon.getHeight() - 10);
    }

    public void paintComponent(Graphics2D g, ClassicPacmanGameState gameState) {
        //draw icon if item is not eaten
        if (!eaten) {
            //draw icon
            Vector2d topLeft = new Vector2d().cartesian(icon.getWidth(), icon.getHeight()).multiply(-.5).add(pos);
            topLeft.use(g::translate);
            g.drawImage(icon, 0, 0, Gui.getInstance().frame);
            topLeft.multiply(-1).use(g::translate);
        } else
        //draw score earned by eating fruit
        {
            //scorePos X&Y are used to determine the offset&progression of the score text from the fruit origin
            int scorePosY = gameState.map.tileSize / 4;
            int scorePosX = -gameState.map.tileSize / 4;
            scorePosY += (int) (Util.progression(eatenTick, despawnTicks, gameState) * -10);
            scorePosX += (int) (Util.progression(eatenTick, despawnTicks, gameState) * -5);
            //score color fade out
            Color scoreColor = this.scoreColor;
            //ramp the alpha value of the score text (visible between progression > 0.15 with fade out)
            if (Util.progression(eatenTick, despawnTicks, gameState) < 0.15) {
                g.setColor(new Color(scoreColor.getRed(), scoreColor.getGreen(), scoreColor.getBlue(), 0));
            } else {
                g.setColor(new Color(scoreColor.getRed(), scoreColor.getGreen(), scoreColor.getBlue(), (int) (255 * (1. - Util.progression(eatenTick, despawnTicks, gameState)))));
            }
            g.setFont(Gui.getInstance().content.getFont().deriveFont(20f));
            //translate to tile center
            new Vector2d().cartesian(this.pos.x, this.pos.y).use(g::translate);
            g.drawString(ClassicPacmanGameConstants.collectionPoints.get(type).toString(), scorePosX, scorePosY);
            new Vector2d().cartesian(this.pos.x, this.pos.y).multiply(-1).use(g::translate);
        }
    }

    public int paintLayer() {
        return Integer.MAX_VALUE - 120;
    }

    @Override
    public void tick(ClassicPacmanGameState gameState) {
        if (eaten && (gameState.currentTick > eatenTick + despawnTicks)) {
            gameState.gameObjects.remove(this);
        }
    }

    private void eat(ClassicPacmanGameState gameState) {
        if (!eaten) {
            gameState.score += collectionPoints.get(this.type);
            this.eaten = true;
            this.eatenTick = gameState.currentTick;

            if (type == ClassicPacmanGameConstants.Collectables.powerUp) {
                gameState.gameObjects.stream()
                        .filter(o -> o instanceof PacmanObject)
                        .map(o -> (PacmanObject) o)
                        .filter(o -> o.player == gameState.player)
                        .forEach(o -> {
                            o.isPoweredUp = true;
                            o.isVulnerable = false;
                        });

                gameState.gameObjects.stream()
                        .filter(o -> o instanceof Ghost)
                        .map(o -> (Ghost) o)
                        .filter(o-> o.currentMode != ClassicPacmanGameConstants.mode.EXIT)
                        .forEach(o -> {
                            o.ai.setMode(ClassicPacmanGameConstants.mode.FRIGHTENED, o,gameState);
                            o.vulnerable = true;
                        });
            }

        }
    }
}
