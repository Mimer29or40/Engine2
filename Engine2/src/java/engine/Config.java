package engine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rutils.Logger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Config
{
    private static final Logger LOGGER = new Logger();
    
    private static final Map<String, ConfigValue<?>> VALUES = new LinkedHashMap<>();
    
    public static final ConfigValueRange<Integer> LAYER_COUNT            = new ConfigValueRange<>("layer_count",
                                                                                                  "The amount of layers available to draw on.",
                                                                                                  10, 1, 100);
    public static final ConfigValue<String>       DEBUG_TEXT_COLOR       = new ConfigValue<>("debug_text_color",
                                                                                             "The color, with alpha, of the debug text.",
                                                                                             "#FFFFFFFF");
    public static final ConfigValue<String>       DEBUG_BACKGROUND_COLOR = new ConfigValue<>("debug_background_color",
                                                                                             "The color, with alpha, of the box drawn behind debug text.",
                                                                                             "#32000000");
    public static final ConfigValueRange<Double>  NOTIFICATION_DURATION  = new ConfigValueRange<>("notification_duration",
                                                                                                  "The amount of time, in seconds, to display notification text.",
                                                                                                  2.0, 1.0, 10.0);
    public static final ConfigValueRange<Integer> PROFILER_FREQUENCY     = new ConfigValueRange<>("profiler_frequency",
                                                                                                  "The number of times a second the profiler will be refreshed.",
                                                                                                  4, 1, 60);
    public static final ConfigValueRange<Integer> TITLE_FREQUENCY        = new ConfigValueRange<>("title_frequency",
                                                                                                  "The number of times a second the title will be updated with stats.",
                                                                                                  4, 1, 60);
    
    // TODO - GLFW Hints
    // TODO - Window Hints
    
    @SuppressWarnings("unchecked")
    public static <V extends Comparable<? super V>> @Nullable ConfigValue<V> value(@NotNull String tag)
    {
        ConfigValue<V> configValue = (ConfigValue<V>) Config.VALUES.get(tag);
        if (configValue == null)
        {
            Config.LOGGER.warning("Invalid Config Tag: '%s'", tag);
            return null;
        }
        return configValue;
    }
    
    @SuppressWarnings("unchecked")
    public static <V extends Comparable<? super V>> @Nullable V get(@NotNull String tag)
    {
        ConfigValue<V> configValue = (ConfigValue<V>) Config.VALUES.get(tag);
        if (configValue == null)
        {
            Config.LOGGER.warning("Invalid Config Tag: '%s'", tag);
            return null;
        }
        return configValue.get();
    }
    
    @SuppressWarnings("unchecked")
    public static <V extends Comparable<? super V>> void set(@NotNull String tag, @NotNull V obj)
    {
        ConfigValue<V> configValue = (ConfigValue<V>) Config.VALUES.get(tag);
        if (configValue == null)
        {
            Config.LOGGER.warning("Invalid Config Tag: '%s'", tag);
            return;
        }
        configValue.set(obj);
    }
    
    public static class ConfigValue<V>
    {
        private final @NotNull String tag;
        private final @NotNull String description;
        
        private @NotNull V value;
        
        private ConfigValue(@NotNull String tag, @NotNull String description, @NotNull V initial)
        {
            this.tag         = tag;
            this.description = description;
            
            this.value = initial;
            
            Config.VALUES.put(this.tag, this);
        }
        
        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null) return false;
            if (o instanceof ConfigValue<?>)
            {
                ConfigValue<?> that = (ConfigValue<?>) o;
                return this.value.equals(that.value);
            }
            return this.value.equals(o);
        }
        
        @Override
        public int hashCode()
        {
            return Objects.hash(this.value);
        }
        
        @Override
        public String toString()
        {
            return "ConfigValue{" + "tag='" + this.tag + '\'' + ", value=" + this.value + '}';
        }
        
        public String tag()
        {
            return this.tag;
        }
        
        public String description()
        {
            return this.description;
        }
        
        public @NotNull V get()
        {
            return this.value;
        }
        
        public void set(@NotNull V value)
        {
            this.value = value;
        }
    }
    
    public static class ConfigValueRange<V extends Comparable<? super V>> extends ConfigValue<V>
    {
        private final @NotNull V min, max;
        
        private ConfigValueRange(@NotNull String tag, @NotNull String description, @NotNull V initial, @NotNull V min, @NotNull V max)
        {
            super(tag, description, initial);
            
            this.min = min;
            this.max = max;
        }
        
        @Override
        public String toString()
        {
            return "ConfigValue{" + "tag='" + tag() + '\'' + ", value=" + get() + ", range=[" + this.min + ", " + this.max + ']' + '}';
        }
        
        @Override
        public void set(@NotNull V value)
        {
            if (this.min.compareTo(value) > 0 || this.max.compareTo(value) < 0)
            {
                Config.LOGGER.warning("Invalid value (%s) for %s", value, this);
                return;
            }
            super.set(value);
        }
    }
}
