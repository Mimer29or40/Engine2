package engine.event;

public interface EventWindowFocused extends EventWindow
{
    @Property
    boolean focused();
    
    final class _EventWindowFocused extends AbstractEventWindow implements EventWindowFocused
    {
        private final boolean focused;
    
        private _EventWindowFocused(boolean focused)
        {
            super();
    
            this.focused = focused;
        }
    
        @Override
        public boolean focused()
        {
            return this.focused;
        }
    }
    
    static EventWindowFocused create(boolean focused)
    {
        return new _EventWindowFocused(focused);
    }
}
