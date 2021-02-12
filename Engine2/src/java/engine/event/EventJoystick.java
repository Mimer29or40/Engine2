package engine.event;

import engine.Joystick;

public interface EventJoystick extends EventInputDevice
{
    @Property(printName = false)
    Joystick joystick();
}
