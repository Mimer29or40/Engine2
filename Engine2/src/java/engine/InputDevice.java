package engine;

import rutils.Logger;

public abstract class InputDevice
{
    private static final Logger LOGGER = new Logger();
    
    protected static long holdFrequency      = 1_000_000L;
    protected static long doublePressedDelay = 200_000_000L;
    
    /**
     * @return The frequency, in seconds, that a "Held" event will be generated while an ButtonInput is down.
     */
    public static double holdFrequency()
    {
        return InputDevice.holdFrequency / 1_000_000_000D;
    }
    
    /**
     * Sets the frequency, in seconds, that a "Held" event will be generated while an ButtonInput is down.
     *
     * @param holdFrequency The frequency, in seconds, that a "Held" event will be generated while an ButtonInput is down.
     */
    public static void holdFrequency(double holdFrequency)
    {
        InputDevice.LOGGER.finest("Setting InputDevice Hold Frequency:", holdFrequency);
        
        InputDevice.holdFrequency = (long) (holdFrequency * 1_000_000_000L);
    }
    
    /**
     * @return The delay, in seconds, before an ButtonInput is pressed twice to be a double pressed.
     */
    public static double doublePressedDelay()
    {
        return InputDevice.doublePressedDelay / 1_000_000_000D;
    }
    
    /**
     * Sets the delay, in seconds, before an ButtonInput is pressed twice to be a double pressed.
     *
     * @param doublePressedDelay The delay, in seconds, before an ButtonInput is pressed twice to be a double pressed.
     */
    public static void doublePressedDelay(double doublePressedDelay)
    {
        InputDevice.LOGGER.finest("Setting InputDevice Double Delay:", doublePressedDelay);
        
        InputDevice.doublePressedDelay = (long) (doublePressedDelay * 1_000_000_000L);
    }
    
    /**
     * This method is called by the window it is attached to. This is where
     * events should be posted to when something has changed.
     *
     * @param time   The system time in nanoseconds.
     * @param deltaT The time in nanoseconds since the last time this method was called.
     */
    protected abstract void postEvents(long time, long deltaT);
    
    protected static class Input
    {
        protected int state = -1, _state = -1;
        
        protected boolean held;
        
        protected long holdTime = Long.MAX_VALUE, pressTime;
    }
}

