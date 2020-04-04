package engine;

import engine.util.Profiler;

@SuppressWarnings("unused")
public abstract class Extension
{
    protected boolean enabled = true;
    
    /**
     * @return If this extension is enabled.
     */
    public boolean enabled()
    {
        return this.enabled;
    }
    
    /**
     * Sets the extension enabled or not.
     */
    public void enabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    /**
     * Toggles if the extension is enabled or not.
     */
    public void toggleEnabled()
    {
        this.enabled = !this.enabled;
    }
    
    /**
     * This is called once before the {@link Engine#setup} method is called.
     */
    public abstract void beforeSetup();
    
    /**
     * This is called once after the {@link Engine#setup} method is called only if {@link Engine#size} is called.
     */
    public abstract void afterSetup();
    
    /**
     * This is called once per frame before the {@link Engine#draw} method is called.
     *
     * @param profiler    The profiler to track the time taken by the extension.
     * @param elapsedTime The time in seconds since the last frame.
     */
    public abstract void beforeDraw(Profiler profiler, double elapsedTime);
    
    /**
     * This is called once per frame after the {@link Engine#draw} method is called.
     *
     * @param profiler    The profiler to track the time taken by the extension.
     * @param elapsedTime The time in seconds since the last frame.
     */
    public abstract void afterDraw(Profiler profiler, double elapsedTime);
    
    /**
     * This is called once before the {@link Engine#destroy} method is called.
     */
    public abstract void beforeDestroy();
    
    /**
     * This is called once after the {@link Engine#destroy} method is called.
     */
    public abstract void afterDestroy();
}
