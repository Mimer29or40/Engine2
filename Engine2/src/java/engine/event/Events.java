package engine.event;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * This class handles the posting and getting of events. Events only exist during the current frame.
 * <p>
 * Events do not get consumed, so they can be processed more than once.
 */
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
    
    /**
     * @return All events posted during the current frame.
     */
    public static Iterable<Event> get()
    {
        ArrayList<Event> events = new ArrayList<>();
        Events.EVENTS.values().forEach(events::addAll);
        return events;
    }
    
    /**
     * @param eventTypes Event type filter
     * @return All events posted during the current frame that are of the types provided.
     */
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
    
    /**
     * @param eventGroups EventGroup filters
     * @return All events posted during the current frame that are apart of the EventGroup's provided.
     */
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
    
    /**
     * Posts an event that can be consumed and subscribed to.
     *
     * @param eventType The event class to post.
     * @param arguments The parameters that are passed to the Event class
     */
    public static void post(Class<? extends Event> eventType, Object... arguments)
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
    
    /**
     * Subscribe to a specific event so that a function is called as soon as its posted.
     *
     * @param eventType The event type.
     * @param function  The function that will be called.
     */
    public static void subscribe(Class<? extends Event> eventType, Consumer<Event> function)
    {
        Events.SUBSCRIPTIONS.computeIfAbsent(eventType, e -> new HashSet<>());
        Events.SUBSCRIPTIONS.get(eventType).add(function);
    }
    
    /**
     * Subscribe to an EventGroup so that a function is called as soon as any event in the group is posted.
     *
     * @param eventGroup The event group.
     * @param function   The function that will be called.
     */
    public static void subscribe(EventGroup eventGroup, Consumer<Event> function)
    {
        for (Class<? extends Event> eventType : eventGroup.getClasses())
        {
            Events.SUBSCRIPTIONS.computeIfAbsent(eventType, e -> new HashSet<>());
            Events.SUBSCRIPTIONS.get(eventType).add(function);
        }
    }
    
    /**
     * Clears all events that were posted.
     */
    public static void clear()
    {
        Events.EVENTS.clear();
    }
}
