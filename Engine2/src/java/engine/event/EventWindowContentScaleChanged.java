package engine.event;

import org.joml.Vector2d;
import org.joml.Vector2dc;

public interface EventWindowContentScaleChanged extends EventWindow
{
    @Property
    Vector2dc contentScale();
    
    default double contentScaleX()
    {
        return contentScale().x();
    }
    
    default double contentScaleY()
    {
        return contentScale().y();
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
    
    final class _EventWindowContentScaleChanged extends AbstractEventWindow implements EventWindowContentScaleChanged
    {
        private final Vector2d scale;
        private final Vector2d rel;
    
        private _EventWindowContentScaleChanged(Vector2d scale, Vector2d rel)
        {
            super();
    
            this.scale = new Vector2d(scale);
            this.rel   = new Vector2d(rel);
        }
    
        @Override
        public Vector2dc contentScale()
        {
            return this.scale;
        }
    
        @Override
        public Vector2dc rel()
        {
            return this.rel;
        }
    }
    
    static EventWindowContentScaleChanged create(Vector2d scale, Vector2d rel)
    {
        return new _EventWindowContentScaleChanged(scale, rel);
    }
}
