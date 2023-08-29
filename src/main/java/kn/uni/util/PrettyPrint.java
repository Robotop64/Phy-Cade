package kn.uni.util;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PrettyPrint
{


  private static final Map <Type, String[]>         typeToBorder    = Map.of(
      Type.Message, new String[]{ "│", "│", "─", "─", "╭", "╮", "╰", "╯", "┤", "├" },
      Type.Info, new String[]{ " ", " " },
      Type.Hint, new String[]{ " ", " " },
      Type.Warning, new String[]{ " ", " " },
      Type.Error, new String[]{ " ", " " },
      Type.Debug, new String[]{ " ", " " },
      Type.Trace, new String[]{ " ", " " },
      Type.Event, new String[]{ "║", "║", "═", "═", "╔", "╗", "╚", "╝", "╣", "╠" }
  );
  public static        Type                         currentType     = Type.Message;
  public static        Progress                     currentProgress = Progress.Header;
  public static        int                          lineLength      = 80;
  public static        ConcurrentLinkedDeque <Type> hirarchy        = new ConcurrentLinkedDeque <>();

  public static void announce (String message)
  {
    int    messageLength    = message.length();
    int    decorationLength = 6;
    int    bufferLength     = ( lineLength - messageLength - decorationLength ) / 2;
    int    overShoot        = ( messageLength + 2 * bufferLength + decorationLength ) - lineLength;
    String bufferLeft       = "=".repeat(Math.max(0, bufferLength));
    String bufferRight      = "=".repeat(Math.max(0, bufferLength - ( overShoot + 1 )));

    StringBuilder out = new StringBuilder();
    out.append("||");
    out.append(bufferLeft);
    out.append("<");
    out.append(message);
    out.append(">");
    out.append(bufferRight);
    out.append("||");

    System.out.println(out);
  }

  public static void shout (String message)
  {

  }

  public static void empty ()
  {
    StringBuilder out = new StringBuilder();

    if (hirarchy.size() >= 1)
      out.append(( "│" + " " ).repeat(Math.max(0, hirarchy.size())));

    out.append(" ".repeat(Math.max(0, lineLength - 1 - ( hirarchy.size() - 1 ) * 2 * 2) - 4));

    if (hirarchy.size() >= 1)
      out.append(( " " + "│" ).repeat(Math.max(0, hirarchy.size())));

    System.out.println(out);
  }

  //region group
  public static void startGroup (Type type, String message)
  {
    currentType = type;
    hirarchy.add(type);
    switch (type)
    {
      case Message -> startMessage(message);
      case Event -> startEvent(message);
      //      case Info -> startInfo(message);
      //      case Hint -> startHint(message);
      //      case Warning -> startWarning(message);
      //      case Error -> startError(message);
      //      case Debug -> startDebug(message);
      //      case Trace -> startTrace(message);
    }
  }

  public static void endGroup ()
  {
    switch (currentType)
    {
      case Message -> endMessage();
      case Event -> endEvent();
      //      case Info -> endInfo();
      //      case Hint -> endHint();
      //      case Warning -> endWarning();
      //      case Error -> endError();
      //      case Debug -> endDebug();
      //      case Trace -> endTrace();
    }
  }
  //endregion

  public static void bullet (String message)
  {
    String[] border = typeToBorder.get(currentType);

    int messageLength    = message.length();
    int decorationLength = 6;
    int bufferLength     = lineLength - messageLength - decorationLength - ( hirarchy.size() - 1 ) * 2 * 2;

    StringBuilder out = new StringBuilder();

    if (hirarchy.size() > 1)
      out.append(( border[0] + " " ).repeat(Math.max(0, hirarchy.size() - 1)));

    out.append(border[0]);
    out.append(" -> ");
    out.append(message);
    out.append(" ".repeat(Math.max(0, bufferLength - 1)));
    out.append(border[1]);

    if (hirarchy.size() > 1)
      out.append(( " " + border[1] ).repeat(Math.max(0, hirarchy.size() - 1)));

    System.out.println(out);
  }

  public static void subBullet (int depth, String message)
  {
    String[] border = typeToBorder.get(currentType);

    int messageLength    = message.length();
    int decorationLength = 6;
    int bufferLength     = lineLength - messageLength - decorationLength - ( hirarchy.size() - 1 ) * 2 * 2;

    StringBuilder out = new StringBuilder();

    if (hirarchy.size() > 1)
      out.append(( border[0] + " " ).repeat(Math.max(0, hirarchy.size() - 1)));

    out.append(border[0]);
    out.append(" ").append("-".repeat(depth)).append("> ");
    out.append(message);
    out.append(" ".repeat(Math.max(0, bufferLength - depth)));
    out.append(border[1]);

    if (hirarchy.size() > 1)
      out.append(( " " + border[1] ).repeat(Math.max(0, hirarchy.size() - 1)));

    System.out.println(out);
  }

  //region Message
  private static void startMessage (String message)
  {
    String[] border           = typeToBorder.get(Type.Message);
    int      messageLength    = message.length();
    int      decorationLength = 4;
    int      leftBufferLength = 2;
    int      bufferLength     = lineLength - messageLength - decorationLength - leftBufferLength;
    int      overShoot        = ( messageLength + leftBufferLength + bufferLength + decorationLength ) - lineLength;
    String   bufferLeft       = border[2].repeat(Math.max(0, leftBufferLength));
    String   bufferRight      = border[2].repeat(Math.max(0, bufferLength - ( overShoot + 1 ) - ( hirarchy.size() - 1 ) * 2 * 2));

    StringBuilder out = new StringBuilder();

    if (hirarchy.size() > 1)
      out.append(( border[0] + " " ).repeat(Math.max(0, hirarchy.size() - 1)));

    out.append(border[4]);
    out.append(bufferLeft);
    out.append(border[8]);
    out.append(message);
    out.append(border[9]);
    out.append(bufferRight);
    out.append(border[5]);

    if (hirarchy.size() > 1)
      out.append(( " " + border[1] ).repeat(Math.max(0, hirarchy.size() - 1)));

    System.out.println(out);
  }

  private static void endMessage ()
  {
    String[]      border = typeToBorder.get(Type.Message);
    StringBuilder out    = new StringBuilder();

    if (hirarchy.size() > 1)
      out.append(( border[0] + " " ).repeat(Math.max(0, hirarchy.size() - 1)));

    out.append(border[6]);
    out.append(border[3].repeat(Math.max(0, lineLength - 3 - ( hirarchy.size() - 1 ) * 2 * 2)));
    out.append(border[7]);

    if (hirarchy.size() > 1)
      out.append(( " " + border[1] ).repeat(Math.max(0, hirarchy.size() - 1)));

    System.out.println(out);
    hirarchy.removeLast();
  }
  //endregion

  //region event
  private static void startEvent (String message)
  {
    String[] border           = typeToBorder.get(Type.Event);
    int      messageLength    = message.length();
    int      decorationLength = 4;
    int      leftBufferLength = 2;
    int      bufferLength     = lineLength - messageLength - decorationLength - leftBufferLength;
    int      overShoot        = ( messageLength + leftBufferLength + bufferLength + decorationLength ) - lineLength;
    String   bufferLeft       = border[2].repeat(Math.max(0, leftBufferLength));
    String   bufferRight      = border[2].repeat(Math.max(0, bufferLength - ( overShoot + 1 ) - ( hirarchy.size() - 1 ) * 2 * 2));

    StringBuilder out = new StringBuilder();

    if (hirarchy.size() > 1)
      out.append(( border[0] + " " ).repeat(Math.max(0, hirarchy.size() - 1)));

    out.append(border[4]);
    out.append(bufferLeft);
    out.append(border[8]);
    out.append(message);
    out.append(border[9]);
    out.append(bufferRight);
    out.append(border[5]);

    if (hirarchy.size() > 1)
      out.append(( " " + border[1] ).repeat(Math.max(0, hirarchy.size() - 1)));

    System.out.println(out);
  }

  private static void endEvent ()
  {
    String[]      border = typeToBorder.get(Type.Event);
    StringBuilder out    = new StringBuilder();

    if (hirarchy.size() > 1)
      out.append(( border[0] + " " ).repeat(Math.max(0, hirarchy.size() - 1)));

    out.append(border[6]);
    out.append(border[3].repeat(Math.max(0, lineLength - 3 - ( hirarchy.size() - 1 ) * 2 * 2)));
    out.append(border[7]);

    if (hirarchy.size() > 1)
      out.append(( " " + border[1] ).repeat(Math.max(0, hirarchy.size() - 1)));

    System.out.println(out);
    hirarchy.removeLast();
  }
  //endregion

  public enum Type
  { Message, Info, Hint, Warning, Error, Debug, Trace, Event }

  public enum Progress
  { Header, Body, Footer }
}
