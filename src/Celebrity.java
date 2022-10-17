import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class Celebrity <Type>
{

  private static int subId = 0;
  ConcurrentMap <Integer, Fan <Type>> subscribers = new ConcurrentHashMap <>();

  public void post (Type type)
  {
    subscribers.forEach((id, fan) -> fan.handle(type));
  }

  public int subscribe (Fan <Type> fan)
  {
    int id = subId;
    subId++;
    subscribers.put(id, fan);
    System.out.println(id + " subbed");
    System.out.println("all subs: " + subscribers);
    return id;
  }

  public void unsubscribe (int id)
  {
    subscribers.remove(id);
    System.out.println(id + " unsubbed");
    System.out.println("all subs: " + subscribers);
  }

  interface Fan <Type>
  {
    void handle (Type type);
  }

}
