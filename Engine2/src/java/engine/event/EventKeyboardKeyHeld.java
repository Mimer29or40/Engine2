package engine.event;

import engine.input.Keyboard;

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
