package engine.event;

import engine.Monitor;

public interface EventMonitor extends Event
{
    @Property(printName = false)
    Monitor monitor();
}
