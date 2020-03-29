package engine.util;

import java.util.regex.Pattern;

import static engine.util.Util.getCurrentTimeString;

/**
 * A simple logging implementation for use in Engine classes. Only one logger should be used per file as the file's class path is in the message.
 * <p>
 * Use the global {@link #setLevel} to allow for log message to be displayed to the console.
 */
@SuppressWarnings("unused")
public class Logger
{
    private static final Pattern fsPattern = Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])"); // Taken from java.lang.Formatter
    
    private static Level level = Level.INFO;
    
    /**
     * @return The global logging level.
     */
    public static Level getLevel()
    {
        return Logger.level;
    }
    
    /**
     * Sets the global logging level.
     *
     * @param level The new level.
     */
    public static void setLevel(Level level)
    {
        Logger.level = level;
    }
    
    private final String className;
    
    /**
     * Creates a new logger whose name is the class path to the file.
     */
    public Logger()
    {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        
        this.className = elements.length > 2 ? elements[2].getClassName() : "";
    }
    
    private void log(Level level, String message)
    {
        if (level.compareTo(Logger.level) <= 0)
        {
            String thread = Thread.currentThread().getName();
            String line;
            if (this.className.equals(""))
            {
                line = String.format("[%s] [%s/%s]: %s", getCurrentTimeString(), thread, level, message);
            }
            else
            {
                line = String.format("[%s] [%s/%s] [%s]: %s", getCurrentTimeString(), thread, level, this.className, message);
            }
            System.out.println(line);
        }
    }
    
    /**
     * Logs the objects separated by spaces at the level specified.
     *
     * @param level   The level to log at.
     * @param objects The objects to log.
     */
    public void log(Level level, Object... objects)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, n = objects.length; i < n; i++)
        {
            builder.append(objects[i]);
            if (i + 1 < n) builder.append(' ');
        }
        log(level, builder.toString());
    }
    
    /**
     * Logs the objects separated by spaces at the level specified. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param level   The level to log at.
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void log(Level level, String format, Object... objects)
    {
        if (Logger.fsPattern.matcher(format).find())
        {
            log(level, String.format(format, objects));
        }
        else
        {
            StringBuilder builder = new StringBuilder(format).append(' ');
            for (int i = 0, n = objects.length; i < n; i++)
            {
                builder.append(objects[i]);
                if (i + 1 < n) builder.append(' ');
            }
            log(level, builder.toString());
        }
    }
    
    /**
     * Logs the object at {@link Level#FATAL}.
     *
     * @param object The objects to log.
     */
    public void fatal(Object object) { log(Level.FATAL, String.valueOf(object)); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#FATAL}.
     *
     * @param objects The objects to log.
     */
    public void fatal(Object... objects) { log(Level.FATAL, objects); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#FATAL}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void fatal(String format, Object... objects) { log(Level.FATAL, format, objects); }
    
    /**
     * Logs the object at {@link Level#ERROR}.
     *
     * @param object The objects to log.
     */
    public void error(Object object) { log(Level.ERROR, String.valueOf(object)); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#ERROR}.
     *
     * @param objects The objects to log.
     */
    public void error(Object... objects) { log(Level.ERROR, objects); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#ERROR}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void error(String format, Object... objects) { log(Level.ERROR, format, objects); }
    
    /**
     * Logs the object at {@link Level#WARN}.
     *
     * @param object The objects to log.
     */
    public void warn(Object object) { log(Level.WARN, String.valueOf(object)); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#WARN}.
     *
     * @param objects The objects to log.
     */
    public void warn(Object... objects) { log(Level.WARN, objects); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#WARN}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void warn(String format, Object... objects) { log(Level.WARN, format, objects); }
    
    /**
     * Logs the object at {@link Level#INFO}.
     *
     * @param object The objects to log.
     */
    public void info(Object object) { log(Level.INFO, String.valueOf(object)); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#INFO}.
     *
     * @param objects The objects to log.
     */
    public void info(Object... objects) { log(Level.INFO, objects); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#INFO}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void info(String format, Object... objects) { log(Level.INFO, format, objects); }
    
    /**
     * Logs the object at {@link Level#DEBUG}.
     *
     * @param object The objects to log.
     */
    public void debug(Object object) { log(Level.DEBUG, String.valueOf(object)); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#DEBUG}.
     *
     * @param objects The objects to log.
     */
    public void debug(Object... objects) { log(Level.DEBUG, objects); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#DEBUG}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void debug(String format, Object... objects) { log(Level.DEBUG, format, objects); }
    
    /**
     * Logs the object at {@link Level#TRACE}.
     *
     * @param object The objects to log.
     */
    public void trace(Object object) { log(Level.TRACE, String.valueOf(object)); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#TRACE}.
     *
     * @param objects The objects to log.
     */
    public void trace(Object... objects) { log(Level.TRACE, objects); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#TRACE}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void trace(String format, Object... objects) { log(Level.TRACE, format, objects); }
    
    /**
     * Logs the object at {@link Level#ALL}.
     *
     * @param object The objects to log.
     */
    public void all(Object object) { log(Level.ALL, String.valueOf(object)); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#ALL}.
     *
     * @param objects The objects to log.
     */
    public void all(Object... objects) { log(Level.ALL, objects); }
    
    /**
     * Logs the objects separated by spaces at {@link Level#ALL}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void all(String format, Object... objects) { log(Level.ALL, format, objects); }
    
    public enum Level
    {
        OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL
    }
}
