package engine.event;

import java.util.Arrays;

/**
 * Generic {@link Event} that represents a group of information related to something happening
 * <p>
 * All events are of type {@code Event} so to delineate different events the parameter {@link Event#type} stores the type.
 * <p>
 * To post an event call {@link Events#post} with the event type string and parameters to ensure that all event
 * listeners are called.
 */
public class Event
{
    private final String   type;
    private final String[] keys;
    private final Object[] values;
    
    public Event(String type, String[] keys, Object[] values)
    {
        if (keys.length != values.length) throw new RuntimeException(String.format("Invalid Parameters: %s.length != %s.length", Arrays.toString(keys), Arrays.toString(values)));
        
        this.type   = type;
        this.keys   = keys;
        this.values = values;
    }
    
    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder(this.type).append("{");
        for (int i = 0, n = this.values.length; i < n; i++)
        {
            if (!this.keys[i].equals("")) s.append(this.keys[i]).append('=');
            s.append(this.values[i]);
            if (i + 1 < n) s.append(' ');
        }
        return s.append("}").toString();
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Event)) return false;
        Event event = (Event) o;
        return Arrays.equals(this.keys, event.keys) && Arrays.equals(this.values, event.values);
    }
    
    @Override
    public int hashCode()
    {
        return 31 * Arrays.hashCode(this.keys) + Arrays.hashCode(this.values);
    }
    
    public String type()
    {
        return this.type;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String parameter)
    {
        for (int i = 0; i < this.keys.length; i++)
        {
            if (parameter.equals(this.keys[i]))
            {
                return (T) this.values[i];
            }
        }
        throw new RuntimeException(String.format("Invalid Parameter: '%s' is not a parameter of event type '%s'", parameter, this.type));
    }
    
    public static final String FRAMEBUFFER_RESIZED = "EventFrameBufferResized";
    
    public static final String WINDOW_FOCUSED    = "EventWindowFocused";
    public static final String WINDOW_FULLSCREEN = "EventWindowFullscreen";
    public static final String WINDOW_MOVED      = "EventWindowMoved";
    public static final String WINDOW_RESIZED    = "EventWindowResized";
    public static final String WINDOW_VSYNC      = "EventWindowVSync";
    
    public static final String MOUSE_BUTTON_CLICKED = "EventMouseButtonClicked";
    public static final String MOUSE_BUTTON_DOWN    = "EventMouseButtonDown";
    public static final String MOUSE_BUTTON_DRAGGED = "EventMouseButtonDragged";
    public static final String MOUSE_BUTTON_HELD    = "EventMouseButtonHeld";
    public static final String MOUSE_BUTTON_REPEAT  = "EventMouseButtonRepeat";
    public static final String MOUSE_BUTTON_UP      = "EventMouseButtonUp";
    
    public static final String MOUSE_CAPTURED = "EventMouseCaptured";
    public static final String MOUSE_ENTERED  = "EventMouseEntered";
    public static final String MOUSE_MOVED    = "EventMouseMoved";
    public static final String MOUSE_SCROLLED = "EventMouseScrolled";
    
    public static final String KEYBOARD_KEY_PRESSED = "EventKeyboardKeyPressed";
    public static final String KEYBOARD_KEY_DOWN    = "EventKeyboardKeyDown";
    public static final String KEYBOARD_KEY_HELD    = "EventKeyboardKeyHeld";
    public static final String KEYBOARD_KEY_REPEAT  = "EventKeyboardKeyRepeat";
    public static final String KEYBOARD_KEY_UP      = "EventKeyboardKeyUp";
    public static final String KEYBOARD_KEY_TYPED   = "EventKeyboardKeyTyped";
}
