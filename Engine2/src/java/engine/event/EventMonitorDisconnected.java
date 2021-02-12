package engine.event;

import engine.Monitor;

public interface EventMonitorDisconnected extends EventMonitor
{
    final class _EventMonitorDisconnected extends AbstractEventMonitor implements EventMonitorDisconnected
    {
        private _EventMonitorDisconnected(Monitor monitor)
        {
            super(monitor);
        }
    }
    
    static EventMonitorDisconnected create(Monitor monitor)
    {
        return new _EventMonitorDisconnected(monitor);
    }
}
