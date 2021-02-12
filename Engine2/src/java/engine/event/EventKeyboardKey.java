package engine.event;

import engine.Keyboard;

public interface EventKeyboardKey extends EventInputDeviceInput, EventKeyboard
{
    @Property(printName = false)
    Keyboard.Key key();
}
