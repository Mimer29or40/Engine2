package engine.event;

import engine.input.Keyboard;

/**
 * This event is generated when ever a key is released, with the key that was released.
 */
public class EventKeyboardKeyUp extends Event
{
    public EventKeyboardKeyUp(Object[] values)
    {
        super(new String[] {""}, values);
    }
    
    public Keyboard.Key key()
    {
        return (Keyboard.Key) this.values[0];
    }
}
