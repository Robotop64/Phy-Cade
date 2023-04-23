package kn.uni.games.classic.pacman.game.internal.tracker;

import java.util.ArrayList;
import java.util.List;

public class DebugManager
{
  public List <Info> infos;

  public DebugManager ()
  {
    infos = new ArrayList <>();
  }

  public void addInfo (String name, List <String> tags, Object value)
  {
    infos.add(new Info(name, tags, value));
  }

  public void removeInfo (String name)
  {
    infos.removeIf(info -> info.name.equals(name));
  }

  public void remove (String tagged)
  {
    infos.removeIf(info -> info.tags.stream().anyMatch(tag -> tag.equals(tagged)));
  }

  public List <Info> getInfoNamed (String name)
  {
    return List.of(infos.stream().filter(info -> info.name.equals(name)).toArray(Info[]::new));
  }

  public List <Info> getInfoTagged (String tagged)
  {
    return List.of(infos.stream().filter(info -> info.tags.stream().anyMatch(tag -> tag.equals(tagged))).toArray(Info[]::new));
  }

  public record Info(String name, List <String> tags, Object value) { }
}
