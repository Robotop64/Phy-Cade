package kn.uni.games.classic.pacman.screens;

import kn.uni.Gui;
import kn.uni.ui.InputListener;
import kn.uni.ui.UIScreen;
import kn.uni.ui.pmButton;
import kn.uni.util.Util;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OnScreenKeyboard extends UIScreen
{
	public static final  double                   ratio               = 2.1538461538461537;
	private static final int                      maxButtonsInRow     = 11;
	private final        Map <Languages, Layout>  LayoutLangStringMap = Map.of(
		Languages.DE, new Layout(LayoutHead.numDE, LayoutBody.DE),
		Languages.GREEK, new Layout(LayoutHead.num, LayoutBody.GREEK));
	private final        Map <LayoutHead, String> layoutHeadStringMap = Map.of(
		LayoutHead.num, "1234567890 ",
		LayoutHead.numDE, "1234567890ß",
		LayoutHead.extra, "!\"§$%&/()=?");
	private final        Map <LayoutBody, String> layoutBodyStringMap = Map.of(
		//      LayoutBody.empty, "                             ",

		LayoutBody.specialSigns1, "    _<>[]# ^  ':;,`~\\|{}()    ",
		LayoutBody.specialSigns2, "°·                           ",

		LayoutBody.math, "+-×÷=±∑∏≂∞∀∃∇≠≈≙√≤≥⋘⋙∫∮⌀∡⦝⟂∂∝",
		LayoutBody.logic, "→←↓↑⇒⇐⇔⇋↯∧∨⊻⊽⋂⋃¬≡∈∉⊂⊄⊃⊅      ",
		LayoutBody.mathSets, "ℕℝℚℙℤℍℂ                      ",
		LayoutBody.roman, "ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅬⅭⅮⅯↀↁↂ           ",

		LayoutBody.currency, "€£¥₩₿￠¤₪₹₱₽                  ",

		LayoutBody.DE, "QWERTZUIOPÜASDFGHJKLÖÄYXCVBNM".toLowerCase(),
		LayoutBody.GREEK, "  ΕΡΤΥΘΙΟΠ ΑΣΔΦΓΗΞΚΛ  ΖΧΨΩΒΝΜ".toLowerCase());
	private final        Map <Point, Point>       keyMap              = Map.of(
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
	private final        char[][]                 keyRows             = new char[3][];
	private final        char[]                   topRow              = new char[11];
	private final        pmButton[][]             buttons             = new pmButton[5][];
	private final        int                      buttonBaseSize;
	private final        int                      buttonBuffer;
	public               Consumer <String>        target;
	public               GameSummaryPanel         parent;
	private              List <LayoutBody>        specialLayers;
	private              LayoutBody               activeExtra         = LayoutBody.DE;
	private              Layout                   activeLayout        = new Layout(LayoutHead.numDE, LayoutBody.DE);
	private              Languages                activeLanguage      = Languages.DE;
	/**
	 * Position of the active key in space coordinates
	 */
	private              Point                    activeKey           = new Point(3, 5);
	private              int                      listenerId;
	private              boolean                  shifted             = false;
	private              InputListener.Player     player;

	public OnScreenKeyboard (GameSummaryPanel parent, InputListener.Player player, int width)
	{
		super(parent);
		this.parent = parent;
		this.player = player;
		//initialisation
		int buttonDistProp = 6;
		buttonBaseSize = width * buttonDistProp / (maxButtonsInRow * 7 + 1);
		buttonBuffer = buttonBaseSize / buttonDistProp;

		setBackground(Color.black);
		setLayout(null);

		pmButton border = new pmButton("");
		border.setBounds(0, 0, width, (int)(width / ratio));
		border.update();
		add(border);
		setSize(width, (int)(width / ratio));
		createButtons();
		setButtonLayout(activeLayout);
		toggleKey(activeKey);
		activate();
		//end of initialisation

	}

	/**
	 * Used to tell the keyboard where it should write to
	 */
	public void setTarget (Consumer <String> target)
	{
		this.target = target;
	}

	/**
	 * Used to generate the key-strings of the selected layout
	 */
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

	/**
	 * Used to initialize the buttons
	 */
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
		buttons[3][0] = createActionButton("\uD83E\uDC39", this::toggleShift, 2, 0, 3);
		buttons[3][0].setFont(new Font("Ariel", Font.PLAIN, 64));
		//buttons Y-M
		for (int i = 1; i < buttons[3].length - 1; i++)
		{
			buttons[3][i] = createPrintableButton("", "", 1, i + 1, 3);
		}
		//button remove
		buttons[3][8] = createPrintableButton("\uD83E\uDC44", "", 2, 9, 3);
		buttons[3][8].setFont(new Font("Ariel", Font.PLAIN, 40));
		//button extra
		buttons[4][0] = createActionButton("!#1", this::extraLayout, 2, 0, 4);
		//button left text
		buttons[4][1] = createPrintableButton("/", "/", 1, 2, 4);
		//button space bar
		buttons[4][2] = createPrintableButton("-----", " ", 4, 3, 4);
		//button right text
		buttons[4][3] = createPrintableButton(".", ".", 1, 7, 4);
		//button lang
		buttons[4][4] = createActionButton("@", this::cycleLanguage, 1, 8, 4);
		//button enter
		buttons[4][5] = createPrintableButton("↲", "", 2, 9, 4);
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

	/**
	 * Used to change the active layout
	 *
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

	/**
	 * Toggles the buttons from upper to lower case or reversed
	 */
	private void toggleShift ()
	{
		this.shifted = !this.shifted;
		if (this.shifted)
		{
			setButtonLayout(new Layout(LayoutHead.extra, activeLayout.layoutBody));
		}
		else
		{
			if (activeLanguage.equals(Languages.DE))
			{
				setButtonLayout(new Layout(LayoutHead.numDE, activeLayout.layoutBody));
			}
			else if (activeLanguage.equals(Languages.GREEK))
			{
				setButtonLayout(new Layout(LayoutHead.num, activeLayout.layoutBody));
			}
		}
	}

	/**
	 * Used to toggle the language
	 */
	private void cycleLanguage ()
	{
		List <Languages> languagesList = Arrays.stream(Languages.values()).toList();
		int              index         = languagesList.indexOf(activeLanguage);
		Languages        next          = languagesList.get((index + 1) % languagesList.size());
		activeLanguage = next;
		setButtonLayout(LayoutLangStringMap.get(next));

		specialLayers = Arrays.stream(LayoutBody.values())
		                      .filter(layoutBody -> (!Arrays.stream(Languages.values())
		                                                    .map(Enum::name)
		                                                    .toList()
		                                                    .contains(layoutBody.name())) || layoutBody.name().equals(activeLanguage.name()))
		                      .toList();

	}

	/**
	 * Used to toggle the extra layout
	 */
	private void extraLayout ()
	{
		if (specialLayers == null)
			specialLayers = Arrays.stream(LayoutBody.values())
			                      .filter(layoutBody -> (!Arrays.stream(Languages.values())
			                                                    .map(Enum::name)
			                                                    .toList()
			                                                    .contains(layoutBody.name())) || layoutBody.name().equals(activeLanguage.name()))
			                      .toList();
		int        index = specialLayers.indexOf(activeExtra);
		LayoutBody next  = specialLayers.get((index + 1) % specialLayers.size());
		activeExtra = next;
		activeLayout = new Layout(activeLayout.layoutHead, next);
		setButtonLayout(activeLayout);
	}

	/**
	 * Used to recursively compute the target button of the special buttons
	 */
	private Point computeShiftedButton (Point coordinate)
	{
		return (coordinate.x == 0) ? coordinate : keyMap.getOrDefault(coordinate, new Point(coordinate.y, computeShiftedButton(new Point(coordinate.y, coordinate.x - 1)).x + 1));
	}

	/**
	 * used to toggle the selection of a key
	 *
	 * @param pos - the position of the concerned key in the 11x5 layout
	 */
	private void toggleKey (Point pos)
	{
		pos = computeShiftedButton(pos);
		buttons[pos.y][pos.x].isSelected = !buttons[pos.y][pos.x].isSelected;
		buttons[pos.y][pos.x].update();
	}

	/**
	 * Used to set a certain key as active
	 *
	 * @param newKey - the location of the new on a 5x11 board
	 */
	private void setActiveKey (Point newKey)
	{
		toggleKey(activeKey);

		if (activeKey.y >= 3)
		{
			if (activeKey.x == 0 && newKey.x == 1) newKey = new Point(activeKey.y, activeKey.x + 2);
			if (activeKey.x == 10 && newKey.x == 9) newKey = new Point(activeKey.y, activeKey.x - 2);
		}
		if (activeKey.y == 4)
		{
			if (activeKey.x >= 3 && activeKey.x <= 6 && newKey.x == activeKey.x + 1) newKey = new Point(activeKey.y, 7);
			if (activeKey.x >= 3 && activeKey.x <= 6 && newKey.x == activeKey.x - 1) newKey = new Point(activeKey.y, 2);
		}

		activeKey = newKey;
		toggleKey(activeKey);
	}

	/**
	 * Create a button, capable of printing a key
	 */
	private pmButton createPrintableButton (String text, String key, double size, int x, int y)
	{
		return createActionButton(text, () -> target.accept(shifted ? key.toUpperCase() : key), size, x, y);
	}

	/**
	 * Create an actionButton, capable of triggering an event
	 */
	private pmButton createActionButton (String text, Runnable action, double size, int x, int y)
	{
		pmButton temp = new pmButton(text);
		temp.addAction(action);
		temp.setSize((int)Math.round(buttonBaseSize * size + (size - 1) * buttonBuffer), buttonBaseSize);
		temp.setLocation((x + 1) * buttonBuffer + x * buttonBaseSize + getWidth() / Gui.frameWidth,
			(y + 1) * buttonBuffer + y * buttonBaseSize + getWidth() / Gui.frameWidth);
		temp.isSelected = false;
		temp.update();

		return temp;
	}

	/**
	 * Activate the Input Listener
	 */
	private void activate ()
	{
		//ToDo use bindPlayer eventually
		listenerId = InputListener.getInstance().subscribe(input ->
		{
			//block inputs from players except the assigned player
			if (!input.player().equals(player)) return;

			if (input.equals(new InputListener.Input(InputListener.Key.A, InputListener.State.down, player)))
			{
				Point p = computeShiftedButton(activeKey);
				buttons[p.y][p.x].press();
			}
			//removes&unsub this and sub input listener of parent
			if (input.equals(new InputListener.Input(InputListener.Key.B, InputListener.State.down, player)))
			{
				InputListener.getInstance().unsubscribe(listenerId);
				setVisible(false);
				getParent().remove(this);
				this.parent.unmuteSummary();
			}

			if (!Arrays.asList(InputListener.Key.vertical, InputListener.Key.horizontal)
			           .contains(input.key())) return;
			int delta = switch (input.state())
				{
					case up -> -1;
					case down -> 1;
					case none -> 0;
				};

			if (input.key().name().equals("horizontal"))
			{
				int x = Util.bounded(activeKey.x + delta, 0, 11 - 1);
				setActiveKey(new Point(activeKey.y, x));
			}
			if (input.key().name().equals("vertical"))
			{
				int y = Util.bounded(activeKey.y + delta, 0, 5 - 1);
				setActiveKey(new Point(y, activeKey.x));
			}
		});
		setVisible(true);
	}

	/**
	 * Deactivate the Input Listener
	 */
	private void deactivate ()
	{
		InputListener.getInstance().unsubscribe(listenerId);
		setVisible(false);
	}

	private enum LayoutHead
	{ num, numDE, extra }

	private enum LayoutBody
	{ specialSigns1, specialSigns2, math, logic, mathSets, roman, currency, DE, GREEK }

	private enum Languages
	{ DE, GREEK }

	private record Layout(LayoutHead layoutHead, LayoutBody layoutBody)
	{}

	private record Language(Languages language)
	{}

	private record Point(int y, int x)
	{}
}
