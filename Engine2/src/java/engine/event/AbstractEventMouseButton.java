package engine.event;

import engine.Mouse;
import org.joml.Vector2d;
import org.joml.Vector2dc;

abstract class AbstractEventMouseButton extends AbstractEventInputDevice implements EventMouseButton
{
    private final Mouse.Button button;
    private final Vector2d     pos;
    
    AbstractEventMouseButton(Mouse.Button button, Vector2dc pos)
    {
        super();
        
        this.button = button;
        this.pos    = new Vector2d(pos);
    }
    
    @Override
    public Mouse.Button button()
    {
        return this.button;
    }
    
    @Override
    public Vector2dc pos()
    {
        return this.pos;
    }
}
