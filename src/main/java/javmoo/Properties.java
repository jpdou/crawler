package javmoo;

import java.io.*;

public class Properties {
    Properties()
    {
        try {
            InputStream inStream = new FileInputStream(new File("app.properties"));
        } catch (FileNotFoundException e) {
            System.out.println("Fatal Error: app.properties Not Found! ");
        }
    }
}
