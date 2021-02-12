package engine.event;

import engine.Mouse;
import org.joml.Vector2d;
import org.joml.Vector2dc;

public interface EventMouseButtonDragged extends EventMouseButton
{
    @Property
    Vector2dc rel();
    
    default double dx()
    {
        return this.rel().x();
    }
    
    default double dy()
    {
        return this.rel().y();
    }
    
    @Property
    Vector2dc dragStart();
    
    default double dragStartX()
    {
        return this.dragStart().x();
    }
    
    default double dragStartY()
    {
        return this.dragStart().y();
    }
    
    final class _EventMouseButtonDragged extends AbstractEventMouseButton implements EventMouseButtonDragged
    {
        private final Vector2d rel;
        private final Vector2d dragStart;
        
        private _EventMouseButtonDragged(Mouse.Button button, Vector2dc pos, Vector2dc rel, Vector2dc dragStart)
        {
            super(button, pos);
            
            this.rel       = new Vector2d(rel);
            this.dragStart = new Vector2d(dragStart);
        }
        
        @Override
        public Vector2dc rel()
        {
            return this.rel;
        }
        
        @Override
        public Vector2dc dragStart()
        {
            return this.dragStart;
        }
    }
    
    static EventMouseButtonDragged create(Mouse.Button button, Vector2dc pos, Vector2dc rel, Vector2dc dragStart)
    {
        return new _EventMouseButtonDragged(button, pos, rel, dragStart);
    }
}
