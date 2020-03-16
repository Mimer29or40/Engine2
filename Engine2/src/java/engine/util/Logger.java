package engine.util;

import java.util.regex.Pattern;

import static engine.util.Util.getCurrentTimeString;

@SuppressWarnings("unused")
public class Logger
{
    private static final Pattern fsPattern = Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])"); // Taken from java.lang.Formatter
    
    private static Level level = Level.INFO;
    
    public static Level getLevel()
    {
        return Logger.level;
    }
    
    public static void setLevel(Level level)
    {
        Logger.level = level;
    }
    
    private final String className;
    
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
    
    public void fatal(Object object)                    { log(Level.FATAL, String.valueOf(object)); }
    
    public void fatal(Object... objects)                { log(Level.FATAL, objects); }
    
    public void fatal(String format, Object... objects) { log(Level.FATAL, format, objects); }
    
    public void error(Object object)                    { log(Level.ERROR, String.valueOf(object)); }
    
    public void error(Object... objects)                { log(Level.ERROR, objects); }
    
    public void error(String format, Object... objects) { log(Level.ERROR, format, objects); }
    
    public void warn(Object object)                     { log(Level.WARN, String.valueOf(object)); }
    
    public void warn(Object... objects)                 { log(Level.WARN, objects); }
    
    public void warn(String format, Object... objects)  { log(Level.WARN, format, objects); }
    
    public void info(Object object)                     { log(Level.INFO, String.valueOf(object)); }
    
    public void info(Object... objects)                 { log(Level.INFO, objects); }
    
    public void info(String format, Object... objects)  { log(Level.INFO, format, objects); }
    
    public void debug(Object object)                    { log(Level.DEBUG, String.valueOf(object)); }
    
    public void debug(Object... objects)                { log(Level.DEBUG, objects); }
    
    public void debug(String format, Object... objects) { log(Level.DEBUG, format, objects); }
    
    public void trace(Object object)                    { log(Level.TRACE, String.valueOf(object)); }
    
    public void trace(Object... objects)                { log(Level.TRACE, objects); }
    
    public void trace(String format, Object... objects) { log(Level.TRACE, format, objects); }
    
    public void all(Object object)                      { log(Level.ALL, String.valueOf(object)); }
    
    public void all(Object... objects)                  { log(Level.ALL, objects); }
    
    public void all(String format, Object... objects)   { log(Level.ALL, format, objects); }
    
    public enum Level
    {
        OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL
    }
}
