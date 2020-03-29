package engine.event;

import engine.input.Keyboard;

/**
 * This event is generated when ever a key is pressed down, with the key that was pressed.
 */
public class EventKeyboardKeyDown extends Event
{
    public EventKeyboardKeyDown(Object[] values)
    {
        super(new String[] {""}, values);
    }
    
    public Keyboard.Key key()
    {
        return (Keyboard.Key) this.values[0];
    }
}
