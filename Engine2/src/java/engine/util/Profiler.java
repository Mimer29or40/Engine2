package engine.util;

import java.util.*;
import java.util.function.Function;

import static engine.util.Util.round;

/**
 * A profiler that tracks the time taken to perform a task. It can track groups and sub groups.
 */
@SuppressWarnings("unused")
public class Profiler
{
    private static final Logger LOGGER = new Logger();
    
    private static final long WARN_TIME_THRESHOLD = 100_000_000L;
    
    private boolean enabled, newEnabled, started;
    
    private final Stack<Pair<String, Long>> sections = new Stack<>();
    
    private final ArrayList<Long>                  frameTimeList    = new ArrayList<>();
    private final HashMap<String, ArrayList<Long>> sectionsTimeList = new HashMap<>();
    
    /**
     * @return If the profiler is enabled.
     */
    public boolean enabled()
    {
        return this.enabled;
    }
    
    /**
     * Sets the profiler to enabled/disabled.
     *
     * @param enabled The new enabled state.
     */
    public void enabled(boolean enabled)
    {
        this.newEnabled = enabled;
    }
    
    /**
     * Toggles the enabled state.
     */
    public void toggleEnabled()
    {
        enabled(!this.enabled);
    }
    
    /**
     * Clears the data.
     */
    public void clear()
    {
        this.frameTimeList.clear();
        this.sectionsTimeList.clear();
    }
    
    /**
     * Starts the profiler frame. The profiler must be enabled and the frame must be stopped.
     */
    public void startFrame()
    {
        this.enabled = this.newEnabled;
        if (this.enabled)
        {
            if (this.started)
            {
                Profiler.LOGGER.warning("Profiler frame already started");
            }
            else
            {
                Profiler.LOGGER.finest("Starting Frame");
                
                this.sections.clear();
                
                this.frameTimeList.add(System.nanoTime());
                
                this.started = true;
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
                Profiler.LOGGER.warning("Profiler frame was never started.");
            }
            else
            {
                Profiler.LOGGER.finest("Ending Frame");
                
                this.frameTimeList.add(System.nanoTime() - this.frameTimeList.remove(this.frameTimeList.size() - 1));
                
                this.started = false;
                if (!this.sections.isEmpty()) Profiler.LOGGER.warning("Profiler frame ended before all sections were ended (remainder: '%s')", this.sections.peek());
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
                Profiler.LOGGER.warning("Cannot start '%s' to profiler because profiler frame wasn't started.", section);
            }
            else
            {
                String name = (!this.sections.isEmpty() ? this.sections.peek().a + '.' : "") + section;
                
                Profiler.LOGGER.finest("Starting Section:", name);
                
                this.sections.push(new Pair<>(name, System.nanoTime()));
            }
        }
    }
    
    /**
     * Ends a section and records the time since {@link #startSection} was called. Must be paired with {@link #startSection}.
     */
    public void endSection()
    {
        if (this.enabled)
        {
            if (!this.started)
            {
                Profiler.LOGGER.warning("Cannot stop section because profiler frame wasn't started.");
            }
            else if (this.sections.isEmpty())
            {
                Profiler.LOGGER.warning("No section was started.");
            }
            else
            {
                Pair<String, Long> data = this.sections.pop();
                
                Profiler.LOGGER.finest("Ending Section:", data.a);
                
                long sectionTime = System.nanoTime() - data.b;
                this.sectionsTimeList.putIfAbsent(data.a, new ArrayList<>());
                this.sectionsTimeList.get(data.a).add(sectionTime);
                
                if (sectionTime > WARN_TIME_THRESHOLD) Profiler.LOGGER.warning("'%s' took approx %s us", data.a, sectionTime / 1_000D);
            }
        }
    }
    
    /**
     * Gets a multiline string that shows the average, minimum, and maximum for each section over the number of frames profiled.
     * <p>
     * All child sections will be included in the string.
     *
     * @param parent The parent section to collect or null for the entire data set.
     * @return The multiline string.
     */
    public String getAvgDataString(String parent)
    {
        return this.enabled ? format(0, parent, new StringBuilder(), true, this::getAverageData).toString() : null;
    }
    
