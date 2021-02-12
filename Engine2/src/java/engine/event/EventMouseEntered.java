package engine.event;

public interface EventMouseEntered extends EventMouse
{
    @Property
    boolean entered();
    
    final class _EventMouseEntered extends AbstractEventInputDevice implements EventMouseEntered
    {
        private final boolean entered;
        
        private _EventMouseEntered(boolean entered)
        {
            super();
            
            this.entered = entered;
        }
        
        @Override
        public boolean entered()
        {
            return this.entered;
        }
    }
    
    static EventMouseEntered create(boolean entered)
    {
        return new _EventMouseEntered(entered);
    }
}
