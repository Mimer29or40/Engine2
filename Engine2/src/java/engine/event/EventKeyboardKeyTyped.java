package engine.event;

/**
 * This event is generated when ever a key is typed, with the character that that key represents. Modifiers are applied.
 */
@SuppressWarnings("unused")
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
