import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.function.Consumer;

public class onScreenKeyboard extends JPanel
{

  public static Consumer <String> target;

  private boolean shifted = false;

  private enum LayoutHead
  { num, numDE, extra }

  private enum LayoutBody
  { extra2, DE, GREEK }

  private enum Languages
  { DE, GREEK }

  private final Map <Languages, Layout>  LayoutLangStringMap = Map.of(
      Languages.DE, new Layout(LayoutHead.numDE, LayoutBody.DE),
      Languages.GREEK, new Layout(LayoutHead.num, LayoutBody.GREEK));
  private final Map <LayoutHead, String> layoutHeadStringMap = Map.of(
      LayoutHead.num, "1234567890 ",
      LayoutHead.numDE, "1234567890ß",
      LayoutHead.extra, "!\"§$%&/()=?");
  private final Map <LayoutBody, String> layoutBodyStringMap = Map.of(
      LayoutBody.extra2, "",
      LayoutBody.DE, "QWERTZUIOPÜASDFGHJKLÖÄYXCVBNM".toLowerCase(),
      LayoutBody.GREEK, "  ΕΡΤΥΘΙΟΠ ΑΣΔΦΓΗΞΚΛ  ΖΧΨΩΒΝΜ".toLowerCase());

  private record Layout(LayoutHead layoutHead, LayoutBody layoutBody)
  { }

  private Layout activeLayout = new Layout(LayoutHead.numDE, LayoutBody.DE);

  private record Language(Languages language)
  { }

  private Language activeLanguage = new Language(Languages.DE);

  private record Point(int y, int x)
  { }

  private Point              activeKey = new Point(3, 5);
  private Map <Point, Point> keyMap    = Map.of(
      //shift
      new Point(3, 1), new Point(3, 0),
      //backspace
      new Point(3, 10), new Point(3, 8),
      //extra
      new Point(4, 1), new Point(4, 0),
      //space
      new Point(4, 4), new Point(4, 2),
      new Point(4, 5), new Point(4, 2),
      new Point(4, 6), new Point(4, 2),
      //enter
      new Point(4, 10), new Point(4, 5)
  );

  private              char[][]     keyRows         = new char[3][];
  private              char[]       topRow          = new char[11];
  private final        pmButton[][] buttons         = new pmButton[5][];
  private static final int          buttonBaseSize  = 80;
  private static final int          buttonBuffer    = 20;
  private static final int          buttonOffset    = buttonBaseSize + buttonBuffer;
  private static final int          maxButtonsInRow = 11;
  private static final int          origin          = Gui.frame_width / 2 - ( maxButtonsInRow * buttonOffset ) / 2;

  public onScreenKeyboard ()
  {
    //initialisation
    setBackground(Color.black);
    setLayout(null);
    createButtons();
    setButtonLayout(activeLayout);
    toggleKey(activeKey);
    //end of initialisation


  }

  /*
   * Used to tell the keyboard where it should write to
   */
  public void setTarget (Consumer <String> target)
  {
    onScreenKeyboard.target = target;
  }

  private void createKeyStrings (String top, String mid)
  {
    char[] arr  = mid.toCharArray();
    char[] arr2 = top.toCharArray();

    int[] l = { 11, 11, 7 };
    for (int i = 0; i < 3; i++)
    {
      keyRows[i] = new char[l[i]];
    }

    for (int j = 0; j < 3; j++)
    {
      System.arraycopy(arr, j * 11, keyRows[j], 0, keyRows[j].length);
    }

    System.arraycopy(arr2, 0, topRow, 0, topRow.length);

  }

