package engine.event;

import engine.Gamepad;
import engine.Joystick;

public interface EventGamepadHat extends EventJoystickHat, EventGamepad
{
    final class _EventGamepadHat extends AbstractEventJoystick implements EventGamepadHat
    {
        private final int          hat;
        private final Joystick.Hat state;
        
        private _EventGamepadHat(Gamepad gamepad, int hat, Joystick.Hat state)
        {
            super(gamepad);
            
            this.hat   = hat;
            this.state = state;
        }
        
        @Override
        public int hat()
        {
            return this.hat;
        }
        
        @Override
        public Joystick.Hat state()
        {
            return this.state;
        }
    }
    
    static EventGamepadHat create(Gamepad gamepad, int hat, Joystick.Hat state)
    {
        return new _EventGamepadHat(gamepad, hat, state);
    }
}
