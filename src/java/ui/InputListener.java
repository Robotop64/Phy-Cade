package ui;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Keyboard;
import ui.InputListener.Input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InputListener extends Celebrity <Input>
{

  private static InputListener instance;

  Thread                   thread;
  Map <Controller, Player> controllerPlayerMap = new HashMap <>();

  private Map <Component.Identifier.Key, State> keyStateMap = new HashMap <>();

  private final Map <Component.Identifier.Key, InputState> inputMap = Map.of(
    Component.Identifier.Key.W, new InputState(Key.vertical, State.up),
    Component.Identifier.Key.S, new InputState(Key.vertical, State.down),
    Component.Identifier.Key.A, new InputState(Key.horizontal, State.up),
    Component.Identifier.Key.D, new InputState(Key.horizontal, State.down),
    Component.Identifier.Key.ESCAPE, new InputState(Key.B, State.down),
    Component.Identifier.Key.SPACE, new InputState(Key.A, State.down)
  );

  private InputListener ()
  {
    inputMap.keySet().forEach(key -> keyStateMap.put(key, State.none));

    thread = new Thread(() ->
    {
      List <Controller> controllers = Arrays.stream(ControllerEnvironment.getDefaultEnvironment().getControllers())
                                            .filter(controller -> controller.getName().contains("Generic   USB  Joystick"))
                                            .toList();


      if (controllers.size() == 2)
      {
        controllerPlayerMap.put(controllers.get(0), Player.playerOne);
        controllerPlayerMap.put(controllers.get(1), Player.playerTwo);

        handleJoystickEvents(controllers, true);

        while (true) handleJoystickEvents(controllers, false);
      }
      else
      {
        // if we don't detect enough joysticks, revert to keyboards
        System.out.println("keyboard :(");
        final List <Controller> keyboards = Arrays.stream(ControllerEnvironment.getDefaultEnvironment().getControllers())
                                                  .filter(controller -> controller.getType().equals(Controller.Type.KEYBOARD))
                                                  .toList();
        while (true)
        {
          keyboards.forEach(Controller::poll);
          Map <Component.Identifier.Key, State> newState = new HashMap <>();
          keyStateMap.keySet()
                     .stream()
                     .map(key -> Map.entry(key, keyboards.stream()
                                                         .map(keyboard -> ((Keyboard)keyboard))
                                                         .map(keyboard -> keyboard.isKeyDown(key))
                                                         .reduce(Boolean::logicalOr)
                                                         .get() ? State.down : State.none))
                     .forEach(keyStateEntry -> newState.put(keyStateEntry.getKey(), keyStateEntry.getValue()));

          List <Input> inputs = keyStateMap.keySet()
                                           .stream()
                                           .filter(key -> !keyStateMap.get(key).equals(newState.get(key)))
                                           .map(inputMap::get)
                                           .map(in -> new Input(in.key, in.state, Player.playerOne)).toList();

          newState.keySet().stream().filter(k -> !newState.get(k).equals(keyStateMap.get(k)))
                  .map(key -> new Input(
                    inputMap.get(key).key,
                    newState.get(key).equals(State.none) ? State.none : inputMap.get(key).state,
                    Player.playerOne))
                  .forEach(this::post);

          keyStateMap = newState;


          try
          {
            Thread.sleep(10);
          }
          catch (InterruptedException e)
          {
            throw new RuntimeException(e);
          }
        }
      }
    });
  }

  private void handleJoystickEvents (List <Controller> controllers, boolean void_input)
  {
    handleEvents(controllers, controller ->
    {
      controller.poll();
      Event      e  = new Event();
      EventQueue eq = controller.getEventQueue();
      while (eq.getNextEvent(e))
      {
        if (void_input) continue;
        if (e.getComponent().getName().equals("Z Axis")) continue;
        post(new Input(getKey(e), getState(e), controllerPlayerMap.get(controller)));
      }
    });
  }

  private void handleEvents (List <Controller> controllers, Consumer <Controller> handling)
  {
    controllers.forEach(handling);
    try
    {
      Thread.sleep(10);
    }
    catch (InterruptedException e)
    {
      throw new RuntimeException(e);
    }
  }

  private State getState (Event e)
  {
    if (Math.abs(e.getValue()) < 0.5) return State.none;
    return e.getValue() > 0 ? State.down : State.up;
  }

  private Key getKey (Event e)
  {
    // todo map this
    return switch (e.getComponent().getName())
      {
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

  public void start () {thread.start();}

  public static InputListener getInstance ()
  {
    if (instance == null)
    {
      instance = new InputListener();
    }
    return instance;
  }

  record Input(Key key, State state, Player player) {}

  enum Key
  { vertical, horizontal, A, B, C, D, X, Y }

  enum State
  { up, down, none }

  enum Player
  { playerOne, playerTwo }

  record InputState(Key key, State state) {}

  ;

}
