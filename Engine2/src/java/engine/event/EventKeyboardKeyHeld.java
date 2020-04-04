package engine.event;

import engine.input.Keyboard;

/**
 * This event is generated when ever a key is held, with the key that was held.
 */
@SuppressWarnings("unused")
public class EventKeyboardKeyHeld extends Event
{
    public EventKeyboardKeyHeld(Object[] values)
    {
        super(new String[] {""}, values);
    }
    
    public Keyboard.Key key()
    {
        return (Keyboard.Key) this.values[0];
    }
}
