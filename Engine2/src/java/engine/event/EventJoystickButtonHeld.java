package engine.event;

import engine.Joystick;

public interface EventJoystickButtonHeld extends EventInputDeviceInputHeld, EventJoystickButton
{
    final class _EventJoystickButtonHeld extends AbstractEventJoystickButton implements EventJoystickButtonHeld
    {
        private _EventJoystickButtonHeld(Joystick joystick, int button)
        {
            super(joystick, button);
        }
    }
    
    static EventJoystickButtonHeld create(Joystick joystick, int button)
    {
        return new _EventJoystickButtonHeld(joystick, button);
    }
}
