package engine.event;

import engine.input.Keyboard;

/**
 * This event is generated when ever a key is repeated, with the key that was repeated.
 */
@SuppressWarnings("unused")
public class EventKeyboardKeyRepeat extends Event
{
    public EventKeyboardKeyRepeat(Object[] values)
    {
        super(new String[] {""}, values);
    }
    
    public Keyboard.Key key()
    {
        return (Keyboard.Key) this.values[0];
    }
}
