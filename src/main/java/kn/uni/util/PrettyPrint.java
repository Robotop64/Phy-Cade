package kn.uni.util;

import java.util.concurrent.ConcurrentLinkedDeque;

public class PrettyPrint
{
  public static Type                         currentType     = Type.Message;
  public static Progress                     currentProgress = Progress.Header;
  public static int                          lineLength      = 80;
  public static ConcurrentLinkedDeque <Type> hirarchy        = new ConcurrentLinkedDeque <>();

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

  public static void startGroup (Type type, String message)
  {
    currentType = type;
    hirarchy.add(type);
    switch (type)
    {
      case Message -> startMessage(message);
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
      //      case Info -> endInfo();
      //      case Hint -> endHint();
      //      case Warning -> endWarning();
      //      case Error -> endError();
      //      case Debug -> endDebug();
      //      case Trace -> endTrace();
    }
  }

  public static void bullet (String message)
  {
    String border = " ";

    switch (currentType)
    {
      case Message -> border = "│";
      case Info -> border = " ";
      case Hint -> border = " ";
      case Warning -> border = " ";
      case Error -> border = " ";
      case Debug -> border = " ";
      case Trace -> border = " ";
    }

    int messageLength    = message.length();
    int decorationLength = 6;
    int bufferLength     = lineLength - messageLength - decorationLength - ( hirarchy.size() - 1 ) * 2 * 2;

    StringBuilder out = new StringBuilder();

    if (hirarchy.size() > 1)
      out.append(( border + " " ).repeat(Math.max(0, hirarchy.size() - 1)));

    out.append(border);
    out.append(" -> ");
    out.append(message);
    out.append(" ".repeat(Math.max(0, bufferLength - 1)));
    out.append(border);

    if (hirarchy.size() > 1)
      out.append(( " " + border ).repeat(Math.max(0, hirarchy.size() - 1)));

    System.out.println(out);
  }

  private static void startMessage (String message)
  {
    int    messageLength    = message.length();
    int    decorationLength = 4;
    int    leftBufferLength = 2;
    int    bufferLength     = lineLength - messageLength - decorationLength - leftBufferLength;
    int    overShoot        = ( messageLength + leftBufferLength + bufferLength + decorationLength ) - lineLength;
    String bufferLeft       = "─".repeat(Math.max(0, leftBufferLength));
    String bufferRight      = "─".repeat(Math.max(0, bufferLength - ( overShoot + 1 ) - ( hirarchy.size() - 1 ) * 2 * 2));

    StringBuilder out = new StringBuilder();

    if (hirarchy.size() > 1)
      out.append(( "│" + " " ).repeat(Math.max(0, hirarchy.size() - 1)));

    out.append("╭");
    out.append(bufferLeft);
    out.append("┤");
    out.append(message);
    out.append("├");
    out.append(bufferRight);
    out.append("╮");

    if (hirarchy.size() > 1)
      out.append(( " " + "│" ).repeat(Math.max(0, hirarchy.size() - 1)));

    System.out.println(out);
  }

  private static void endMessage ()
  {
    StringBuilder out = new StringBuilder();

    if (hirarchy.size() > 1)
      out.append(( "│" + " " ).repeat(Math.max(0, hirarchy.size() - 1)));

    out.append("╰");
    out.append("─".repeat(Math.max(0, lineLength - 3 - ( hirarchy.size() - 1 ) * 2 * 2)));
    out.append("╯");

    if (hirarchy.size() > 1)
      out.append(( " " + "│" ).repeat(Math.max(0, hirarchy.size() - 1)));

    System.out.println(out);
    hirarchy.removeLast();
  }

  public enum Type
  { Message, Info, Hint, Warning, Error, Debug, Trace }

  public enum Progress
  { Header, Body, Footer }
}
