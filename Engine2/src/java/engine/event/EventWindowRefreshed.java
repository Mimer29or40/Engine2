package engine.event;

public interface EventWindowRefreshed extends EventWindow
{
    final class _EventWindowRefreshed extends AbstractEventWindow implements EventWindowRefreshed {}
    
    static EventWindowRefreshed create()
    {
        return new _EventWindowRefreshed();
    }
}
