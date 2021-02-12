package engine.event;

public interface EventWindowIconified extends EventWindow
{
    @Property
    boolean iconified();
    
    final class _EventWindowIconified extends AbstractEventWindow implements EventWindowIconified
    {
        private final boolean iconified;
        
        private _EventWindowIconified(boolean iconified)
        {
            super();
    
            this.iconified = iconified;
        }
        
        @Override
        public boolean iconified()
        {
            return this.iconified;
        }
    }
    
    static EventWindowIconified create(boolean iconified)
    {
        return new _EventWindowIconified(iconified);
    }
}
