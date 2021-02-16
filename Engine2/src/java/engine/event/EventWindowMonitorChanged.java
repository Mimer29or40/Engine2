package engine.event;

import engine.Monitor;

public interface EventWindowMonitorChanged extends EventWindow
{
    @Property
    Monitor from();
    
    @Property
    Monitor to();
    
    final class _EventWindowMonitorChanged extends AbstractEventWindow implements EventWindowMonitorChanged
    {
        private final Monitor from;
        private final Monitor to;
    
        private _EventWindowMonitorChanged(Monitor from, Monitor to)
        {
            super();
    
            this.from = from;
            this.to   = to;
        }
    
        @Override
        public Monitor from()
        {
            return this.from;
        }
    
        @Override
        public Monitor to()
        {
            return this.to;
        }
    }
    
    static EventWindowMonitorChanged create(Monitor from, Monitor to)
    {
        return new _EventWindowMonitorChanged(from, to);
    }
}
