package org.example.GUI;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class ConsoleToFile {
    private PrintStream consoleStream;
    private PrintStream fileStream;

    public ConsoleToFile(String filePath) {
        try {
            consoleStream = System.out;

            fileStream = new PrintStream(new FileOutputStream(filePath));

            // Setăm noul stream ca fiind System.out.
            System.setOut(fileStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Dacă fișierul nu poate fi creat, revenim la consola standard.
            System.setOut(consoleStream);
            System.out.println("Nu a fost posibil să se creeze fișierul de log.");
        }
    }


    public void close() {
        if (fileStream != null) {
            System.setOut(consoleStream); // Restabilim output-ul la consola standard
            fileStream.close(); // Închidem stream-ul de fișier
        }
    }
}
