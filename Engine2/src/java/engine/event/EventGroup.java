package engine.event;

import java.util.ArrayList;

/**
 * A group of events. This can be used to filter events from the entire list of events.
 */
public class EventGroup
{
    private final ArrayList<String>     events = new ArrayList<>();
    private final ArrayList<EventGroup> groups = new ArrayList<>();
    
    private final ArrayList<String> cache   = new ArrayList<>();
    private       boolean           rebuild = true;
    
    @Override
    public String toString()
    {
        return "EventGroup{" + this.events + '}';
    }
    
    public EventGroup(Object... eventTypes)
    {
        for (Object type : eventTypes)
        {
            if (type instanceof String)
            {
                this.events.add((String) type);
            }
            else if (type instanceof EventGroup)
            {
                this.groups.add((EventGroup) type);
            }
        }
    }
    
    public Iterable<String> events()
    {
        if (this.rebuild)
        {
            this.cache.clear();
            
            this.cache.addAll(this.events);
            
            for (EventGroup group : this.groups)
            {
                for (String event : group.events())
                {
                    this.cache.add(event);
                }
            }
            
            this.rebuild = false;
        }
        return this.cache;
    }
    
    public void add(String event)
    {
        this.rebuild = true;
        
        this.events.add(event);
    }
    
    public void add(EventGroup eventGroup)
    {
        this.rebuild = true;
        
        this.groups.add(eventGroup);
    }
    
    public static final EventGroup WINDOW       = new EventGroup();
    public static final EventGroup MOUSE_BUTTON = new EventGroup();
    public static final EventGroup MOUSE        = new EventGroup();
    public static final EventGroup KEYBOARD_KEY = new EventGroup();
    public static final EventGroup KEYBOARD     = new EventGroup();
    public static final EventGroup INPUT        = new EventGroup();
}
