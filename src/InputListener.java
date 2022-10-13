import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputListener{

    private static InputListener instance;
    private static int subId = 0;

    Thread thread;
    Map<Integer, InputSubscriber> subscribers = new HashMap<>();
    Map<Controller, Player> controllerPlayerMap = new HashMap<>();

    private InputListener(){
        thread = new Thread(() -> {
            List<Controller> controllers = Arrays.stream(ControllerEnvironment.getDefaultEnvironment().getControllers())
                    .filter(controller -> controller.getName().contains("Generic   USB  Joystick"))
                    .toList();
            if (controllers.size() != 2) throw new RuntimeException();

            controllerPlayerMap.put(controllers.get(0), Player.player_one);
            controllerPlayerMap.put(controllers.get(1), Player.player_two);

            while (true) {
                controllers.forEach(controller -> {
                    controller.poll();
                    Event e = new Event();
                    EventQueue eq = controller.getEventQueue();
                    while (eq.getNextEvent(e)) {
                        if (e.getComponent().getName().equals("Z Axis")) continue;
                        notify(new Input(getKey(e), getState(e), controllerPlayerMap.get(controller)));
                    }
                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    private State getState(Event e){
        if (Math.abs(e.getValue()) < 0.5) return State.none;
        return e.getValue() > 0 ? State.down : State.up;
    }

    private Key getKey(Event e){
        return switch (e.getComponent().getName()){
            case "Y Axis" -> Key.vertical;
            case "X Axis" -> Key.horizontal;
            case "Button 1" -> Key.A;
            case "Button 2" -> Key.B;
            case "Button 3" -> Key.C;
            case "Button 4" -> Key.D;
            case "Button 5" -> Key.X;
            case "Button 6" -> Key.Y;
            default -> null;
        };
    }

    public void start(){
        thread.start();
    }

    private void notify(Input input){
        subscribers.forEach((id, sub) -> sub.handle(input));
    }

    public int subscribe(InputSubscriber inputSubscriber){
        subscribers.put(subId, inputSubscriber);
        return subId++;
    }

    public void unsubscribe(int id){
        subscribers.remove(id);
    }

    public static InputListener getInstance(){
        if (instance == null) {
            instance = new InputListener();
        }
        return instance;
    }

    interface InputSubscriber {

        void handle(Input input);

    }

    record Input(Key key, State state, Player player){}
    enum Key{vertical, horizontal, A, B, C, D, X, Y}
    enum State{up, down, none}
    enum Player{player_one, player_two}

}
