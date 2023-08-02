package kn.uni.games.classic.pacman.game.internal.tracker;

import kn.uni.games.classic.pacman.game.entities.AdvPacManEntity;
import kn.uni.games.classic.pacman.game.entities.Entity;
import kn.uni.games.classic.pacman.game.entities.Spawner;
import kn.uni.games.classic.pacman.game.internal.GameEnvironment;
import kn.uni.games.classic.pacman.game.internal.objects.AdvGameObject;
import kn.uni.games.classic.pacman.game.internal.objects.AdvPlacedObject;
import kn.uni.games.classic.pacman.game.items.Item;
import kn.uni.games.classic.pacman.game.map.AdvPacManMap;
import kn.uni.util.ConcurrentLayeredList;
import kn.uni.util.Direction;
import kn.uni.util.fileRelated.Config.Config;

import java.util.ArrayList;
import java.util.List;

public class AdvGameState
{

  //region game environment
  public GameEnvironment                       env;
  public boolean                               running             = false;
  public boolean                               paused              = false;
  public long                                  currentTick;
  public long                                  lastTickTime;
  public long                                  gameStartTime;
  public ObjList <AdvGameObject>               objects             = new ObjList <>();
  public List <AdvPacManEntity>                players             = new ArrayList <>();
  public List <Direction>                      requestedDirections = new ArrayList <>();
  //endregion

  //region game stats
  public long score       = 0;
  public int  lives       ;
  public int  livesGained = 0;
  public int  level       = 1;
  public long time        = 0;
  //endregion

  //region trackers
  public boolean fruitSpawned = false;
  public int     pelletCount  = 0;
  public int     pelletsEaten = 0;
  public int     ghostStreak  = 0;
  //endregion

  public AdvGameState (GameEnvironment env)
  {
    this.env = env;

    //noinspection DataFlowIssue
    lives = (int) (double) Config.getCurrent("Gameplay/PacMan/StartLives");
  }

  public void addScore (long score)
  {
    long oldScore = this.score;
    long newScore = oldScore + score;
    this.score = newScore;
    //add a live if the score passed a multiple of 10000 (AdvGameConst.pointsToLife)
    if (newScore / AdvGameConst.pointsToLife > oldScore / AdvGameConst.pointsToLife)
    {
      livesGained++;
      lives++;
      env.gameScreen.setLives(lives);
    }
    env.gameScreen.setScore((int) this.score);
  }

  public void setLives (int lives)
  {
    this.lives = lives;
    env.gameScreen.setLives(lives);
  }

  public void addScaled(Layer type, AdvPlacedObject obj)
  {
    obj.absPos = obj.mapPos.multiply(AdvGameConst.tileSize);

    objects.add(type, obj);

    if (type != Layer.INTERNALS)
      env.updateLayer.set(type.ordinal(), true);
  }

  public void add(Layer type, AdvGameObject obj)
  {
    objects.add(type, obj);
    if (type != Layer.INTERNALS)
      env.updateLayer.set(type.ordinal(), true);
  }

  public void checkFruit ()
  {
    if (fruitSpawned)
      return;

    if (pelletsEaten >= pelletCount / 2)
    {
      fruitSpawned = true;

      objects.entities().stream()
            .filter(obj -> obj instanceof Spawner)
            .map(obj -> (Spawner) obj)
            .filter(spawner -> spawner.name.equals("FruitSpawn"))
            .forEach(Spawner::spawn);
    }
  }

  public void checkProgress ()
  {
    if (pelletsEaten == pelletCount)
    {
      env.reloadLevel();
    }
  }

  public void checkGameOver ()
  {
    if (lives <= 0)
    {
      env.stopGame();
    }
  }

  public enum Layer
  {
    BACKGROUND, INTERNALS, MAP, OBJECTS, ITEMS, ENTITIES, VFX, PHYSICS, INFORMATIONAL
  }

  public static class ObjList <AdvGameObject> extends ConcurrentLayeredList <AdvGameObject, Layer>
  {
    public ObjList ()
    {
      super(Layer.class);
    }

    public ArrayList <AdvPacManMap> maps ()
    {
      return get(Layer.MAP).stream().map(o -> (AdvPacManMap) o).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public ArrayList <AdvGameObject> objects ()
    {
      return get(Layer.OBJECTS).stream().collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public ArrayList <Item> items ()
    {
      return get(Layer.ITEMS).stream().map(o -> (Item) o).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public ArrayList <Entity> entities ()
    {
      return get(Layer.ENTITIES).stream().map(o -> (Entity) o).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
  }

}
