package engine.event;

import engine.Keyboard;

public interface EventKeyboardKeyRepeated extends EventInputDeviceInputRepeated, EventKeyboardKey
{
    final class _EventKeyboardKeyRepeated extends AbstractEventKeyboardKey implements EventKeyboardKeyRepeated
    {
        private _EventKeyboardKeyRepeated(Keyboard.Key key)
        {
            super(key);
        }
    }
    
    static EventKeyboardKeyRepeated create(Keyboard.Key key)
    {
        return new _EventKeyboardKeyRepeated(key);
    }
}
