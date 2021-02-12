package engine.event;

import engine.Monitor;

public interface EventMonitorConnected extends EventMonitor
{
    final class _EventMonitorConnected extends AbstractEventMonitor implements EventMonitorConnected
    {
        private _EventMonitorConnected(Monitor monitor)
        {
            super(monitor);
        }
    }
    
    static EventMonitorConnected create(Monitor monitor)
    {
        return new _EventMonitorConnected(monitor);
    }
}
