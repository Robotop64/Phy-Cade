package kn.uni.util;

import java.util.ArrayList;

public class ConcurrentLayeredList <T, E extends Enum <E>> extends LayeredList <T, E>
{
  private final ArrayList <TombStone<T,E>> corpses;

  public ConcurrentLayeredList (Class <E> enumClass)
  {
    super(enumClass);

    corpses = new ArrayList <>();
  }

  public ArrayList <T> get(E layer)
  {
    ArrayList<T> out = super.get(layer);

    corpses.forEach(
        tombStone -> {
          if (tombStone.layer() == layer)
            out.remove(tombStone.obj());
        }
    );

    return out;
  }

  public T get(int index)
  {
    T out = super.list.get(index);

    if (corpses.stream().anyMatch(tombStone -> tombStone.obj() ==out))
      return null;

    return out;
  }

  public void remove (E layer, T obj)
  {
    corpses.add(new TombStone<>(layer, obj));
  }

  public void cleanUp ()
  {
    for (TombStone<T,E> obj : corpses)
    {
      super.remove(obj.layer(), obj.obj());
    }
    corpses.clear();
  }

  private record TombStone <T, E>(E layer, T obj){}
}
