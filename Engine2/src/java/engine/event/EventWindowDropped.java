package engine.event;

import java.nio.file.Path;

public interface EventWindowDropped extends EventWindow
{
    @Property
    Path[] paths();
    
    final class _EventWindowDropped extends AbstractEventWindow implements EventWindowDropped
    {
        private final Path[] paths;
    
        private _EventWindowDropped(Path[] paths)
        {
            super();
    
            this.paths = paths;
        }
    
        @Override
        public Path[] paths()
        {
            return this.paths;
        }
    }
    
    static EventWindowDropped create(Path[] paths)
    {
        return new _EventWindowDropped(paths);
    }
}
