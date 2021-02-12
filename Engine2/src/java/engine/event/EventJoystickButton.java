package engine.event;

public interface EventJoystickButton extends EventInputDeviceInput, EventJoystick
{
    @Property
    int button();
}
