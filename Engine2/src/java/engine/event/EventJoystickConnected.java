package engine.event;

import engine.Joystick;

public interface EventJoystickConnected extends EventJoystick
{
    final class _EventJoystickConnected extends AbstractEventJoystick implements EventJoystickConnected
    {
        private _EventJoystickConnected(Joystick joystick)
        {
            super(joystick);
        }
    }
    
    static EventJoystickConnected create(Joystick joystick)
    {
        return new _EventJoystickConnected(joystick);
    }
}