    /**
     * Gets a multiline string that shows the frame that took the minimum amount of time.
     * <p>
     * All child sections will be included in the string with the percentage that the child took in the parent
     * as well as the percentage of the frame time taken.
     *
     * @param parent The parent section to collect or null for the entire data set.
     * @return The multiline string.
     */
    public String getMinDataString(String parent)
    {
        return this.enabled ? format(0, parent, new StringBuilder(), true, this::getMinData).toString() : null;
    }
    
    /**
     * Gets a multiline string that shows the frame that took the maximum amount of time.
     * <p>
     * All child sections will be included in the string with the percentage that the child took in the parent
     * as well as the percentage of the frame time taken.
     *
     * @param parent The parent section to collect or null for the entire data set.
     * @return The multiline string.
     */
    public String getMaxDataString(String parent)
    {
        return this.enabled ? format(0, parent, new StringBuilder(), true, this::getMaxData).toString() : null;
    }
    
    private StringBuilder format(int level, String parent, StringBuilder builder, boolean header, Function<String, List<? extends Data>> points)
    {
        List<? extends Data> apply = points.apply(parent);
        for (int i = header ? 0 : 1, n = apply.size(); i < n; i++)
        {
            Data point = apply.get(i);
            // builder.append(String.format("[%02d] ", level));
            builder.append("|   ".repeat(Math.max(0, level)));
            builder.append(point.name.contains(".") ? point.name.substring(point.name.lastIndexOf(".") + 1) : point.name);
            builder.append(" - ").append(point.valueString()).append('\n');
            if (point.name.equals(parent) || point.name.equals("Frame"))
            {
                level += 1;
            }
            else if (!point.name.contains("Unspecified"))
            {
                try
                {
                    format(level + 1, point.name, builder, false, points);
                }
                catch (Exception e)
                {
                    builder.append("[[ EXCEPTION ").append(e).append(" ]]");
                }
            }
        }
        return builder;
    }
    
    /**
     * Gets a ArrayList of data points that shows the average, minimum, and maximum for each section in the parent over the number of frames profiled.
     *
     * @param parent The parent section to collect or null for the top level.
     * @return The ArrayList of data points.
     */
    public List<Data> getAverageData(String parent)
    {
        if (!this.enabled) return new ArrayList<>();
        
        Function<String, Boolean> check = s -> (parent == null && !s.contains(".")) || (s.startsWith(parent + '.') && !s.replaceFirst(parent + '.', "").contains("."));
        
        ArrayList<Data> data = new ArrayList<>();
        for (String section : this.sectionsTimeList.keySet())
        {
            if (check.apply(section))
            {
                ArrayList<Long> times = this.sectionsTimeList.get(section);
                
                long minTime   = Long.MAX_VALUE;
                long maxTime   = Long.MIN_VALUE;
                long totalTime = 0;
                
                for (long time : times)
                {
                    minTime = Math.min(minTime, time);
                    maxTime = Math.max(maxTime, time);
                    totalTime += time;
                }
                
                data.add(new SectionData(section, totalTime / times.size(), minTime, maxTime));
            }
        }
        
        long minTime   = Long.MAX_VALUE;
        long maxTime   = Long.MIN_VALUE;
        long totalTime = 0;
        
        for (long time : this.frameTimeList)
        {
            minTime = Math.min(minTime, time);
            maxTime = Math.max(maxTime, time);
            totalTime += time;
        }
        data.sort(Collections.reverseOrder());
        data.add(0, new SectionData(parent == null ? "Frame" : parent, totalTime / this.frameTimeList.size(), minTime, maxTime));
        return data;
    }
    
    /**
     * Gets a ArrayList of data points that shows the frame that the parent took the minimum amount of time to execute.
     * </p>
     * The child sections will be included in the string with the percentage that the child took in the parent
     * as well as the percentage of the frame time taken.
     *
     * @param parent The parent section to collect or null for the top level.
     * @return The ArrayList of data points.
     */
    public List<Data> getMinData(String parent)
    {
        if (!this.enabled) return new ArrayList<>();
        
        ArrayList<Long> data = parent != null ? this.sectionsTimeList.get(parent) : this.frameTimeList;
        
        long min = Long.MAX_VALUE;
        int  idx = 0;
        for (int i = 0, n = data.size(); i < n; i++)
        {
            long value = data.get(i);
            if (value < min)
            {
                min = value;
                idx = i;
            }
        }
        return getFrameData(idx, parent);
    }
    
