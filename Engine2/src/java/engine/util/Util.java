package engine.util;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

public class Util
{
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("uuuu-MMM-dd HH.mm.ss.SSS");
    private static final DateTimeFormatter timeFormat     = DateTimeFormatter.ofPattern("HH.mm.ss.SSS");
    private static final DateTimeFormatter dateFormat     = DateTimeFormatter.ofPattern("uuuu-MMM-dd");
    
    /**
     * @return The current DateTime string.
     */
    public static String getCurrentDateTimeString()
    {
        return LocalDateTime.now().format(dateTimeFormat);
    }
    
    /**
     * @return The current Time string.
     */
    public static String getCurrentTimeString()
    {
        return LocalDateTime.now().toLocalTime().format(timeFormat);
    }
    
    /**
     * @return The current Date string.
     */
    public static String getCurrentDateString()
    {
        return LocalDateTime.now().toLocalDate().format(dateFormat);
    }
    
    private static final Pattern fsPattern = Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])"); // Taken from java.lang.Formatter
    
    /**
     * Prints an object to the console.
     *
     * @param object The object.
     */
    public static void print(Object object)
    {
        System.out.print(object);
    }
    
    /**
     * Prints the objects to the console separated by a space.
     *
     * @param objects The objects.
     */
    public static void print(Object... objects)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, n = objects.length; i < n; i++)
        {
            builder.append(objects[i]);
            if (i + 1 < n) builder.append(" ");
        }
        System.out.print(builder.toString());
    }
    
    /**
     * Prints the objects separated by a space to the console. If the format string contains format characters, then is will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects.
     */
    public static void print(String format, Object... objects)
    {
        if (Util.fsPattern.matcher(format).find())
        {
            System.out.print(String.format(format, objects));
        }
        else
        {
            StringBuilder builder = new StringBuilder(format).append(' ');
            for (int i = 0, n = objects.length; i < n; i++)
            {
                builder.append(objects[i]);
                if (i + 1 < n) builder.append(' ');
            }
            System.out.print(builder.toString());
        }
    }
    
    /**
     * Prints an object, then a new line to the console.
     *
     * @param object The object.
     */
    public static void println(Object object)
    {
        System.out.println(object);
    }
    
    /**
     * Prints the objects separated by a space, then a new line to the console.
     *
     * @param objects The objects.
     */
    public static void println(Object... objects)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, n = objects.length; i < n; i++)
        {
            builder.append(objects[i]);
            if (i + 1 < n) builder.append(" ");
        }
        System.out.println(builder.toString());
    }
    
    /**
     * Prints the objects separated by a space, then a new line to the console. If the format string contains format characters, then is will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects.
     */
    public static void println(String format, Object... objects)
    {
        if (Util.fsPattern.matcher(format).find())
        {
            System.out.println(String.format(format, objects));
        }
        else
        {
            StringBuilder builder = new StringBuilder(format).append(' ');
            for (int i = 0, n = objects.length; i < n; i++)
            {
                builder.append(objects[i]);
                if (i + 1 < n) builder.append(' ');
            }
            System.out.println(builder.toString());
        }
    }
    
    /**
     * Maps the value x from between x0 and x1 to a value between y0 and y1
     *
     * @param x  The value to map.
     * @param x0 The initial bound
     * @param x1 The initial bound
     * @param y0 The mapped bound
     * @param y1 The mapped bound
     * @return The mapped value.
     */
    public static double map(double x, double x0, double x1, double y0, double y1)
    {
        return (x - x0) * (y1 - y0) / (x1 - x0) + y0;
    }
    
    /**
     * This takes an array of decimals and determines the max length before and after the decimal point to align the decimal points when printed in a column.
     *
     * @param values The double values.
     * @return The pair of number.
     */
    public static PairI getFormatNumbers(double[] values)
    {
        int numI = 1, numD = 0;
        for (double val : values)
        {
            String[] num = String.valueOf(val).split("\\.");
            numI = Math.max(numI, num[0].length());
            if (val != (int) val) numD = Math.max(numD, num[1].length());
        }
        return new PairI(numI, numD);
    }
    
    /**
     * Prints a number with a fixed number of character before and after the decimal point.
     *
     * @param x    The number.
     * @param numI The amount of characters before the decimal point.
     * @param numD The amount of characters after the decimal point.
     * @return The formatter number string.
     */
    public static String format(double x, int numI, int numD)
    {
        // TODO - Negative Numbers
        String I  = String.valueOf((int) x);
        String D  = numD > 0 ? String.valueOf((int) Math.round((x - (int) x) * Math.pow(10, numD))) : "";
        String fI = numI > I.length() ? "%" + (numI - I.length()) + "s" : "%s";
        String fD = numD > D.length() ? "%" + (numD - D.length()) + "s" : "%s";
        return String.format(fI + "%s%s%s" + fD, "", I, numD > 0 ? "." : "", D, "");
    }
    
    /**
     * Prints a number with a fixed number of character before and after the decimal point.
     *
     * @param x       The number.
     * @param numbers The pair of number for before and after the decimal point.
     * @return The formatter number string.
     */
    public static String format(double x, PairI numbers)
    {
        return format(x, numbers.a, numbers.b);
    }
    
    /**
     * Joins a collection of objects together into a string separated by a provided string.
     *
     * @param collection The collection of objects.
     * @param between    The string between each object.
     * @param prefix     The string before any objects.
     * @param suffix     The string after all the objects.
     * @return The string.
     */
    public static String join(Collection<?> collection, String between, String prefix, String suffix)
    {
        if (collection.size() == 0) return "";
        Object[]      array = collection.toArray();
        StringBuilder b     = new StringBuilder(prefix);
        b.append(array[0]);
        for (int i = 1, n = array.length; i < n; i++) b.append(between).append(array[i]);
        return b.append(suffix).toString();
    }
    
    /**
     * Joins a collection of objects together into a string separated by a provided string.
     *
     * @param collection The collection of objects.
     * @param between    The string between each object.
     * @return The string.
     */
    public static String join(Collection<?> collection, String between)
    {
        return join(collection, between, "", "");
    }
    
    /**
     * Joins a collection of objects together into a string separated by a space.
     *
     * @param collection The collection of objects.
     * @return The string.
     */
    public static String join(Collection<?> collection)
    {
        return join(collection, ", ", "", "");
    }
    
    /**
     * Rounds a number to a specified number of decimal places.
     *
     * @param value  The number.
     * @param places The number of places.
     * @return The rounded number.
     */
    public static double round(double value, int places)
    {
        if (places <= 0) return Math.round(value);
        double pow = Math.pow(10, places);
        return Math.round(value * pow) / pow;
    }
    
    /**
     * Rounds a number to the nearest integer.
     *
     * @param value The number.
     * @return The rounded number.
     */
    public static double round(double value)
    {
        return round(value, 0);
    }
    
    /**
     * Gets the path to the file. First it tries to load a resources, then if it fails then tries to load from disk.
     *
     * @param resource The string path to the file
     * @return The path to the file
     */
    public static Path getPath(String resource)
    {
        try
        {
            return Paths.get(Objects.requireNonNull(Util.class.getClassLoader().getResource(resource)).toURI());
        }
        catch (URISyntaxException | NullPointerException ignored) { }
        return Paths.get(resource);
    }
    
    /**
     * Loads a file as a ByteBuffer.
     *
     * @param resource The path to the file.
     * @return The data as a ByteBuffer.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public static ByteBuffer resourceToByteBuffer(String resource)
    {
        try (SeekableByteChannel fc = Files.newByteChannel(getPath(resource)))
        {
            ByteBuffer buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
            while (fc.read(buffer) != -1) { }
            buffer.flip();
            return buffer.slice();
        }
        catch (IOException e)
        {
            throw new RuntimeException(String.format("Could not load resource: \"%s\"", resource), e);
        }
    }
    
    /**
     * Gets the decimal part of a number.
     *
     * @param value The number.
     * @return The decimal part.
     */
    public static double getDecimal(double value)
    {
        return value - (int) value;
    }
    
    /**
     * @param value The number.
     * @return If the number is even.
     */
    public static boolean isEven(int value)
    {
        return (value & 1) == 0;
    }
    
    /**
     * @param value The number.
     * @return If the number is odd.
     */
    public static boolean isOdd(int value)
    {
        return (value & 1) == 1;
    }
    
    /**
     * @param value The number.
     * @return If the number is even.
     */
    public static boolean isEven(double value)
    {
        return isEven((int) Math.floor(value));
    }
    
    /**
     * @param value The number.
     * @return If the number is odd.
     */
    public static boolean isOdd(double value)
    {
        return isOdd((int) Math.floor(value));
    }
    
    /**
     * @param array The array.
     * @return The minimum value in the array.
     */
    public static int min(int... array)
    {
        int min = Integer.MAX_VALUE;
        for (int x : array) min = Math.min(min, x);
        return min;
    }
    
    /**
     * @param array The array.
     * @return The minimum value in the array.
     */
    public static long min(long... array)
    {
        long min = Long.MAX_VALUE;
        for (long x : array) min = Math.min(min, x);
        return min;
    }
    
    /**
     * @param array The array.
     * @return The minimum value in the array.
     */
    public static float min(float... array)
    {
        float min = Float.MAX_VALUE;
        for (float x : array) min = Math.min(min, x);
        return min;
    }
    
    /**
     * @param array The array.
     * @return The minimum value in the array.
     */
    public static double min(double... array)
    {
        double min = Double.MAX_VALUE;
        for (double x : array) min = Math.min(min, x);
        return min;
    }
    
    /**
     * @param array The array.
     * @return The maximum value in the array.
     */
    public static int max(int... array)
    {
        int max = Integer.MIN_VALUE;
        for (int x : array) max = Math.max(max, x);
        return max;
    }
    
    /**
     * @param array The array.
     * @return The maximum value in the array.
     */
    public static long max(long... array)
    {
        long max = Long.MIN_VALUE;
        for (long x : array) max = Math.max(max, x);
        return max;
    }
    
    /**
     * @param array The array.
     * @return The maximum value in the array.
     */
    public static float max(float... array)
    {
        float max = Float.MIN_VALUE;
        for (float x : array) max = Math.max(max, x);
        return max;
    }
    
    /**
     * @param array The array.
     * @return The maximum value in the array.
     */
    public static double max(double... array)
    {
        double max = Double.MIN_VALUE;
        for (double x : array) max = Math.max(max, x);
        return max;
    }
}
