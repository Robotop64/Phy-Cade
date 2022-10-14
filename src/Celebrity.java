import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class Celebrity<Type> {

    private static int subId = 0;
    ConcurrentMap<Integer, Fan<Type>> subscribers = new ConcurrentHashMap<>();

    public void post(Type type){
        subscribers.forEach((id, fan) -> fan.handle(type));
    }

    public int subscribe(Fan<Type> fan){
        subscribers.put(subId, fan);
        return subId++;
    }

    public void unsubscribe(int id){
        subscribers.remove(id);
    }

    interface Fan<Type>{
        void handle(Type type);
    }

}
