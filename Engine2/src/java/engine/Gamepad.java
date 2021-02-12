package engine;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Gamepad extends Joystick
{
    protected final String name;
    
    protected Gamepad(int jid)
    {
        super(jid, true);
        
        this.name = glfwGetGamepadName(this.jid);
        
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            GLFWGamepadState state = GLFWGamepadState.mallocStack(stack);
            
            if (glfwGetGamepadState(jid, state))
            {
                this.axisMap.clear();
                FloatBuffer axes = state.axes();
                for (int i = 0, n = axes.remaining(); i < n; i++) this.axisMap.put(i, new AxisInput(axes.get(i)));
                
                this.buttonMap.clear();
                ByteBuffer buttons = state.buttons();
                for (int i = 0, n = buttons.remaining(); i < n; i++) this.buttonMap.put(i, new Input());
            }
            else
            {
                throw new RuntimeException("Gamepad is not connected.");
            }
        }
    }
    
    /**
     * Returns the human-readable name of the gamepad from the gamepad mapping
     * assigned to the specified joystick.
     *
     * @return the UTF-8 encoded name of the gamepad, or {@code null} if the gamepad is not present, or does not have a mapping.
     */
    public @Nullable String gamepadName()
    {
        return this.name;
    }
    
    @Override
    protected void postAxisEvent(int axis, double value, double delta)
    {
        // GLFW.EVENT_BUS.post(EventGamepadAxis.create(this, Axis.get(axis), value, delta)); // TODO
    }
    
    @Override
    protected void postButtonDownEvent(int button)
    {
        // GLFW.EVENT_BUS.post(EventGamepadButtonDown.create(this, Button.get(button))); // TODO
    }
    
    @Override
    protected void postButtonUpEvent(int button)
    {
        // GLFW.EVENT_BUS.post(EventGamepadButtonUp.create(this, Button.get(button))); // TODO
    }
    
    @Override
    protected void postButtonPressedEvent(int button, boolean doublePressed)
    {
        // GLFW.EVENT_BUS.post(EventGamepadButtonPressed.create(this, Button.get(button), doublePressed)); // TODO
    }
    
    @Override
    protected void postButtonHeldEvent(int button)
    {
        // GLFW.EVENT_BUS.post(EventGamepadButtonHeld.create(this, Button.get(button))); // TODO
    }
    
    @Override
    protected void postButtonRepeatedEvent(int button)
    {
        // GLFW.EVENT_BUS.post(EventGamepadButtonRepeated.create(this, Button.get(button))); // TODO
    }
    
    @Override
    protected void postHatEvent(int hat, int state)
    {
        // GLFW.EVENT_BUS.post(EventGamepadHat.create(this, hat, Hat.get(state))); // TODO
    }
    
    public enum Axis
    {
        NONE(-1),
        
        LEFT_X(GLFW_GAMEPAD_AXIS_LEFT_X),
        LEFT_Y(GLFW_GAMEPAD_AXIS_LEFT_Y),
        
        RIGHT_X(GLFW_GAMEPAD_AXIS_RIGHT_X),
        RIGHT_Y(GLFW_GAMEPAD_AXIS_RIGHT_Y),
        
        LEFT_TRIGGER(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER),
        RIGHT_TRIGGER(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER),
        ;
        
        private final int id;
        
        Axis(int id)
        {
            this.id = id;
        }
        
        public int id()
        {
            return this.id;
        }
        
        public static Axis get(int value)
        {
            for (Axis hat : Axis.values()) if (hat.id == value) return hat;
            return Axis.NONE;
        }
    }
    
    public enum Button
    {
        NONE(-1),
        
        A(GLFW_GAMEPAD_BUTTON_A),
        B(GLFW_GAMEPAD_BUTTON_B),
        X(GLFW_GAMEPAD_BUTTON_X),
        Y(GLFW_GAMEPAD_BUTTON_Y),
        
        LEFT_BUMPER(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER),
        RIGHT_BUMPER(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER),
        
        BACK(GLFW_GAMEPAD_BUTTON_BACK),
        START(GLFW_GAMEPAD_BUTTON_START),
        GUIDE(GLFW_GAMEPAD_BUTTON_GUIDE),
        
        LEFT_THUMB(GLFW_GAMEPAD_BUTTON_LEFT_THUMB),
        RIGHT_THUMB(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB),
        
        DPAD_UP(GLFW_GAMEPAD_BUTTON_DPAD_UP),
        DPAD_RIGHT(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT),
        DPAD_DOWN(GLFW_GAMEPAD_BUTTON_DPAD_DOWN),
        DPAD_LEFT(GLFW_GAMEPAD_BUTTON_DPAD_LEFT),
        
        CROSS(GLFW_GAMEPAD_BUTTON_CROSS),
        CIRCLE(GLFW_GAMEPAD_BUTTON_CIRCLE),
        SQUARE(GLFW_GAMEPAD_BUTTON_SQUARE),
        TRIANGLE(GLFW_GAMEPAD_BUTTON_TRIANGLE),
        ;
        
        private final int id;
        
        Button(int id)
        {
            this.id = id;
        }
        
        public int id()
        {
            return this.id;
        }
        
        public static Button get(int value)
        {
            for (Button button : Button.values()) if (button.id == value) return button;
            return Button.NONE;
        }
    }
}
