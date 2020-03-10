package bg.sofia.uni.fmi.mjt.stylechecker;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        StyleChecker styleChecker = new StyleChecker();

        try {
            styleChecker.checkStyle(new FileReader("test.txt"), new FileWriter("temp.txt"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
