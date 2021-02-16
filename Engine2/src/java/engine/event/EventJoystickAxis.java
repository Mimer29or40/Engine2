package engine.event;

import engine.Joystick;

public interface EventJoystickAxis extends EventJoystick
{
    @Property
    int axis();
    
    @Property
    double value();
    
    @Property
    double delta();
    
    final class _EventJoystickAxis extends AbstractEventJoystick implements EventJoystickAxis
    {
        private final int    axis;
        private final double value;
        private final double delta;
    
        private _EventJoystickAxis(Joystick joystick, int axis, double value, double delta)
        {
            super(joystick);
    
            this.axis  = axis;
            this.value = value;
            this.delta = delta;
        }
    
        @Override
        public int axis()
        {
            return this.axis;
        }
    
        @Override
        public double value()
        {
            return this.value;
        }
    
        @Override
        public double delta()
        {
            return this.delta;
        }
    }
    
    static EventJoystickAxis create(Joystick joystick, int axis, double value, double delta)
    {
        return new _EventJoystickAxis(joystick, axis, value, delta);
    }
}
