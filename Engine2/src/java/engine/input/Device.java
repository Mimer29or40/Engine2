package engine.input;

import engine.util.Logger;

import java.util.Collection;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("unused")
public abstract class Device<I extends Device.Input>
{
    private static final Logger LOGGER = new Logger();
    
    protected static long holdDelay   = 500_000_000;
    protected static long repeatDelay = 100_000_000;
    protected static long doubleDelay = 100_000_000;
    
    /**
     * @return The delay in seconds before an Input is "held".
     */
    public static double holdDelay()
    {
        return Device.holdDelay / 1_000_000_000D;
    }
    
    /**
     * Sets the delay in seconds before an Input is "held".
     *
     * @param holdDelay The new delay in seconds.
     */
    public static void holdDelay(double holdDelay)
    {
        Device.LOGGER.finest("Setting Device Hold Delay:", holdDelay);
        
        Device.holdDelay = (long) (holdDelay * 1_000_000_000L);
    }
    
    /**
     * @return The delay in seconds before an Input is "repeated".
     */
    public static double repeatDelay()
    {
        return Device.repeatDelay / 1_000_000_000D;
    }
    
    /**
     * Sets the delay in seconds before an Input is "repeated".
     *
     * @param repeatDelay The new delay in seconds.
     */
    public static void repeatDelay(double repeatDelay)
    {
        Device.LOGGER.finest("Setting Device Repeat Delay:", holdDelay);
        
        Device.repeatDelay = (long) (repeatDelay * 1_000_000_000L);
    }
    
    /**
     * @return The delay in seconds before an Input is pressed/clicked twice to be a double pressed/clicked.
     */
    public static double doubleDelay()
    {
        return Device.doubleDelay / 1_000_000_000D;
    }
    
    /**
     * Sets the delay in seconds before an Input is pressed/clicked twice to be a double pressed/clicked.
     *
     * @param doubleDelay The new delay in seconds.
     */
    public static void doubleDelay(double doubleDelay)
    {
        Device.LOGGER.finest("Setting Device Double Delay:", holdDelay);
        
        Device.doubleDelay = (long) (doubleDelay * 1_000_000_000L);
    }
    
    private final HashMap<Integer, I> inputs = new HashMap<>();
    
    /**
     * @return Gets all inputs for this Device
     */
    public Collection<I> inputs()
    {
        return this.inputs.values();
    }
    
    /**
     * @return Gets the Input that represents the GLFW constant.
     */
    public I get(int reference)
    {
        return this.inputs.getOrDefault(reference, getDefault());
    }
    
    /**
     * @return Gets the default Input for this Device.
     */
    protected abstract I getDefault();
    
    /**
     * This is called by the Engine to generate the events and state changes for the Device.
     *
     * @param time  The time in nano seconds that it happened.
     * @param delta The time in nano seconds since the last frame.
     */
    public void handleEvents(long time, long delta)
    {
        Device.LOGGER.finest("Handling Device Events:", getClass().getSimpleName());
        
        for (I input : inputs())
        {
            input.down   = false;
            input.up     = false;
            input.repeat = false;
            input.mods   = 0;
            
            if (input.newState != input.state)
            {
                if (input.newState == GLFW_PRESS)
                {
                    input.down     = true;
                    input.held     = true;
                    input.downTime = time;
                }
                else if (input.newState == GLFW_RELEASE)
                {
                    input.up       = true;
                    input.held     = false;
                    input.downTime = Long.MAX_VALUE;
                }
                input.state = input.newState;
                input.mods = input.newMods;
            }
            if (input.state == GLFW_REPEAT || input.held && time - input.downTime > Device.holdDelay)
            {
                input.downTime += Device.repeatDelay;
                input.repeat = true;
                input.mods = input.newMods;
            }
            
            postEvents(input, time, delta);
        }
    }
    
    /**
     * This is called by the Device to post any events that it may have generated this frame.
     *
     * @param input The Input
     * @param time  The time in nano seconds that it happened.
     * @param delta The time in nano seconds since the last frame.
     */
    protected abstract void postEvents(I input, long time, long delta);
    
    /**
     * This is the callback used by the window whenever an input is pressed, released, or repeated
     *
     * @param reference The Input
     * @param state     The action that took place
     * @param mods      The modifier info
     */
    public void stateCallback(int reference, int state, int mods)
    {
        Device.LOGGER.finest("Device(%s) Input State Callback: %s %s", getClass().getSimpleName(), reference, state);
        
        I input = get(reference);
        input.newState = state;
        input.newMods  = mods;
    }
    
    /**
     * This class represents the input method on the Device. This is the keys on the keyboard or the buttons on the mouse.
     */
    public static class Input
    {
        private final String name;
        
        protected boolean down, up, held, repeat;
        protected int newState, state, newMods, mods;
        protected long downTime, pressTime;
        
        @SuppressWarnings("unchecked")
        protected <I extends Device.Input> Input(Device<I> device, String name, int reference)
        {
            this.name = name;
            device.inputs.put(reference, (I) this);
        }
        
        @Override
        public String toString()
        {
            return getClass().getSimpleName() + "." + this.name;
        }
    
        /**
         * @return If the Input was pressed with optional modifiers. This will only be true for one frame.
         */
        public boolean down(Modifiers.Modifier... modifiers)
        {
            return this.down && checkModifiers(modifiers);
        }
    
        /**
         * @return If the Input was released with optional modifiers. This will only be true for one frame.
         */
        public boolean up(Modifiers.Modifier... modifiers)
        {
            return this.up && checkModifiers(modifiers);
        }
    
        /**
         * @return If the Input is being held down with optional modifiers.
         */
        public boolean held(Modifiers.Modifier... modifiers)
        {
            return this.held && checkModifiers(modifiers);
        }
    
        /**
         * @return If the Input is being repeated with optional modifiers. This will be true for one frame at a time.
         */
        public boolean repeat(Modifiers.Modifier... modifiers)
        {
            return this.repeat && checkModifiers(modifiers);
        }
        
        private boolean checkModifiers(Modifiers.Modifier[] modifiers)
        {
            if (modifiers.length == 0) return true;
            int mods = 0;
            for (Modifiers.Modifier modifier : modifiers) mods |= modifier.value();
            return (mods == 0 && this.mods == 0) || (this.mods & mods) != 0;
        }
    }
}
