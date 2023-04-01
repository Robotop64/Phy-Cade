package kn.uni.ui;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class Celebrity <Type>
{

  private static int subId = 0;
  ConcurrentMap <Integer, Fan <Type>> subscribersQueue = new ConcurrentHashMap <>();
  ConcurrentMap <Integer, Fan <Type>> subscribers      = new ConcurrentHashMap <>();

  boolean locked = false;

  public void post (Type type)
  {
    locked = true;
    subscribers.forEach((id, fan) -> fan.handle(type));
    locked = false;
    subscribers.putAll(subscribersQueue);
    subscribersQueue.clear();
  }

  public int subscribe (Fan <Type> fan)
  {
    int id = subId;
    subId++;
    subscribersQueue.put(id, fan);

    System.out.println("╭─────┤ Subscription ├────────────────────────────────────────────────────────╮");
    System.out.println("⎢ -> Requested by: " + fan.getClass().getSimpleName().split("\\$")[0] + " (id:" + id + ")");
    System.out.println("⎢ -> All current subs: " + getSubs());
    System.out.println("⎢ -> Queued subs: " + getQueue());
    System.out.println("╰─────────────────────────────────────────────────────────────────────────────╯");

    if (!locked)
    {
      subscribers.putAll(subscribersQueue);
      subscribersQueue.clear();
    }
    return id;
  }

  public void unsubscribe (int id)
  {
    Fan <Type> fan = subscribers.get(id);
    subscribers.remove(id);
    System.out.println(fan.getClass().getSimpleName() + "(" + id + ")" + " unsubbed");
    System.out.println("all subs: " + getSubs());
  }

  private String getSubs ()
  {
    StringBuilder subs = new StringBuilder();

    for (Fan <Type> f : subscribers.values())
    {

      subs.append(f.getClass().getSimpleName().split("\\$")[0]).append(", ");
    }

    return "{" + subs + "}";
  }

  private String getQueue ()
  {
    StringBuilder subs = new StringBuilder();

    for (Fan <Type> f : subscribersQueue.values())
    {
      subs.append(f.getClass().getSimpleName().split("\\$")[0]);
      if (subscribersQueue.values().stream().toList().indexOf(f) < subscribersQueue.values().size() - 1)
      {
        subs.append(", ");
      }
    }

    return "{" + subs + "}";
  }

  public interface Fan <Type>
  {
    void handle (Type type);
  }

}
