package engine;

import org.jetbrains.annotations.Nullable;
import rutils.Logger;
import rutils.group.Pair;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;

public class Joystick extends InputDevice
{
    private static final Logger LOGGER = new Logger();
    
    protected final int jid;
    
    protected final boolean gamepad;
    
    protected final String name;
    protected final String guid;
    
    protected final Queue<Pair<Integer, Float>>   axisStateChanges   = new ConcurrentLinkedQueue<>();
    protected final Queue<Pair<Integer, Integer>> buttonStateChanges = new ConcurrentLinkedQueue<>();
    protected final Queue<Pair<Integer, Integer>> hatStateChanges    = new ConcurrentLinkedQueue<>();
    
    protected final Map<Integer, AxisInput> axisMap;
    protected final Map<Integer, Input>     buttonMap;
    protected final Map<Integer, Input>     hatMap;
    
    protected Joystick(int jid, boolean gamepad)
    {
        this.jid = jid;
        
        this.gamepad = gamepad;
        
        this.name = glfwGetJoystickName(this.jid);
        this.guid = glfwGetJoystickGUID(this.jid);
        
        this.axisMap = new LinkedHashMap<>();
        FloatBuffer axes = Objects.requireNonNull(glfwGetJoystickAxes(this.jid), "Joystick is not connected.");
        for (int i = 0, n = axes.remaining(); i < n; i++) this.axisMap.put(i, new AxisInput(axes.get(i)));
        
        this.buttonMap = new LinkedHashMap<>();
        ByteBuffer buttons = Objects.requireNonNull(glfwGetJoystickButtons(this.jid), "Joystick is not connected.");
        for (int i = 0, n = buttons.remaining(); i < n; i++) this.buttonMap.put(i, new Input());
        
        this.hatMap = new LinkedHashMap<>();
        ByteBuffer hats = Objects.requireNonNull(glfwGetJoystickHats(this.jid), "Joystick is not connected.");
        for (int i = 0, n = hats.remaining(); i < n; i++) this.hatMap.put(i, new Input());
        
        Joystick.LOGGER.finer("Created", this);
    }
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "{" + "name='" + this.name + '\'' + ", jid=" + this.jid + '}';
    }
    
    /**
     * Returns whether the specified joystick is both present and has a gamepad mapping.
     *
     * @return {@code true} if a joystick is both present and has a gamepad mapping or {@code false} otherwise
     */
    public boolean isGamepad()
    {
        return this.gamepad;
    }
    
    /**
     * Returns the name, encoded as UTF-8, of the specified joystick.
     *
     * @return the UTF-8 encoded name of the joystick, or {@code NULL} if the joystick is not present
     */
    public @Nullable String name()
    {
        return this.name;
    }
    
    /**
     * Returns the SDL compatible GUID, as a UTF-8 encoded hexadecimal string,
     * of the specified joystick.
     * <p>
     * The GUID is what connects a joystick to a gamepad mapping. A connected
     * joystick will always have a GUID even if there is no gamepad mapping
     * assigned to it.
     * <p>
     * The GUID uses the format introduced in SDL 2.0.5. This GUID tries to
     * uniquely identify the make and model of a joystick but does not identify
     * a specific unit, e.g. all wired Xbox 360 controllers will have the same
     * GUID on that platform. The GUID for a unit may vary between platforms
     * depending on what hardware information the platform specific APIs
     * provide.
     *
     * @return the UTF-8 encoded GUID of the joystick, or {@code NULL} if the joystick is not present or an error occurred
     */
    public @Nullable String guid()
    {
        return this.guid;
    }
    
    /**
     * This method is called by the window it is attached to. This is where
     * events should be posted to when something has changed.
     *
     * @param time   The system time in nanoseconds.
     * @param deltaT The time in nanoseconds since the last time this method was called.
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    protected void postEvents(long time, long deltaT)
    {
        Pair<Integer, Float> axisStateChange;
        while ((axisStateChange = this.axisStateChanges.poll()) != null)
        {
            int axis = axisStateChange.getA();
            
            AxisInput axisObj = this.axisMap.get(axis);
            
            axisObj._value = axisStateChange.getB();
            if (Double.compare(axisObj.value, axisObj._value) != 0)
            {
                double delta = axisObj._value - axisObj.value;
                axisObj.value = axisObj._value;
                postAxisEvent(axis, axisObj.value, delta);
            }
        }
        
        Pair<Integer, Integer> buttonStateChange;
        while ((buttonStateChange = this.buttonStateChanges.poll()) != null)
        {
            Input buttonObj = this.buttonMap.get(buttonStateChange.getA());
            
            buttonObj._state = buttonStateChange.getB();
        }
        
        for (int button : this.buttonMap.keySet())
        {
            Input input = this.buttonMap.get(button);
            
            input.state  = input._state;
            input._state = -1;
            switch (input.state)
            {
                case GLFW_PRESS -> {
                    input.held     = true;
                    input.holdTime = time + InputDevice.holdFrequency;
                    postButtonDownEvent(button);
                }
                case GLFW_RELEASE -> {
                    input.held     = false;
                    input.holdTime = Long.MAX_VALUE;
                    postButtonUpEvent(button);
                    
                    if (time - input.pressTime < InputDevice.doublePressedDelay)
                    {
                        input.pressTime = 0;
                        postButtonPressedEvent(button, true);
                    }
                    else
                    {
                        input.pressTime = time;
                        postButtonPressedEvent(button, false);
                    }
                }
                case GLFW_REPEAT -> postButtonRepeatedEvent(button);
            }
            if (input.held && time - input.holdTime >= InputDevice.holdFrequency)
            {
                input.holdTime += InputDevice.holdFrequency;
                postButtonHeldEvent(button);
            }
        }
        
        Pair<Integer, Integer> hatStateChange;
        while ((hatStateChange = this.hatStateChanges.poll()) != null)
        {
            int hat = hatStateChange.getA();
            
            Input hatObj = this.hatMap.get(hat);
            
            hatObj._state = hatStateChange.getB();
            if (hatObj.state != hatObj._state)
            {
                hatObj.state = hatObj._state;
                postHatEvent(hat, hatObj.state);
            }
        }
    }
    
    protected void postAxisEvent(int axis, double value, double delta)
    {
        // GLFW.EVENT_BUS.post(EventJoystickAxis.create(this, axis, value, delta)); // TODO
    }
    
    protected void postButtonDownEvent(int button)
    {
        // GLFW.EVENT_BUS.post(EventJoystickButtonDown.create(this, button)); // TODO
    }
    
    protected void postButtonUpEvent(int button)
    {
        // GLFW.EVENT_BUS.post(EventJoystickButtonUp.create(this, button)); // TODO
    }
    
    protected void postButtonPressedEvent(int button, boolean doublePressed)
    {
        // GLFW.EVENT_BUS.post(EventJoystickButtonPressed.create(this, button, doublePressed)); // TODO
    }
    
    protected void postButtonHeldEvent(int button)
    {
        // GLFW.EVENT_BUS.post(EventJoystickButtonHeld.create(this, button)); // TODO
    }
    
    protected void postButtonRepeatedEvent(int button)
    {
        // GLFW.EVENT_BUS.post(EventJoystickButtonRepeated.create(this, button)); // TODO
    }
    
    protected void postHatEvent(int hat, int state)
    {
        // GLFW.EVENT_BUS.post(EventJoystickHat.create(this, hat, Hat.get(state))); // TODO
    }
    
    protected static final class AxisInput
    {
        protected double _value, value;
        
        protected AxisInput(double initial)
        {
            this.value = this._value = initial;
        }
    }
    
    public enum Hat
    {
        CENTERED(GLFW_HAT_CENTERED),
        
        UP(GLFW_HAT_UP),
        RIGHT(GLFW_HAT_RIGHT),
        DOWN(GLFW_HAT_DOWN),
        LEFT(GLFW_HAT_LEFT),
        
        RIGHT_UP(GLFW_HAT_RIGHT_UP),
        RIGHT_DOWN(GLFW_HAT_RIGHT_DOWN),
        LEFT_UP(GLFW_HAT_LEFT_UP),
        LEFT_DOWN(GLFW_HAT_LEFT_DOWN),
        
        UP_DOWN(GLFW_HAT_UP | GLFW_HAT_DOWN),
        RIGHT_LEFT(GLFW_HAT_RIGHT | GLFW_HAT_LEFT),
        
        RIGHT_UP_LEFT(GLFW_HAT_RIGHT | GLFW_HAT_UP | GLFW_HAT_LEFT),
        RIGHT_DOWN_LEFT(GLFW_HAT_RIGHT | GLFW_HAT_DOWN | GLFW_HAT_LEFT),
        UP_RIGHT_DOWN(GLFW_HAT_UP | GLFW_HAT_RIGHT | GLFW_HAT_DOWN),
        UP_LEFT_DOWN(GLFW_HAT_UP | GLFW_HAT_LEFT | GLFW_HAT_DOWN),
        
        ALL(GLFW_HAT_UP | GLFW_HAT_RIGHT | GLFW_HAT_DOWN | GLFW_HAT_LEFT),
        
        ;
        
        private final int ref;
        
        Hat(int ref)
        {
            this.ref = ref;
        }
        
        public static Hat get(int value)
        {
            for (Hat hat : Hat.values()) if ((value & hat.ref) == value) return hat;
            return Hat.CENTERED;
        }
    }
}
