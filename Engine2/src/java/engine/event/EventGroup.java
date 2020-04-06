package engine.event;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A group of events. This can be used to filter events from the entire list of events.
 */
public class EventGroup
{
    private final ArrayList<Class<? extends Event>> classes = new ArrayList<>();
    
    @Override
    public String toString()
    {
        return "EventGroup{" + "classes=" + this.classes + '}';
    }
    
    @SafeVarargs
    public EventGroup(Class<? extends Event>... eventTypes)
    {
        this.classes.addAll(Arrays.asList(eventTypes));
    }
    
    public EventGroup addFromGroups(EventGroup... eventGroups)
    {
        for (EventGroup eventGroup : eventGroups)
        {
            for (Class<? extends Event> eventType : eventGroup.getClasses())
            {
                this.classes.add(eventType);
            }
        }
        return this;
    }
    
    public Iterable<Class<? extends Event>> getClasses()
    {
        return this.classes;
    }
}
