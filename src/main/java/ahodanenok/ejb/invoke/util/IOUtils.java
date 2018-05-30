package ahodanenok.ejb.invoke.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class IOUtils {

    private IOUtils() { }

    public static List<String> getLines(String file) throws IOException {
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

    private static final void close(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException e) {
            // todo: log
        }
    }
}
