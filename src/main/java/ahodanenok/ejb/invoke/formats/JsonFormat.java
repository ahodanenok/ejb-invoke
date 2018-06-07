package ahodanenok.ejb.invoke.formats;

import ahodanenok.ejb.invoke.descriptor.EjbInvocationArgument;
import ahodanenok.ejb.invoke.util.StringUtils;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonFormat {

    private static final Logger LOGGER = Logger.getLogger(JsonFormat.class.getName());

    private Gson gson;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private List<SimpleDateFormat> parseDateFormats = new ArrayList<SimpleDateFormat>();

    public JsonFormat() {
        LOGGER.config("Configuring JsonFormat");
        String dateFormatPattern = System.getProperty("ejb.invoke.date.format");
        if (!StringUtils.isNullOrEmpty(dateFormatPattern)) {
            dateFormat = new SimpleDateFormat(dateFormatPattern);
        }


        String formats = System.getProperty("ejb.invoke.date.parse.formats");
        if (!StringUtils.isNullOrEmpty(formats)) {
            for (String format : formats.split(";")) {
                parseDateFormats.add(new SimpleDateFormat(format.trim()));
            }
        } else {
            parseDateFormats.add(dateFormat);
        }

        LOGGER.config("output date format: " + dateFormat.toPattern());
        if (LOGGER.isLoggable(Level.CONFIG)) {
            LOGGER.config("valid date format for parsing:");
            for (SimpleDateFormat df : parseDateFormats) {
                LOGGER.config("  " + df.toPattern());
            }
        }
    }

    public <T> T parse(String file, Class<T> objClass) {
        initGson();

        try {
            LOGGER.finer(String.format("Deserializing file '%s' to class '%s'", file, objClass.getName()));
            return gson.fromJson(new BufferedReader(new FileReader(file)), objClass);
        } catch (FileNotFoundException e) {
            LOGGER.severe(String.format("File '%s' wasn't found", file));
            return null;
        }
    }

    public String format(Object obj) {
        return gson.toJson(obj);
    }

    private void initGson() {
        if (gson != null) {
            return;
        }

        LOGGER.finer("Initializing GSON");
        gson = new GsonBuilder()
            .serializeNulls()
            .setDateFormat(dateFormat.toPattern())
            .registerTypeAdapter(EjbInvocationArgument.class, new EjbInvocationArgumentTypeAdapter())
            .registerTypeAdapter(TimeZone.class, new TimeZoneTypeAdapter().nullSafe())
            .registerTypeAdapter(Calendar.class, new CalendarTypeAdapter().nullSafe())
            .registerTypeAdapter(Date.class, new DateTypeAdapter().nullSafe())
            .setExclusionStrategies(new ExclusionStrategy() {

                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    // workaround: parent and subclass has fields with the same name
                    return isFieldInSuperclass(f.getDeclaringClass(), f.getName());
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }

                private boolean isFieldInSuperclass(Class<?> subclass, String fieldName) {
                    Class<?> superclass = subclass.getSuperclass();
                    Field field;

                    while(superclass != null) {
                        field = getField(superclass, fieldName);
                        if(field != null)
                            return true;
                        superclass = superclass.getSuperclass();
                    }

                    return false;
                }

                private Field getField(Class<?> theClass, String fieldName) {
                    try {
                        return theClass.getDeclaredField(fieldName);
                    } catch(Exception e) {
                        return null;
                    }
                }
            })
            .setPrettyPrinting()
            .create();
    }

    private static class TimeZoneTypeAdapter extends TypeAdapter<TimeZone> {

        @Override
        public void write(JsonWriter out, TimeZone value) throws IOException {
            LOGGER.finer("writing timezone instance '%s'" + value.getID());
            out.value(value.getID());
        }

        @Override
        public TimeZone read(JsonReader in) throws IOException {
            String id = in.nextString();
            LOGGER.finer(String.format("reading timezone instance '%s'", id));
            return TimeZone.getTimeZone(id);
        }
    }

    private class CalendarTypeAdapter extends TypeAdapter<Calendar> {

        @Override
        public void write(JsonWriter out, Calendar value) throws IOException {
            LOGGER.finer(String.format("writing calendar instance with time '%s'", value.getTime()));
            out.value(dateFormat.format(value.getTime()));
        }

        @Override
        public Calendar read(JsonReader in) throws IOException {
            Date date = null;
            String val = in.nextString();
            LOGGER.finer(String.format("reading calendar instance '%s'", val));

            for (int i = 0; i < parseDateFormats.size(); i++) {
                SimpleDateFormat df = parseDateFormats.get(i);
                try {
                    date = df.parse(val);
                    LOGGER.finer(String.format("date parsed, format '%s'", df.toPattern()));
                    break; // we'd better leave this place
                } catch (ParseException e) {
                    LOGGER.finer(String.format("wrong date format '%s', skipping", df.toPattern()));
                }
            }

            if (date == null) {
                throw new IOException("Can't parse calendar from: " + val);
            }

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return c;
        }
    }

    private class DateTypeAdapter extends TypeAdapter<Date> {

        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            LOGGER.finer(String.format("writing date instance with time '%s'", value.getTime()));
            out.value(dateFormat.format(value.getTime()));
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            Date date = null;
            String val = in.nextString();
            LOGGER.finer(String.format("reading date instance '%s'", val));

            for (int i = 0; i < parseDateFormats.size(); i++) {
                SimpleDateFormat df = parseDateFormats.get(i);
                try {
                    date = df.parse(val);
                    LOGGER.finer(String.format("date parsed, format '%s'", df.toPattern()));
                    break; // we'd better leave this place
                } catch (ParseException e) {
                    LOGGER.finer(String.format("wrong date format '%s', skipping", df.toPattern()));
                }
            }

            if (date == null) {
                throw new IOException("Can't parse date from: " + val);
            }

            return date;
        }
    }

    private class EjbInvocationArgumentTypeAdapter extends TypeAdapter<EjbInvocationArgument> {

        @Override
        public void write(JsonWriter out, EjbInvocationArgument value) throws IOException {
            LOGGER.finer("writing EjbInvocationArgument instance");
            LOGGER.finer("argument class: " + value.getValue().getClass().getName());
            out.beginObject();
            out.name("className");
            out.value(value.getValue().getClass().getName());
            out.name("value");
            gson.toJson(value.getValue(), value.getValue().getClass(), out);
            out.endObject();
        }

        @Override
        public EjbInvocationArgument read(JsonReader in) throws IOException {
            String className = null;
            try {
                LOGGER.finer("reading EjbInvocationArgument instance");
                in.beginObject();
                in.nextName();
                className = in.nextString();
                LOGGER.finer("argument class: " + className);
                Class valueClass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
                in.nextName();
                Object value = gson.fromJson(in, valueClass);
                in.endObject();

                EjbInvocationArgument argument = new EjbInvocationArgument();
                argument.setValue(value);
                return argument;
            } catch (ClassNotFoundException e) {
                LOGGER.severe(String.format("Class '%s' wasn't found, please check classpath config", className));
                return null;
            }
        }
    }
}
