package engine.event;

public interface EventWindowVsyncChanged extends EventWindow
{
    @Property
    boolean vsync();
    
    final class _EventWindowVsyncChanged extends AbstractEventWindow implements EventWindowVsyncChanged
    {
        private final boolean vsync;
        
        private _EventWindowVsyncChanged(boolean vsync)
        {
            super();
    
            this.vsync = vsync;
        }
        
        @Override
        public boolean vsync()
        {
            return this.vsync;
        }
    }
    
    static EventWindowVsyncChanged create(boolean maximized)
    {
        return new _EventWindowVsyncChanged(maximized);
    }
}
