import javax.swing.*;
import java.awt.*;

public class onScreenKeyboard extends JPanel {


    private pmButton temp;
    private String bKeys ="QWERTZUIOPÜASDFGHJKLÖÄYXCVBNM";
    private char[][] rows = new char[3][];

    public onScreenKeyboard(){

        setBackground(Color.black);
        setLayout(null);

        temp = new pmButton("Ü");
        temp.setBounds(12,12,500,100);
        add(temp);


    }

    private void createKeys(){



        String[] arr = bKeys.split("", 1);

        for(int i =0;i<arr.length;i++){

            System.out.print(arr[i]);
        }

    }
}
