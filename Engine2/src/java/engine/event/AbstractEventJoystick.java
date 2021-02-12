package engine.event;

import engine.Joystick;

abstract class AbstractEventJoystick extends AbstractEventInputDevice implements EventJoystick
{
    private final Joystick joystick;
    
    AbstractEventJoystick(Joystick joystick)
    {
        this.joystick = joystick;
    }
    
    @Override
    public Joystick joystick()
    {
        return this.joystick;
    }
}
