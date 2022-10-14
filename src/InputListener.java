import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputListener extends Celebrity<InputListener.Input>{

    private static InputListener instance;

    Thread thread;
    Map<Controller, Player> controllerPlayerMap = new HashMap<>();

    private InputListener(){
        thread = new Thread(() -> {
            List<Controller> controllers = Arrays.stream(ControllerEnvironment.getDefaultEnvironment().getControllers())
                    .filter(controller -> controller.getName().contains("Generic   USB  Joystick"))
                    .toList();
            if (controllers.size() != 2) throw new RuntimeException();

            controllerPlayerMap.put(controllers.get(0), Player.player_one);
            controllerPlayerMap.put(controllers.get(1), Player.player_two);

            handle_events(controllers, true);

            while (true) {
                handle_events(controllers, false);

            }
        });
    }

    private void handle_events(List<Controller> controllers, boolean void_input) {
        controllers.forEach(controller -> {
            controller.poll();
            Event e = new Event();
            EventQueue eq = controller.getEventQueue();
            while (eq.getNextEvent(e)) {
                if (void_input) continue;
                if (e.getComponent().getName().equals("Z Axis")) continue;
                post(new Input(getKey(e), getState(e), controllerPlayerMap.get(controller)));
            }
        });
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    public static InputListener getInstance(){
        if (instance == null) {
            instance = new InputListener();
        }
        return instance;
    }

    record Input(Key key, State state, Player player){}
    enum Key{vertical, horizontal, A, B, C, D, X, Y}
    enum State{up, down, none}
    enum Player{player_one, player_two}

}
