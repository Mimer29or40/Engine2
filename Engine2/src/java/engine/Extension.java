package engine;

import engine.util.Profiler;

public abstract class Extension
{
    protected boolean enabled = true;
    
    public void enable()
    {
        this.enabled = true;
    }
    
    public void disable()
    {
        this.enabled = false;
    }
    
    public boolean isEnabled()
    {
        return this.enabled;
    }
    
    public abstract void beforeSetup();
    
    public abstract void afterSetup();
    
    public abstract void beforeDraw(Profiler profiler, double elapsedTime);
    
    public abstract void afterDraw(Profiler profiler, double elapsedTime);
    
    public abstract void beforeDestroy();
    
    public abstract void afterDestroy();
}
