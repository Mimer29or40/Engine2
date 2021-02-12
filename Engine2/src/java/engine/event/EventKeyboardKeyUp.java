package engine.event;

import engine.Keyboard;

public interface EventKeyboardKeyUp extends EventInputDeviceInputUp, EventKeyboardKey
{
    final class _EventKeyboardKeyUp extends AbstractEventKeyboardKey implements EventKeyboardKeyUp
    {
        private _EventKeyboardKeyUp(Keyboard.Key key)
        {
            super(key);
        }
    }
    
    static EventKeyboardKeyUp create(Keyboard.Key key)
    {
        return new _EventKeyboardKeyUp(key);
    }
}
