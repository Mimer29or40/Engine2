package engine.event;

import engine.Joystick;

public interface EventJoystickButtonDown extends EventInputDeviceInputDown, EventJoystickButton
{
    final class _EventJoystickButtonDown extends AbstractEventJoystickButton implements EventJoystickButtonDown
    {
        private _EventJoystickButtonDown(Joystick joystick, int button)
        {
            super(joystick, button);
        }
    }
    
    static EventJoystickButtonDown create(Joystick joystick, int button)
    {
        return new _EventJoystickButtonDown(joystick, button);
    }
}