    /**
     * Gets a ArrayList of data points that shows the frame that the parent took the maximum amount of time to execute.
     * </p>
     * The child sections will be included in the string with the percentage that the child took in the parent
     * as well as the percentage of the frame time taken.
     *
     * @param parent The parent section to collect or null for the top level.
     * @return The ArrayList of data points.
     */
    public List<Data> getMaxData(String parent)
    {
        if (!this.enabled) return new ArrayList<>();
        
        ArrayList<Long> data = parent != null ? this.sectionsTimeList.get(parent) : this.frameTimeList;
        
        long max = Long.MIN_VALUE;
        int  idx = 0;
        for (int i = 0, n = data.size(); i < n; i++)
        {
            long value = data.get(i);
            if (value > max)
            {
                max = value;
                idx = i;
            }
        }
        return getFrameData(idx, parent);
    }
    
    private List<Data> getFrameData(int frame, String parent)
    {
        Function<String, Boolean> check = s -> (parent == null && !s.contains(".")) || (s.startsWith(parent + '.') && !s.replaceAll(parent + '.', "").contains("."));
        
        long actualTotal = 0;
        for (String section : this.sectionsTimeList.keySet()) if (check.apply(section)) actualTotal += this.sectionsTimeList.get(section).get(frame);
        
        long parentTotal = (parent != null ? this.sectionsTimeList.get(parent) : this.frameTimeList).get(frame);
        long globalTotal = Math.max(this.frameTimeList.get(frame), parentTotal);
        
        long total = Math.max(actualTotal, parentTotal);
        
        ArrayList<Data> data = new ArrayList<>();
        for (String section : this.sectionsTimeList.keySet())
        {
            if (check.apply(section))
            {
                long   time     = this.sectionsTimeList.get(section).get(frame);
                double percent  = round(((double) time / (double) total) * 100D, 3);
                double gPercent = round(((double) time / (double) globalTotal) * 100D, 3);
                data.add(new SectionPercent(section, time, percent, gPercent));
            }
        }
        
        if (parentTotal > actualTotal && !data.isEmpty())
        {
            long   time     = parentTotal - actualTotal;
            double percent  = round(((double) time / (double) total) * 100D, 3);
            double gPercent = round(((double) time / (double) globalTotal) * 100D, 3);
            data.add(new SectionPercent(parent != null ? parent + ".Unspecified" : "Unspecified", time, percent, gPercent));
        }
        
        data.sort(Collections.reverseOrder());
        data.add(0, new SectionPercent(parent == null ? "Frame" : parent, parentTotal, 100, round((double) parentTotal / (double) globalTotal * 100, 3)));
        return data;
    }
    
    public static abstract class Data implements Comparable<Data>
    {
        public final String name;
        
        private Data(String name)
        {
            this.name = name;
        }
        
        public abstract long value();
        
        public abstract String valueString();
        
        public int compareTo(Data o)
        {
            return value() < o.value() ? -1 : value() > o.value() ? 1 : this.name.compareTo(o.name);
        }
    }
    
    public static class SectionData extends Data
    {
        public final long avgTime, minTime, maxTime;
        
        private SectionData(String name, long avgTime, long minTime, long maxTime)
        {
            super(name);
            this.avgTime = avgTime / 1000;
            this.minTime = minTime / 1000;
            this.maxTime = maxTime / 1000;
        }
        
        @Override
        public long value()
        {
            return this.avgTime;
        }
        
        @Override
        public String valueString()
        {
            return String.format("Avg: % 6d us Min: % 6d us Max: % 6d us", this.avgTime, this.minTime, this.maxTime);
        }
    }
    
    public static class SectionPercent extends Data
    {
        public final long   time;
        public final double percentage, globalPercentage;
        
        private SectionPercent(String name, long time, double percentage, double globalPercentage)
        {
            super(name);
            this.time             = time / 1000;
            this.percentage       = percentage;
            this.globalPercentage = globalPercentage;
        }
        
        @Override
        public long value()
        {
            return this.time;
        }
        
        @Override
        public String valueString()
        {
            return String.format("% 6d us (%3.3f%% / %3.3f%%)", this.time, this.percentage, this.globalPercentage);
        }
    }
}
