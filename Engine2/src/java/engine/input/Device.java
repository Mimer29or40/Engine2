package engine.input;

import java.util.Collection;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public abstract class Device<I extends Device.Input>
{
    protected static long holdDelay   = 500_000_000;
    protected static long repeatDelay = 100_000_000;
    protected static long doubleDelay = 100_000_000;
    
    public static double holdDelay()
    {
        return Device.holdDelay / 1_000_000_000D;
    }
    
    public static void holdDelay(double holdDelay)
    {
        Device.holdDelay = (long) (holdDelay * 1_000_000_000L);
    }
    
    public static double repeatDelay()
    {
        return Device.repeatDelay / 1_000_000_000D;
    }
    
    public static void repeatDelay(double repeatDelay)
    {
        Device.repeatDelay = (long) (repeatDelay * 1_000_000_000L);
    }
    
    private final HashMap<Integer, I> inputs = new HashMap<>();
    
    public Collection<I> inputs()
    {
        return this.inputs.values();
    }
    
    public I get(int reference)
    {
        return this.inputs.getOrDefault(reference, getDefault());
    }
    
    protected abstract I getDefault();
    
    public void handleEvents(long time, long delta)
    {
        for (I input : inputs())
        {
            input.down   = false;
            input.up     = false;
            input.repeat = false;
            
            if (input.state != input.prevState)
            {
                if (input.state == GLFW_PRESS)
                {
                    input.down     = true;
                    input.held     = true;
                    input.downTime = time;
                }
                else if (input.state == GLFW_RELEASE)
                {
                    input.up       = true;
                    input.held     = false;
                    input.downTime = Long.MAX_VALUE;
                }
            }
            if (input.state == GLFW_REPEAT || input.held && time - input.downTime > Device.holdDelay)
            {
                input.downTime += Device.repeatDelay;
                input.repeat = true;
            }
            input.prevState = input.state;
    
            postEvents(input, time, delta);
        }
    }
    
    protected abstract void postEvents(I input, long time, long delta);
    
    public void stateCallback(int reference, int state)
    {
        get(reference).state = state;
    }
    
    public static class Input
    {
        private final String name;
        
        protected boolean down, up, held, repeat;
        protected int state, prevState;
        protected long downTime, pressTime;
        
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
        
        public String getName()
        {
            return this.name;
        }
        
        public boolean down()
        {
            return this.down;
        }
        
        public boolean up()
        {
            return this.up;
        }
        
        public boolean held()
        {
            return this.held;
        }
        
        public boolean repeat()
        {
            return this.repeat;
        }
    }
}
