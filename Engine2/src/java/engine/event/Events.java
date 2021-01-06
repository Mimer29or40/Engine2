package engine.event;

import engine.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

import static engine.util.Util.join;

/**
 * This class handles the posting and getting of events. Events only exist during the current frame.
 * <p>
 * Events do not get consumed, so they can be processed more than once.
 */
@SuppressWarnings("unused")
public class Events
{
    private static final Logger LOGGER = new Logger();
    
    private static final HashMap<String, String[]>                 REGISTRY      = new HashMap<>();
    private static final HashMap<String, ArrayList<Event>>         EVENTS        = new HashMap<>();
    private static final HashMap<String, HashSet<Consumer<Event>>> SUBSCRIPTIONS = new HashMap<>();
    
    /**
     * Registers an Event with parameters that can be posted.
     *
     * @param eventType  The event type
     * @param parameters The event parameters
     */
    public static void register(String eventType, String[] parameters)
    {
        Events.LOGGER.fine("Registered Event '%s' with parameters %s", eventType, Arrays.toString(parameters));
        
        Events.REGISTRY.put(eventType, parameters);
    }
    
    /**
     * @return All events posted during the current frame.
     */
    public static Iterable<Event> get()
    {
        Events.LOGGER.finest("Getting all Events");
        
        ArrayList<Event> events = new ArrayList<>();
        Events.EVENTS.values().forEach(events::addAll);
        return events;
    }
    
