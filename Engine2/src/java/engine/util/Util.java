package engine.util;

import java.net.URISyntaxException;
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
    
    public static String getCurrentDateTimeString() { return LocalDateTime.now().format(dateTimeFormat); }
    
    public static String getCurrentTimeString()     { return LocalDateTime.now().toLocalTime().format(timeFormat); }
    
    public static String getCurrentDateString()     { return LocalDateTime.now().toLocalDate().format(dateFormat); }
    
    private static final Pattern fsPattern = Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])"); // Taken from java.lang.Formatter
    
    public static void print(Object object) { System.out.print(object); }
    
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
    
    public static void println(Object object) { System.out.println(object); }
    
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
    
    public static double map(double x, double xMin, double xMax, double yMin, double yMax) { return (x - xMin) * (yMax - yMin) / (xMax - xMin) + yMin; }
    
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
    
    // TODO - Negative Numbers
    public static String format(double x, int numI, int numD)
    {
        String I  = String.valueOf((int) x);
        String D  = numD > 0 ? String.valueOf((int) Math.round((x - (int) x) * Math.pow(10, numD))) : "";
        String fI = numI > I.length() ? "%" + (numI - I.length()) + "s" : "%s";
        String fD = numD > D.length() ? "%" + (numD - D.length()) + "s" : "%s";
        return String.format(fI + "%s%s%s" + fD, "", I, numD > 0 ? "." : "", D, "");
    }
    
    public static String format(double x, PairI numbers) { return format(x, numbers.a, numbers.b); }
    
    public static String join(Collection<?> lines, String between, String prefix, String suffix)
    {
        if (lines.size() == 0) return "";
        Object[]      array = lines.toArray();
        StringBuilder b     = new StringBuilder(prefix);
        b.append(array[0]);
        for (int i = 1, n = array.length; i < n; i++) b.append(between).append(array[i]);
        return b.append(suffix).toString();
    }
    
    public static String join(Collection<?> lines, String between) { return join(lines, between, "", ""); }
    
    public static String join(Collection<?> lines)                 { return join(lines, ", ", "", ""); }
    
    public static double round(double value, int places)
    {
        if (places <= 0) return Math.round(value);
        double pow = Math.pow(10, places);
        return Math.round(value * pow) / pow;
    }
    
    public static double round(double value) { return round(value, 0); }
    
    public static Path getPath(String filePath)
    {
        try
        {
            return Paths.get(Objects.requireNonNull(Util.class.getClassLoader().getResource(filePath)).toURI());
        }
        catch (URISyntaxException | NullPointerException ignored) { }
        return Paths.get(filePath);
    }
    
    public static double getDecimal(double value)
    {
        return value - (int) value;
    }
    
    public static boolean isEven(int value)
    {
        return (value & 1) == 0;
    }
    
    public static boolean isOdd(int value)
    {
        return (value & 1) == 1;
    }
    
    public static boolean isEven(double value)
    {
        return ((int) Math.floor(value) & 1) == 0;
    }
    
    public static boolean isOdd(double value)
    {
        return ((int) Math.floor(value) & 1) == 1;
    }
    
    public static int min(int[] array)
    {
        int min = Integer.MAX_VALUE;
        for (int x : array) min = Math.min(min, x);
        return min;
    }
    
    public static long min(long[] array)
    {
        long min = Long.MAX_VALUE;
        for (long x : array) min = Math.min(min, x);
        return min;
    }
    
    public static float min(float[] array)
    {
        float min = Float.MAX_VALUE;
        for (float x : array) min = Math.min(min, x);
        return min;
    }
    
    public static double min(double[] array)
    {
        double min = Double.MAX_VALUE;
        for (double x : array) min = Math.min(min, x);
        return min;
    }
    
    public static int max(int[] array)
    {
        int max = Integer.MIN_VALUE;
        for (int x : array) max = Math.max(max, x);
        return max;
    }
    
    public static long max(long[] array)
    {
        long max = Long.MIN_VALUE;
        for (long x : array) max = Math.max(max, x);
        return max;
    }
    
    public static float max(float[] array)
    {
        float max = Float.MIN_VALUE;
        for (float x : array) max = Math.max(max, x);
        return max;
    }
    
    public static double max(double[] array)
    {
        double max = Double.MIN_VALUE;
        for (double x : array) max = Math.max(max, x);
        return max;
    }
    
    public static int minFrom(int... values)       { return min(values); }
    
    public static long minFrom(long... values)     { return min(values); }
    
    public static float minFrom(float... values)   { return min(values); }
    
    public static double minFrom(double... values) { return min(values); }
    
    public static int maxFrom(int... values)       { return max(values); }
    
    public static long maxFrom(long... values)     { return max(values); }
    
    public static float maxFrom(float... values)   { return max(values); }
    
    public static double maxFrom(double... values) { return max(values); }
}
