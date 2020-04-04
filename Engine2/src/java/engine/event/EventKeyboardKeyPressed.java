package engine.event;

import engine.input.Keyboard;

/**
 * This event is generated when ever a key is pressed then released quickly, with the key that was pressed and if it was a double press or not.
 */
@SuppressWarnings("unused")
public class EventKeyboardKeyPressed extends Event
{
    public EventKeyboardKeyPressed(Object[] values)
    {
        super(new String[] {"", "double"}, values);
    }
    
    public Keyboard.Key key()
    {
        return (Keyboard.Key) this.values[0];
    }
    
    public boolean doublePressed()
    {
        return (boolean) this.values[1];
    }
}
