package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.*;

public class StalkAI extends GhostAI{

    private Direction currentDirection = Direction.up;

    public StalkAI ()
    {
        borderColor = Color.red;
    }

    @Override
    public Direction getNextDirection(ClassicPacmanGameState gameState) {
        return null;
    }

    @Override
    public Vector2d targetPos(ClassicPacmanGameState gameState) {
        return getPacmanPos(gameState).get(0);
    }
}
