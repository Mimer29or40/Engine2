package engine.util;

import java.util.*;
import java.util.function.Function;

import static engine.util.Util.round;

/**
 * A profiler that tracks the time taken to perform a task. It can track groups and sub groups.
 */
public class Profiler
{
    private static final Logger LOGGER = new Logger();
    
    private static final long WARN_TIME_THRESHOLD = 100_000_000L;
    
    private final String root;
    
    private final Stack<Pair<String, Long>> sections = new Stack<>();
    private final Map<String, Long>         times    = new HashMap<>();
    
    public  boolean enabled = false;
    private boolean started = false;
    
    /**
     * Creates a new Profiler.
     *
     * @param root The root group name.
     */
    public Profiler(String root)
    {
        this.root = root;
    }
    
    /**
     * Starts the profiler frame. The profiler must be enabled and the frame must be stopped.
     */
    public void startFrame()
    {
        if (this.enabled)
        {
            if (this.started)
            {
                Profiler.LOGGER.error("Profiler tick already started");
            }
            else
            {
                this.started = true;
                this.sections.clear();
                this.times.clear();
                startSection(this.root);
            }
        }
    }
    
    /**
     * Ends the profiler frame. The profiler must be enabled and the frame must have been started.
     */
    public void endFrame()
    {
        if (this.enabled)
        {
            if (!this.started)
            {
                Profiler.LOGGER.error("Profiler tick already ended");
            }
            else
            {
                endSection();
                this.started = false;
                if (!this.sections.isEmpty())
                {
                    Profiler.LOGGER.warn("Profiler tick ended before path was fully popped (remainder: '%s')", this.sections.peek());
                }
            }
        }
    }
    
    /**
     * Begins a section to start timing it. Must be paired with {@link #endSection()}. You can call again with a unique name to start a sub section.
     *
     * @param section The unique section name.
     */
    public void startSection(String section)
    {
        if (this.enabled)
        {
            if (!this.started)
            {
                Profiler.LOGGER.error("Cannot push '%s' to profiler if profiler tick hasn't started", section);
            }
            else
            {
                String parent = !this.sections.isEmpty() ? this.sections.peek().a + '.' : "";
                
                this.sections.push(new Pair<>(parent + section, System.nanoTime()));
                
                Profiler.LOGGER.trace("Starting Section: %s", parent + section);
            }
        }
    }
    
    /**
     * Ends a section and records the time since {@link #startSection} was called. Must be paired with {@link #endSection()}.
     */
    public void endSection()
    {
        if (this.enabled)
        {
            if (!this.started)
            {
                Profiler.LOGGER.error("Cannot pop from profiler if profiler tick hasn't started");
            }
            else if (this.sections.isEmpty())
            {
                Profiler.LOGGER.error("Tried to pop one too many times");
            }
            else
            {
                Pair<String, Long> data = this.sections.pop();
                
                String section = data.a;
                long   delta   = System.nanoTime() - data.b;
                this.times.put(section, delta);
                if (delta > WARN_TIME_THRESHOLD)
                {
                    Profiler.LOGGER.warn("Something's taking too long! '%s' took approx %s us", section, delta / 1_000D);
                }
                
                Profiler.LOGGER.trace("Ending Section: %s", section);
            }
        }
    }
    
    /**
     * Gets the raw data for the section and all of its children and grandchildren.
     *
     * @param parent The section.
     * @return The list of data points for the parent.
     */
    public List<FrameData> getData(String parent)
    {
        final String p = parent == null || parent.equals("") ? this.root : parent;
        
        Function<String, Boolean> check = (s) -> s.startsWith(p + ".") && !s.replaceAll(p + ".", "").contains(".");
        
        long actualTotal = 0;
        for (String section : this.times.keySet()) if (check.apply(section)) actualTotal += this.times.get(section);
        
        long parentTotal = this.times.get(p);
        long globalTotal = Math.max(this.times.get(this.root), parentTotal);
        
        long total = Math.max(actualTotal, parentTotal);
        
        List<FrameData> data = new ArrayList<>();
        for (String section : this.times.keySet())
        {
            long time = this.times.get(section);
            if (check.apply(section))
            {
                double percent  = round(((double) time / (double) total) * 100D, 3);
                double gPercent = round(((double) time / (double) globalTotal) * 100D, 3);
                data.add(new FrameData(section, time, percent, gPercent));
            }
        }
        
        if (parentTotal > actualTotal && !data.isEmpty())
        {
            long   time     = parentTotal - actualTotal;
            double percent  = round(((double) time / (double) total) * 100D, 3);
            double gPercent = round(((double) time / (double) globalTotal) * 100D, 3);
            data.add(new FrameData(p + ".Unspecified", time, percent, gPercent));
        }
        
        Collections.sort(data);
        FrameData pData = new FrameData(p, parentTotal, 100, round((double) parentTotal / (double) globalTotal * 100, 3));
        data.add(0, pData);
        
        return data;
    }
    
    /**
     * Gets a formatted string of the data points for the parent and its children.
     *
     * @param parent The section.
     * @return The formatted string.
     */
    public String getFormattedData(String parent)
    {
        final String  p       = parent == null || parent.equals("") ? this.root : parent;
        StringBuilder builder = new StringBuilder();
        format(0, p, builder, true);
        return builder.toString();
    }
    
    private void format(int level, String base, StringBuilder builder, boolean header)
    {
        List<FrameData> data = this.getData(base);
        for (int i = header ? 1 : 0; i < data.size(); ++i)
        {
            FrameData point = data.get(i);
            builder.append(String.format("[%02d] ", level));
            builder.append("|   ".repeat(Math.max(0, level)));
            if (point.name.contains("."))
            {
                builder.append(point.name.substring(point.name.indexOf(".") + 1)).append(": ");
            }
            else
            {
                builder.append(point.name).append(": ");
            }
            builder.append(point.time / 1000).append("us ");
            builder.append(String.format("%.3f", point.percentage)).append("% / ");
            builder.append(String.format("%.3f", point.globalPercentage)).append("%)\n");
            if (point.name.equals(base))
            {
                level += 1;
            }
            else if (base == null || !point.name.equals(base + ".Unspecified"))
            {
                try
                {
                    format(level + 1, point.name, builder, false);
                }
                catch (Exception e)
                {
                    builder.append("[[ EXCEPTION ").append(e).append(" ]]");
                }
            }
        }
    }
    
    public static class FrameData implements Comparable<FrameData>
    {
        public final String name;
        public final long   time;
        public final double percentage;
        public final double globalPercentage;
        
        private FrameData(String name, long time, double percentage, double globalPercentage)
        {
            this.name             = name;
            this.time             = time;
            this.percentage       = percentage;
            this.globalPercentage = globalPercentage;
        }
        
        public int compareTo(FrameData o)
        {
            return o.percentage < this.percentage ? -1 : o.percentage > this.percentage ? 1 : o.name.compareTo(this.name);
        }
    }
}
