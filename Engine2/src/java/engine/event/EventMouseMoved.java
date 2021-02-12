package engine.event;

import org.joml.Vector2d;
import org.joml.Vector2dc;

public interface EventMouseMoved extends EventMouse
{
    @Property
    Vector2dc pos();
    
    default double x()
    {
        return pos().x();
    }
    
    default double y()
    {
        return pos().y();
    }
    
    @Property
    Vector2dc rel();
    
    default double dx()
    {
        return rel().x();
    }
    
    default double dy()
    {
        return rel().y();
    }
    
    final class _EventMouseMoved extends AbstractEventInputDevice implements EventMouseMoved
    {
        private final Vector2d pos;
        private final Vector2d rel;
        
        private _EventMouseMoved(Vector2dc pos, Vector2dc rel)
        {
            super();
            
            this.pos = new Vector2d(pos);
            this.rel = new Vector2d(rel);
        }
        
        @Override
        public Vector2dc pos()
        {
            return this.pos;
        }
        
        @Override
        public Vector2dc rel()
        {
            return this.rel;
        }
    }
    
    static EventMouseMoved create(Vector2dc pos, Vector2dc rel)
    {
        return new _EventMouseMoved(pos, rel);
    }
}