    /**
     * @param eventTypes Event type filter
     * @return All events posted during the current frame that are of the types provided.
     */
    public static Iterable<Event> get(String... eventTypes)
    {
        Events.LOGGER.finest("Getting Events for types", join(eventTypes, ", ", "[", "]"));
        
        ArrayList<Event> events = new ArrayList<>();
        for (String eventType : eventTypes)
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
        Events.LOGGER.finest("Getting Events for groups", join(eventGroups, ", ", "[", "]"));
        
        ArrayList<Event> events = new ArrayList<>();
        for (EventGroup eventGroup : eventGroups)
        {
            for (String eventType : eventGroup.events())
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
    public static void post(String eventType, Object... arguments)
    {
        if (!Events.REGISTRY.containsKey(eventType)) throw new RuntimeException(String.format("Event '%s' not registered", eventType));
        
        Events.EVENTS.computeIfAbsent(eventType, e -> new ArrayList<>());
        Event event = new Event(eventType, Events.REGISTRY.get(eventType), arguments);
        Events.EVENTS.get(eventType).add(event);
        
        Events.LOGGER.finer("Event Posted:", event);
        
        Events.SUBSCRIPTIONS.computeIfAbsent(eventType, e -> new HashSet<>());
        for (Consumer<Event> function : Events.SUBSCRIPTIONS.get(eventType))
        {
            Events.LOGGER.finest("Calling Subscribed Function:", event);
            
            function.accept(event);
        }
    }
    
    /**
     * Subscribe to a specific event so that a function is called as soon as its posted.
     *
     * @param eventType The event type.
     * @param function  The function that will be called.
     */
    public static void subscribe(String eventType, Consumer<Event> function)
    {
        Events.LOGGER.finer("Event Subscription for type:", eventType);
        
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
        for (String eventType : eventGroup.events()) subscribe(eventType, function);
    }
    
    public static void unsubscribe(String eventType, Consumer<Event> function)
    {
        Events.LOGGER.finer("Event Un-subscription for type:", eventType);
        
        Events.SUBSCRIPTIONS.computeIfAbsent(eventType, e -> new HashSet<>());
        Events.SUBSCRIPTIONS.get(eventType).remove(function);
    }
    
    /**
     * Clears all events that were posted.
     */
    public static void clear()
    {
        Events.LOGGER.finest("Events Cleared");
        
        Events.EVENTS.clear();
    }
    
    static
    {
        register(Event.FRAMEBUFFER_RESIZED, new String[] {"size"});
        
        register(Event.WINDOW_FOCUSED, new String[] {"focused"});
        register(Event.WINDOW_FULLSCREEN, new String[] {"fullscreen"});
        register(Event.WINDOW_MOVED, new String[] {"pos"});
        register(Event.WINDOW_RESIZED, new String[] {"size"});
        register(Event.WINDOW_VSYNC, new String[] {"vsync"});
        
        register(Event.MOUSE_BUTTON_CLICKED, new String[] {"button", "pos", "doubleClicked"});
        register(Event.MOUSE_BUTTON_DOWN, new String[] {"button", "pos"});
        register(Event.MOUSE_BUTTON_DRAGGED, new String[] {"button", "dragPos", "pos", "rel"});
        register(Event.MOUSE_BUTTON_HELD, new String[] {"button", "pos"});
        register(Event.MOUSE_BUTTON_REPEAT, new String[] {"button", "pos"});
        register(Event.MOUSE_BUTTON_UP, new String[] {"button", "pos"});
        
        register(Event.MOUSE_CAPTURED, new String[] {"captured"});
        register(Event.MOUSE_ENTERED, new String[] {"entered"});
        register(Event.MOUSE_MOVED, new String[] {"pos", "rel"});
        register(Event.MOUSE_SCROLLED, new String[] {"dir"});
        
        register(Event.KEYBOARD_KEY_PRESSED, new String[] {"key", "doublePressed"});
        register(Event.KEYBOARD_KEY_DOWN, new String[] {"key"});
        register(Event.KEYBOARD_KEY_HELD, new String[] {"key"});
        register(Event.KEYBOARD_KEY_REPEAT, new String[] {"key"});
        register(Event.KEYBOARD_KEY_UP, new String[] {"key"});
        register(Event.KEYBOARD_KEY_TYPED, new String[] {"char"});
        
        EventGroup.WINDOW.add(Event.WINDOW_FOCUSED);
        EventGroup.WINDOW.add(Event.WINDOW_FULLSCREEN);
        EventGroup.WINDOW.add(Event.WINDOW_MOVED);
        EventGroup.WINDOW.add(Event.WINDOW_RESIZED);
        EventGroup.WINDOW.add(Event.WINDOW_VSYNC);
        
        EventGroup.MOUSE_BUTTON.add(Event.MOUSE_BUTTON_CLICKED);
        EventGroup.MOUSE_BUTTON.add(Event.MOUSE_BUTTON_DOWN);
        EventGroup.MOUSE_BUTTON.add(Event.MOUSE_BUTTON_DRAGGED);
        EventGroup.MOUSE_BUTTON.add(Event.MOUSE_BUTTON_HELD);
        EventGroup.MOUSE_BUTTON.add(Event.MOUSE_BUTTON_REPEAT);
        EventGroup.MOUSE_BUTTON.add(Event.MOUSE_BUTTON_UP);
        
        EventGroup.MOUSE.add(Event.MOUSE_CAPTURED);
        EventGroup.MOUSE.add(Event.MOUSE_ENTERED);
        EventGroup.MOUSE.add(Event.MOUSE_MOVED);
        EventGroup.MOUSE.add(Event.MOUSE_SCROLLED);
        
        EventGroup.KEYBOARD_KEY.add(Event.KEYBOARD_KEY_PRESSED);
        EventGroup.KEYBOARD_KEY.add(Event.KEYBOARD_KEY_DOWN);
        EventGroup.KEYBOARD_KEY.add(Event.KEYBOARD_KEY_HELD);
        EventGroup.KEYBOARD_KEY.add(Event.KEYBOARD_KEY_REPEAT);
        EventGroup.KEYBOARD_KEY.add(Event.KEYBOARD_KEY_UP);
        EventGroup.KEYBOARD_KEY.add(Event.KEYBOARD_KEY_TYPED);
        
        EventGroup.MOUSE.add(EventGroup.MOUSE_BUTTON);
        
        EventGroup.KEYBOARD.add(EventGroup.KEYBOARD_KEY);
        
        EventGroup.INPUT.add(EventGroup.MOUSE);
        EventGroup.INPUT.add(EventGroup.KEYBOARD);
    }
}
