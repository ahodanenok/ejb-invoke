package ahodanenok.ejb.invoke.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class IOUtils {

    private static final Logger LOGGER = Logger.getLogger(IOUtils.class.getName());

    private IOUtils() { }

    public static List<String> getLines(File file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            List<String> lines = new ArrayList<String>();
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            return lines;
        } finally {
            close(reader);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            LOGGER.finer("Couldn't close closable instance, reason: " + e.getMessage());
        }
    }
}
