package ahodanenok.ejb.invoke.util;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PropertiesUtils {

    private static final Logger LOGGER = Logger.getLogger(PropertiesUtils.class.getName());

    private PropertiesUtils() { }

    public static Properties fromFile(File file) {
        Properties properties = new Properties();

        try {
            properties.load(new BufferedReader(new FileReader(file)));
        } catch (FileNotFoundException e) {
            LOGGER.warning(String.format("Properties file '%s' hasn't been found", file));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.format("Couldn't load properties from file '%s'", file), e);
        }

        return properties;
    }
}
