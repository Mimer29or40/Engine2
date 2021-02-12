package engine.event;

public interface EventKeyboardTyped extends EventKeyboard
{
    @Property
    String typed();
    
    final class _EventKeyboardTyped extends AbstractEventInputDevice implements EventKeyboardTyped
    {
        private final String typed;
    
        private _EventKeyboardTyped(String typed)
        {
            super();
        
            this.typed = typed;
        }
    
        @Override
        public String typed()
        {
            return this.typed;
        }
    }
    
    static EventKeyboardTyped create(String typed)
    {
        return new _EventKeyboardTyped(typed);
    }
}
