package kn.uni.ui;

import kn.uni.util.PrettyPrint;

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

    PrettyPrint.startGroup(PrettyPrint.Type.Message, "Subscription");
    PrettyPrint.bullet("Requested by: " + fan.getClass().getSimpleName().split("\\$")[0] + " (id:" + id + ")");
    PrettyPrint.bullet("All current subs: " + getSubs());
    PrettyPrint.bullet("Queued subs: " + getQueue());
    PrettyPrint.endGroup();

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

    PrettyPrint.startGroup(PrettyPrint.Type.Message, "Unsubscription");
    PrettyPrint.bullet("Requested by: " + fan.getClass().getSimpleName().split("\\$")[0] + " (id:" + id + ")");
    PrettyPrint.bullet("All current subs: " + getSubs());
    PrettyPrint.endGroup();
  }

  private String getSubs ()
  {
    StringBuilder subs = new StringBuilder();

    for (int i = 0; i < subscribers.size(); i++)
    {
      subs.append(subscribers.get(i).getClass().getSimpleName().split("\\$")[0]);
      if (i < subscribers.size() - 1)
        subs.append(", ");
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
