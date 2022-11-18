package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PacmanMapTile;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.*;

public class StalkAI extends GhostAI{

    public StalkAI (ClassicPacmanGameState gameState)
    {
        borderColor = Color.red;
        activeTarget = new Vector2d().cartesian(0,0);
    }

    @Override
    public Direction getNextDirection(ClassicPacmanGameState gameState) {
        return null;
    }

    @Override
    public void setTargetPos(ClassicPacmanGameState gameState) {
        chase = getPacmanPos(gameState).get(0);
        scatter = new Vector2d().cartesian(gameState.map.width,0);
        //exitSpawn = gameState.map.getTilesOfType(PacmanMapTile.Type.ghostExit).get(0).pos;
    }


}
