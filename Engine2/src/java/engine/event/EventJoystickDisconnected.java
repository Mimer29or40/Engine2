package engine.event;

import engine.Joystick;

public interface EventJoystickDisconnected extends EventJoystick
{
    final class _EventJoystickDisconnected extends AbstractEventJoystick implements EventJoystickDisconnected
    {
        private _EventJoystickDisconnected(Joystick joystick)
        {
            super(joystick);
        }
    }
    
    static EventJoystickDisconnected create(Joystick joystick)
    {
        return new _EventJoystickDisconnected(joystick);
    }
}