  private void createButtons ()
  {
    //create button array
    int[] l = { 11, 11, 11, 9, 6 };
    for (int i = 0; i < 5; i++)
    {
      buttons[i] = new pmButton[l[i]];
    }

    //button row 0-2
    for (int j = 0; j < 3; j++)
    {
      for (int i = 0; i < buttons[j].length; i++)
      {
        buttons[j][i] = createPrintableButton("", "", 1, i, j);
      }
    }
    //button shift
    buttons[3][0] = createActionButton("\uD83E\uDC39", this::toggleShift, 2.3, 0, 3);
    buttons[3][0].setFont(new Font("Ariel", Font.PLAIN, 64));
    //buttons Y-M
    for (int i = 1; i < buttons[3].length - 1; i++)
    {
      buttons[3][i] = createPrintableButton("", "", 1, i + 1, 3);
    }
    //button remove
    buttons[3][8] = createPrintableButton("\uD83E\uDC44", "", 2.3, 9, 3);
    buttons[3][8].setFont(new Font("Ariel", Font.PLAIN, 40));
    //button extra
    buttons[4][0] = createActionButton("!#1", this::extraLayout, 2.3, 0, 4);
    //button left text
    buttons[4][1] = createPrintableButton("/", "/", 1, 2, 4);
    //button space bar
    buttons[4][2] = createPrintableButton("-----", " ", 4.75, 3, 4);
    //button right text
    buttons[4][3] = createPrintableButton(".", ".", 1, 7, 4);
    //button lang
    buttons[4][4] = createActionButton("@", this::selectLang, 1, 8, 4);
    //button enter
    buttons[4][5] = createPrintableButton("↲", "", 2.3, 9, 4);
    buttons[4][5].setFont(new Font("Ariel", Font.BOLD, 50));
    //add buttons to frame
    for (int j = 0; j < 5; j++)
    {
      for (int i = 0; i < buttons[j].length; i++)
      {
        buttons[j][i].isSelected = false;
        add(buttons[j][i]);
      }
    }
  }

  /*
   * Used to change the active layout
   * @param layout - the new layout
   */
  private void setButtonLayout (Layout layout)
  {
    activeLayout = layout;
    createKeyStrings(layoutHeadStringMap.get(activeLayout.layoutHead), layoutBodyStringMap.get(activeLayout.layoutBody));
    for (int i = 0; i < 11; i++)
    {
      String c = Character.toString(this.topRow[i]);
      buttons[0][i].setText(shifted ? c.toUpperCase() : c);
      buttons[0][i].clearActions();
      buttons[0][i].addAction(() -> target.accept(shifted ? c.toUpperCase() : c));

    }
    for (int j = 0; j < 2; j++)
    {
      for (int i = 0; i < buttons[j + 1].length; i++)
      {
        String c = Character.toString(keyRows[j][i]);
        buttons[j + 1][i].setText(shifted ? c.toUpperCase() : c);
        buttons[j + 1][i].clearActions();
        buttons[j + 1][i].addAction(() -> target.accept(shifted ? c.toUpperCase() : c));
      }
    }
    for (int i = 1; i < buttons[3].length - 1; i++)
    {
      String c = Character.toString(keyRows[2][i - 1]);
      buttons[3][i].setText(shifted ? c.toUpperCase() : c);
      buttons[3][i].clearActions();
      buttons[3][i].addAction(() -> target.accept(shifted ? c.toUpperCase() : c));
    }

  }

  private void setActiveLanguage (Languages language)
  {

    this.activeLanguage = new Language(language);
  }

  /*
   *Toggles the buttons from upper to lower case or reversed
   */
  private void toggleShift ()
  {
    this.shifted = !this.shifted;
    if (this.shifted)
    {
      System.out.println("a");
      setButtonLayout(new Layout(LayoutHead.extra, activeLayout.layoutBody));
    }
    else
    {
      if (activeLanguage.language.equals(Languages.DE))
      {
        System.out.println("b");
        setButtonLayout(new Layout(LayoutHead.numDE, activeLayout.layoutBody));
      }
      else if (activeLanguage.language.equals(Languages.GREEK))
      {
        setButtonLayout(new Layout(LayoutHead.num, activeLayout.layoutBody));
      }
    }
  }

  private void selectLang ()
  {

  }

  private void extraLayout ()
  {

  }

  private Point computeShiftedButton (Point coordinate)
  {
    return ( coordinate.x == 0 ) ? coordinate : keyMap.getOrDefault(coordinate, new Point(coordinate.y, computeShiftedButton(new Point(coordinate.y, coordinate.x - 1)).x + 1));
  }

  private void toggleKey (Point pos)
  {
    pos = computeShiftedButton(pos);
    buttons[pos.y][pos.x].isSelected = !buttons[pos.y][pos.x].isSelected;
    buttons[pos.y][pos.x].update();
  }

  private void moveActive (int directions)
  {
  }

  private pmButton createPrintableButton (String text, String key, double size, int x, int y)
  {
    return createActionButton(text, () -> target.accept(shifted ? key.toUpperCase() : key), size, x, y);
  }

  private pmButton createActionButton (String text, Runnable action, double size, int x, int y)
  {
    pmButton temp = new pmButton(text);
    temp.addAction(action);
    temp.setSize((int) Math.round(buttonBaseSize * size), buttonBaseSize);
    temp.setLocation(origin + x * buttonOffset, 375 + origin + y * buttonOffset);
    temp.isSelected = false;
    temp.update();

    return temp;
  }


}
