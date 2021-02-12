package engine.event;

import engine.Keyboard;

public interface EventKeyboardKeyPressed extends EventInputDeviceInputPressed, EventKeyboardKey
{
    final class _EventKeyboardKeyPressed extends AbstractEventKeyboardKey implements EventKeyboardKeyPressed
    {
        private final boolean doublePressed;
    
        private _EventKeyboardKeyPressed(Keyboard.Key key, boolean doublePressed)
        {
            super(key);
    
            this.doublePressed = doublePressed;
        }
    
        @Override
        public boolean doublePressed()
        {
            return this.doublePressed;
        }
    }
    
    static EventKeyboardKeyPressed create(Keyboard.Key key, boolean doublePressed)
    {
        return new _EventKeyboardKeyPressed(key, doublePressed);
    }
}
