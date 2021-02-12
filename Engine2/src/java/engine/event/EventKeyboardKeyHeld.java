package engine.event;

import engine.Keyboard;

public interface EventKeyboardKeyHeld extends EventInputDeviceInputHeld, EventKeyboardKey
{
    final class _EventKeyboardKeyHeld extends AbstractEventKeyboardKey implements EventKeyboardKeyHeld
    {
        private _EventKeyboardKeyHeld(Keyboard.Key key)
        {
            super(key);
        }
    }
    
    static EventKeyboardKeyHeld create(Keyboard.Key key)
    {
        return new _EventKeyboardKeyHeld(key);
    }
}
