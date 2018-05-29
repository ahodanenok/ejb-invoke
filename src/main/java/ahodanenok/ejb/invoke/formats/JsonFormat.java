package ahodanenok.ejb.invoke.json;

import ahodanenok.ejb.invoke.descriptor.EjbInvocationArgument;
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

public class JsonProcessor {

    private Gson gson;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private List<SimpleDateFormat> parseDateFormats = new ArrayList<SimpleDateFormat>();;

    public JsonProcessor() {
        String dateFormatPattern = System.getProperty("ejb.invoke.date.format");
        if (dateFormatPattern != null) {
            // todo: log
            dateFormat = new SimpleDateFormat(dateFormatPattern);
        }


        String formats = System.getProperty("ejb.invoke.date.parse.formats");
        if (formats != null) {
            // todo: log
            for (String format : formats.split(";")) {
                parseDateFormats.add(new SimpleDateFormat(format.trim()));
            }
        }
    }

    public <T> T parse(String file, Class<T> objClass) {
        initGson();

        try {
            return gson.fromJson(new BufferedReader(new FileReader(file)), objClass);
        } catch (FileNotFoundException e) {
            // todo: log
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
            // todo: log
            out.value(value.getID());
        }

        @Override
        public TimeZone read(JsonReader in) throws IOException {
            String id = in.nextString();
            // todo: log
            return TimeZone.getTimeZone(id);
        }
    }

    private class CalendarTypeAdapter extends TypeAdapter<Calendar> {

        @Override
        public void write(JsonWriter out, Calendar value) throws IOException {
            // todo: log
            out.value(dateFormat.format(value.getTime()));
        }

        @Override
        public Calendar read(JsonReader in) throws IOException {
            Date date = null;
            String val = in.nextString();
            // todo: log

            for (int i = 0; i < parseDateFormats.size(); i++) {
                SimpleDateFormat df = parseDateFormats.get(i);
                try {
                    date = df.parse(val);
                    // todo: log
                    break; // we'd better leave this place
                } catch (ParseException e) {
                    // todo: log
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
            // todo: log
            out.value(dateFormat.format(value.getTime()));
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            Date date = null;
            String val = in.nextString();
            // todo: log

            for (int i = 0; i < parseDateFormats.size(); i++) {
                SimpleDateFormat df = parseDateFormats.get(i);
                try {
                    date = df.parse(val);
                    // todo: log
                    break; // we'd better leave this place
                } catch (ParseException e) {
                    // todo: log
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
            out.beginObject();
            out.name("className");
            out.value(value.getValue().getClass().getName());
            out.name("value");
            gson.toJson(value.getValue(), value.getValue().getClass(), out);
            out.endObject();
        }

        @Override
        public EjbInvocationArgument read(JsonReader in) throws IOException {
            try {
                // todo: log
                in.beginObject();
                in.nextName();
                Class valueClass = Class.forName(in.nextString(), true, Thread.currentThread().getContextClassLoader());
                in.nextName();
                Object value = gson.fromJson(in, valueClass);
                in.endObject();

                EjbInvocationArgument argument = new EjbInvocationArgument();
                argument.setValue(value);
                return argument;
            } catch (ClassNotFoundException e) {
                // todo: log
                return null;
            }
        }
    }
}
