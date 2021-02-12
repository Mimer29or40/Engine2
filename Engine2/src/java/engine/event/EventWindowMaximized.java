package engine.event;

public interface EventWindowMaximized extends EventWindow
{
    @Property
    boolean maximized();
    
    final class _EventWindowMaximized extends AbstractEventWindow implements EventWindowMaximized
    {
        private final boolean maximized;
        
        private _EventWindowMaximized(boolean maximized)
        {
            super();
            
            this.maximized = maximized;
        }
        
        @Override
        public boolean maximized()
        {
            return this.maximized;
        }
    }
    
    static EventWindowMaximized create(boolean maximized)
    {
        return new _EventWindowMaximized(maximized);
    }
}
