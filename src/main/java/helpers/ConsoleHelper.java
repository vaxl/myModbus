package helpers;

import base.LogWork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper implements LogWork {
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void print(String text) {
        System.out.println(text);
    }

    @Override
    public String readText() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error readText";
    }

    @Override
    public int readInt() {
        return 0;
    }
}
