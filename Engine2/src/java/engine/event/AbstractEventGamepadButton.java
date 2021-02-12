package engine.event;

import engine.Gamepad;

abstract class AbstractEventGamepadButton extends AbstractEventJoystickButton implements EventGamepadButton
{
    private final Gamepad.Button button;
    
    AbstractEventGamepadButton(Gamepad gamepad, Gamepad.Button button)
    {
        super(gamepad, button.id());
    
        this.button = button;
    }
    
    public Gamepad gamepad()
    {
        return (Gamepad) joystick();
    }
    
    public Gamepad.Button gamepadButton()
    {
        return this.button;
    }
}
