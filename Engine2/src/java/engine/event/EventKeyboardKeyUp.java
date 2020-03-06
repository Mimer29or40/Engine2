package engine.event;

import engine.input.Keyboard;

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
