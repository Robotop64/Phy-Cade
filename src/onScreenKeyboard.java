import javax.swing.*;
import java.awt.*;

public class onScreenKeyboard extends JPanel {


    private pmButton button;
    private String DE ="QWERTZUIOPÜASDFGHJKLÖÄYXCVBNM";
    private String GREEK ="  ΕΡΤΥΘΙΟΠ ΑΣΔΦΓΗΞΚΛ  ΖΧΨΩΒΝΜ";

    private final char[][] keyRows = new char[3][];
    private final pmButton[][] buttons = new pmButton[5][];

    public onScreenKeyboard(){

        setBackground(Color.black);
        setLayout(null);
        createButtons();

        renameButtons(DE);
    }

    private void createKeyStrings(String lang){
        char[] arr = lang.toCharArray();

        int[] l = {11,11,7};
        for(int i=0;i<3;i++){
            keyRows[i] = new char[l[i]];
        }

        for(int j=0;j<3;j++) {
            System.arraycopy(arr, j * 11, keyRows[j], 0, keyRows[j].length);
        }
    }
    private void createButtons(){
        int buttonSize =80;
        int buttonBuffer = 20;
        int buttonOffset = buttonSize+buttonBuffer;
        int maxButtonsInRow = 11;
        int origin = Gui.frame_width/2-(maxButtonsInRow*buttonOffset)/2;

        int[] l = {11,11,11,9,6};
        for(int i=0;i<5;i++){
            buttons[i] = new pmButton[l[i]];
        }

        for(int j=0;j<5;j++) {
            for (int i = 0; i < buttons[j].length; i++) {
                buttons[j][i] =new pmButton("");
                buttons[j][i].setBounds(origin+i*buttonOffset,375+origin+j*buttonOffset,buttonSize,buttonSize);
                buttons[j][i].isSelected=false;
                buttons[j][i].update();
                add(buttons[j][i]);
            }
        }

        int[] location = {buttons[3][0].getBounds().getLocation().x,buttons[3][0].getBounds().y};
        buttons[3][0].setBounds(location[0],location[1],buttonSize+buttonOffset,buttonSize);
        buttons[3][0].setText("\uD83E\uDC39");
        buttons[3][0].setFont(new Font("Ariel", Font.PLAIN, 64));

        for(int i=1;i<9;i++){
            int[] location2 = {buttons[3][i].getBounds().getLocation().x,buttons[3][i].getBounds().y};
            buttons[3][i].setBounds(location2[0]+buttonOffset,location2[1],buttonSize,buttonSize);
        }

        int[] location3 = {buttons[3][8].getBounds().getLocation().x,buttons[3][8].getBounds().y};
        buttons[3][8].setBounds(location3[0],location3[1],buttonSize+buttonOffset,buttonSize);
        buttons[3][8].setText("\uD83E\uDC44");
        buttons[3][8].setFont(new Font("Ariel", Font.PLAIN, 40));

        int var = buttonSize/2+buttonBuffer/2;

        Point r = buttons[4][2].getLocation();
        buttons[4][2].getBounds().translate(2* (var) ,0);
        buttons[4][2].setLocation(r.x+2 * (var),r.y);
        buttons[4][2].setSize(4*buttonSize+3*buttonBuffer, buttonSize);
        buttons[4][2].setText("-----");

        for(int i=0;i<6;i++){
            if(i<2) {

                int newW = buttonSize + var;
                Point h = buttons[4][i].getLocation();
                buttons[4][i].setLocation(h.x+i * (var),h.y);
                buttons[4][i].setSize(newW, buttonSize);
            }
            if(i>2) {
                int newW2= (int) Math.round((4*buttonSize+buttonBuffer)/3.0);
                Point f = buttons[4][2].getLocation();
                buttons[4][i].setLocation(f.x+buttons[4][2].getSize().width+buttonBuffer+(i-3)*(newW2+buttonBuffer),f.y);
                buttons[4][i].setSize(newW2, buttonSize);
            }
        }

        buttons[4][0].setText("!#1");
        buttons[4][1].setText("/");
        buttons[4][3].setText(".");
        buttons[4][4].setText("Lang");
        buttons[4][5].setText("↲");
        buttons[4][5].setFont(new Font("Ariel", Font.PLAIN, 40));





    }

    private void renameButtons(String lang){
        createKeyStrings(lang);
        for(int i=0;i<10;i++){
            buttons[0][i].setText(Integer.toString(i));
        }
        for(int j=0;j<2;j++) {
            for (int i = 0; i < buttons[j+1].length; i++) {
                buttons[j+1][i].setText(Character.toString(keyRows[j][i]));
            }
        }
        for (int i = 0; i < keyRows[2].length; i++) {
            buttons[3][i+1].setText(Character.toString(keyRows[2][i]));
        }
    }

}
