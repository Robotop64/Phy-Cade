package kn.uni.games.classic.pacman.game.ghosts;

import kn.uni.games.classic.pacman.game.ClassicPacmanGameState;
import kn.uni.games.classic.pacman.game.PacmanMapTile;
import kn.uni.games.classic.pacman.game.PacmanObject;
import kn.uni.util.Direction;
import kn.uni.util.Vector2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class GhostAI {
    protected Color borderColor;

    public abstract Direction getNextDirection(ClassicPacmanGameState gameState);

    public abstract Vector2d targetPos(ClassicPacmanGameState gameState);

    public Direction nextDirection2(ClassicPacmanGameState gameState, Ghost ghost) {
        //find all valid tiles
        List<PacmanMapTile> possibleTiles =
                Arrays.stream(Direction.values())
                        .filter(d -> d != ghost.direction.opposite())
                        .map(Direction::toVector)
                        .map(vec -> ghost.currentTile.neighbors.get(vec))
                        .filter(Objects::nonNull)
                        .filter(tile -> PacmanMapTile.walkable.contains(tile.type))
                        .toList();

        gameState.map.tiles.forEach((vec,tile) -> tile.color= Color.black);
        possibleTiles.stream().forEach(tile -> tile.color = Color.blue);
        //sort tiles by distance to target, target = indexed at 0
        possibleTiles=
                possibleTiles.stream()
                        .sorted((a, b) -> {
                            Vector2d aVec = a.pos.subtract(ghost.ai.targetPos(gameState));
                            Vector2d bVec = b.pos.subtract(ghost.ai.targetPos(gameState));
                            double aDist = aVec.lenght();
                            double bDist = bVec.lenght();
                            if (aDist == bDist) {
                                return 0;
                            } else if (aDist < bDist) {
                                return -1;
                            } else {
                                return 1;
                            }
                        })
                .toList();
        //get direction of target tile
        Vector2d goTo = possibleTiles.get(0).pos.subtract(ghost.currentTile.pos);
        Direction newDirection = goTo.divide(goTo.lenght()).toDirection();
        ghost.direction = newDirection;
        return newDirection;
    }

    public List<Vector2d> getPacmanPos(ClassicPacmanGameState gameState) {
        List<Vector2d> pacPos = new ArrayList<>();
        gameState.gameObjects.stream()
                .filter(o -> o instanceof PacmanObject)
                .map(o -> (PacmanObject) o)
                .forEach(o -> pacPos.add(o.pos));
        return pacPos;
    }
}
