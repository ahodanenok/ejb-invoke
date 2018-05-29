package ahodanenok.ejb.invoke;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public final class PropertiesUtils {

    private PropertiesUtils() { }

    public static Properties fromFile(String path) {
        Properties properties = new Properties();

        try {
            properties.load(new BufferedReader(new FileReader(path)));
        } catch (FileNotFoundException e) {
            // todo: log
        } catch (IOException e) {
            // todo: log
        }

        return properties;
    }
}
