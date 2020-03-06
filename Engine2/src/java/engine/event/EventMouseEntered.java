package engine.event;

@SuppressWarnings("unused")
public class EventMouseEntered extends Event
{
    public EventMouseEntered(Object[] values)
    {
        super(new String[] {"entered"}, values);
    }
    
    public boolean entered()
    {
        return (boolean) this.values[0];
    }
}
