package engine.event;

import engine.input.Keyboard;

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
