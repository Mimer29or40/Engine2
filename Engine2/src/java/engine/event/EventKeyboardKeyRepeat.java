package engine.event;

import engine.input.Keyboard;

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
