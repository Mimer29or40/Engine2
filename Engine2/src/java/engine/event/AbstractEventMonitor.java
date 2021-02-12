package engine.event;

import engine.Monitor;

abstract class AbstractEventMonitor extends AbstractEvent implements EventMonitor
{
    private final Monitor monitor;
    
    AbstractEventMonitor(Monitor monitor)
    {
        super();
    
        this.monitor = monitor;
    }
    
    @Override
    public Monitor monitor()
    {
        return this.monitor;
    }
}
