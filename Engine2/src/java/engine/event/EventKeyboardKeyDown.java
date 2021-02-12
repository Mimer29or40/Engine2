package engine.event;

import engine.Keyboard;

public interface EventKeyboardKeyDown extends EventInputDeviceInputDown, EventKeyboardKey
{
    final class _EventKeyboardKeyDown extends AbstractEventKeyboardKey implements EventKeyboardKeyDown
    {
        private _EventKeyboardKeyDown(Keyboard.Key key)
        {
            super(key);
        }
    }
    
    static EventKeyboardKeyDown create(Keyboard.Key key)
    {
        return new _EventKeyboardKeyDown(key);
    }
}
