package engine.event;

public class EventKeyboardKeyTyped extends Event
{
    public EventKeyboardKeyTyped(Object[] values)
    {
        super(new String[] {"char"}, values);
    }
    
    public char charTyped()
    {
        return (char) this.values[0];
    }
}
