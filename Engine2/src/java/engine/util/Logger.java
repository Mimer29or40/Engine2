package engine.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;
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
    private static final Logger LOGGER = new Logger();
    
    private static final Pattern      PATTERN = Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])"); // Taken from java.lang.Formatter
    private static final OutputStream OUT     = new BufferedOutputStream(System.out);
    
    private static Level level = Level.INFO;
    
    private static final HashMap<String, Pattern> WHITELIST = new HashMap<>();
    private static final HashMap<String, Pattern> BLACKLIST = new HashMap<>();
    
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
        Logger.LOGGER.finest("Setting Global Log Level", level);
        
        Logger.level = level;
    }
    
    /**
     * Adds a filter to the loggers. Only Loggers in this filter will be logged.
     *
     * @param regex The filter string.
     */
    public static void addWhitelistFilter(String regex)
    {
        Logger.LOGGER.finest("Adding filter the Whitelist", regex);
        
        Logger.WHITELIST.put(regex, Pattern.compile(regex));
    }
    
    /**
     * Removes the filter from the Whitelist.
     *
     * @param regex The filter String.
     */
    public static void removeWhitelistFilter(String regex)
    {
        Logger.LOGGER.finest("Removing filter from the Whitelist", regex);
        
        Logger.WHITELIST.remove(regex);
    }
    
    /**
     * Adds a filter to the loggers. Only Loggers not in this filter will be logged.
     *
     * @param regex The filter string.
     */
    public static void addBlacklistFilter(String regex)
    {
        Logger.LOGGER.finest("Adding filter to the Blacklist", regex);
        
        Logger.BLACKLIST.put(regex, Pattern.compile(regex));
    }
    
    /**
     * Removes the filter from the Blacklist.
     *
     * @param regex The filter String.
     */
    public static void removeBlacklistFilter(String regex)
    {
        Logger.LOGGER.finest("Removing filter from the Blacklist", regex);
        
        Logger.BLACKLIST.remove(regex);
    }
    
    private static boolean applyFilters(String name)
    {
        for (Pattern pattern : Logger.BLACKLIST.values())
        {
            if (pattern.matcher(name).find()) return true;
        }
        if (Logger.WHITELIST.size() > 0)
        {
            for (Pattern pattern : Logger.WHITELIST.values())
            {
                if (pattern.matcher(name).find()) return false;
            }
            return true;
        }
        return false;
    }
    
    private final String name;
    
    /**
     * Creates a new logger whose name is the class path to the file.
     */
    public Logger()
    {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        
        this.name = elements.length > 2 ? elements[2].getClassName() : "";
    }
    
    private void log(Level level, String message)
    {
        if (level.intValue() < Logger.level.intValue()) return;
        if (applyFilters(this.name)) return;
        try
        {
            String prefix = '[' + getCurrentTimeString() + "] [" + Thread.currentThread().getName() + '/' + level + "]";
            if (!this.name.equals("")) prefix += " [" + this.name + "]";
            prefix += ": ";
            for (String line : message.split("\n"))
            {
                Logger.OUT.write(prefix.getBytes());
                Logger.OUT.write((line + '\n').getBytes());
            }
            Logger.OUT.flush();
        }
        catch (IOException ignored) { }
    }
    
    /**
     * Logs the objects separated by spaces at the level specified.
     *
     * @param level   The level to log at.
     * @param objects The objects to log.
     */
    public void log(Level level, Object... objects)
    {
        if (level.intValue() < Logger.level.intValue()) return;
        if (applyFilters(this.name)) return;
        int n = objects.length;
        if (n == 0) return;
        StringBuilder builder = new StringBuilder(String.valueOf(objects[0]));
        for (int i = 1; i < n; i++) builder.append(' ').append(objects[i]);
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
        if (level.intValue() < Logger.level.intValue()) return;
        if (applyFilters(this.name)) return;
        if (Logger.PATTERN.matcher(format).find())
        {
            log(level, String.format(format, objects));
        }
        else
        {
            StringBuilder builder = new StringBuilder(format);
            for (Object object : objects) builder.append(' ').append(object);
            log(level, builder.toString());
        }
    }
    
    /**
     * Logs the object at {@link Level#SEVERE}.
     *
     * @param object The objects to log.
     */
    public void severe(Object object)
    {
        log(Level.SEVERE, String.valueOf(object));
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#SEVERE}.
     *
     * @param objects The objects to log.
     */
    public void severe(Object... objects)
    {
        log(Level.SEVERE, objects);
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#SEVERE}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void severe(String format, Object... objects)
    {
        log(Level.SEVERE, format, objects);
    }
    
    /**
     * Logs the object at {@link Level#WARNING}.
     *
     * @param object The objects to log.
     */
    public void warning(Object object)
    {
        log(Level.WARNING, String.valueOf(object));
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#WARNING}.
     *
     * @param objects The objects to log.
     */
    public void warning(Object... objects)
    {
        log(Level.WARNING, objects);
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#WARNING}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void warning(String format, Object... objects)
    {
        log(Level.WARNING, format, objects);
    }
    
    /**
     * Logs the object at {@link Level#INFO}.
     *
     * @param object The objects to log.
     */
    public void info(Object object)
    {
        log(Level.INFO, String.valueOf(object));
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#INFO}.
     *
     * @param objects The objects to log.
     */
    public void info(Object... objects)
    {
        log(Level.INFO, objects);
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#INFO}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void info(String format, Object... objects)
    {
        log(Level.INFO, format, objects);
    }
    
    /**
     * Logs the object at {@link Level#CONFIG}.
     *
     * @param object The objects to log.
     */
    public void config(Object object)
    {
        log(Level.CONFIG, String.valueOf(object));
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#CONFIG}.
     *
     * @param objects The objects to log.
     */
    public void config(Object... objects)
    {
        log(Level.CONFIG, objects);
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#CONFIG}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void config(String format, Object... objects)
    {
        log(Level.CONFIG, format, objects);
    }
    
    /**
     * Logs the object at {@link Level#FINE}.
     *
     * @param object The objects to log.
     */
    public void fine(Object object)
    {
        log(Level.FINE, String.valueOf(object));
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#FINE}.
     *
     * @param objects The objects to log.
     */
    public void fine(Object... objects)
    {
        log(Level.FINE, objects);
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#FINE}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void fine(String format, Object... objects)
    {
        log(Level.FINE, format, objects);
    }
    
    /**
     * Logs the object at {@link Level#FINER}.
     *
     * @param object The objects to log.
     */
    public void finer(Object object)
    {
        log(Level.FINER, String.valueOf(object));
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#FINER}.
     *
     * @param objects The objects to log.
     */
    public void finer(Object... objects)
    {
        log(Level.FINER, objects);
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#FINER}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void finer(String format, Object... objects)
    {
        log(Level.FINER, format, objects);
    }
    
    /**
     * Logs the object at {@link Level#FINEST}.
     *
     * @param object The objects to log.
     */
    public void finest(Object object)
    {
        log(Level.FINEST, String.valueOf(object));
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#FINEST}.
     *
     * @param objects The objects to log.
     */
    public void finest(Object... objects)
    {
        log(Level.FINEST, objects);
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#FINEST}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void finest(String format, Object... objects)
    {
        log(Level.FINEST, format, objects);
    }
    
    /**
     * Logs the object at {@link Level#ALL}.
     *
     * @param object The objects to log.
     */
    public void all(Object object)
    {
        log(Level.ALL, String.valueOf(object));
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#ALL}.
     *
     * @param objects The objects to log.
     */
    public void all(Object... objects)
    {
        log(Level.ALL, objects);
    }
    
    /**
     * Logs the objects separated by spaces at {@link Level#ALL}. If the format string has format characters in it, then it will be used to format the objects.
     *
     * @param format  The optional format string.
     * @param objects The objects to log.
     */
    public void all(String format, Object... objects)
    {
        log(Level.ALL, format, objects);
    }
}
