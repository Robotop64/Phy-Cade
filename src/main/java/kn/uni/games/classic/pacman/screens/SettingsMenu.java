package kn.uni.games.classic.pacman.screens;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsMenu {


    public static void setActiveVersion(String version){
        try{
            FileWriter fileWriter = new FileWriter("GameBranch.txt");
            fileWriter.write(version);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
