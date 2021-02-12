package engine.event;

import engine.Gamepad;

public interface EventGamepad extends EventJoystick
{
    default Gamepad gamepad()
    {
        return (Gamepad) joystick();
    }
}
