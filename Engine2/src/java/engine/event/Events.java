package engine.event;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class Events
{
    public static final EventGroup WINDOW_EVENTS   = new EventGroup(EventWindowFocused.class,
                                                                    EventWindowFullscreen.class,
                                                                    EventWindowMoved.class,
                                                                    EventWindowResized.class,
                                                                    EventWindowVSync.class);
    public static final EventGroup BUTTON_EVENTS   = new EventGroup(EventMouseButtonClicked.class,
                                                                    EventMouseButtonDown.class,
                                                                    EventMouseButtonDragged.class,
                                                                    EventMouseButtonHeld.class,
                                                                    EventMouseButtonRepeat.class,
                                                                    EventMouseButtonUp.class);
    public static final EventGroup MOUSE_EVENTS    = new EventGroup(EventMouseEntered.class, EventMouseMoved.class, EventMouseScrolled.class).addFromGroups(BUTTON_EVENTS);
    public static final EventGroup KEY_EVENTS      = new EventGroup(EventKeyboardKeyPressed.class,
                                                                    EventKeyboardKeyDown.class,
                                                                    EventKeyboardKeyHeld.class,
                                                                    EventKeyboardKeyRepeat.class,
                                                                    EventKeyboardKeyUp.class,
                                                                    EventKeyboardKeyTyped.class);
    public static final EventGroup KEYBOARD_EVENTS = new EventGroup().addFromGroups(KEY_EVENTS);
    public static final EventGroup INPUT_EVENTS    = new EventGroup().addFromGroups(MOUSE_EVENTS, KEY_EVENTS);
    
    private static final HashMap<Class<? extends Event>, ArrayList<Event>>         EVENTS        = new HashMap<>();
    private static final HashMap<Class<? extends Event>, HashSet<Consumer<Event>>> SUBSCRIPTIONS = new HashMap<>();
    
    public static Iterable<Event> get()
    {
        ArrayList<Event> events = new ArrayList<>();
        Events.EVENTS.values().forEach(events::addAll);
        return events;
    }
    
    @SafeVarargs
    public static Iterable<Event> get(Class<? extends Event>... eventTypes)
    {
        ArrayList<Event> events = new ArrayList<>();
        for (Class<? extends Event> eventType : eventTypes)
        {
            Events.EVENTS.computeIfAbsent(eventType, e -> new ArrayList<>());
            events.addAll(Events.EVENTS.get(eventType));
        }
        return events;
    }
    
    public static Iterable<Event> get(EventGroup... eventGroups)
    {
        ArrayList<Event> events = new ArrayList<>();
        for (EventGroup eventGroup : eventGroups)
        {
            for (Class<? extends Event> eventType : eventGroup.getClasses())
            {
                Events.EVENTS.computeIfAbsent(eventType, e -> new ArrayList<>());
                events.addAll(Events.EVENTS.get(eventType));
            }
        }
        return events;
    }
    
    public static <T extends Event> void post(Class<T> eventType, Object... arguments)
    {
        try
        {
            Events.EVENTS.computeIfAbsent(eventType, e -> new ArrayList<>());
            Event event = eventType.getDeclaredConstructor(Object[].class).newInstance(new Object[] {arguments});
            Events.EVENTS.get(eventType).add(event);
            
            Events.SUBSCRIPTIONS.computeIfAbsent(eventType, e -> new HashSet<>());
            for (Consumer<Event> function : Events.SUBSCRIPTIONS.get(eventType))
            {
                function.accept(event);
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored)
        {
        
        }
    }
    
    public static void subscribe(Class<? extends Event> eventType, Consumer<Event> function)
    {
        Events.SUBSCRIPTIONS.computeIfAbsent(eventType, e -> new HashSet<>());
        Events.SUBSCRIPTIONS.get(eventType).add(function);
    }
    
    public static void subscribe(EventGroup eventGroup, Consumer<Event> function)
    {
        for (Class<? extends Event> eventType : eventGroup.getClasses())
        {
            Events.SUBSCRIPTIONS.computeIfAbsent(eventType, e -> new HashSet<>());
            Events.SUBSCRIPTIONS.get(eventType).add(function);
        }
    }
    
    public static void clear()
    {
        Events.EVENTS.clear();
    }
}
