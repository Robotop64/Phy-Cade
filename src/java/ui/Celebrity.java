package ui;

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
    System.out.println(id + " wants to sub");
    System.out.println("all subs: " + subscribers);
    System.out.println("sub queue: " + subscribersQueue);

    if (!locked)
    {
      subscribers.putAll(subscribersQueue);
      subscribersQueue.clear();
    }
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
