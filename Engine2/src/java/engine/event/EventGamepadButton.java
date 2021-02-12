package engine.event;

import engine.Gamepad;

public interface EventGamepadButton extends EventInputDeviceInput, EventJoystickButton, EventGamepad
{
    Gamepad gamepad();
    
    @Property
    Gamepad.Button gamepadButton();
}
