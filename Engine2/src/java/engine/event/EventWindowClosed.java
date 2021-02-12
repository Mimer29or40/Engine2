package engine.event;

public interface EventWindowClosed extends EventWindow
{
    final class _EventWindowClosed extends AbstractEventWindow implements EventWindowClosed { }
    
    static EventWindowClosed create()
    {
        return new _EventWindowClosed();
    }
}
