package engine.input;

import engine.event.*;
import org.joml.Vector2d;
import org.joml.Vector2dc;

import static engine.Engine.screenHeight;
import static engine.Engine.screenWidth;
import static org.lwjgl.glfw.GLFW.*;

public class Mouse extends Device<Mouse.Button>
{
    public final Button NONE   = new Button("NONE", -1);
    public final Button LEFT   = new Button("LEFT", GLFW_MOUSE_BUTTON_LEFT);
    public final Button RIGHT  = new Button("RIGHT", GLFW_MOUSE_BUTTON_RIGHT);
    public final Button MIDDLE = new Button("MIDDLE", GLFW_MOUSE_BUTTON_MIDDLE);
    public final Button FOUR   = new Button("FOUR", GLFW_MOUSE_BUTTON_4);
    public final Button FIVE   = new Button("FIVE", GLFW_MOUSE_BUTTON_5);
    public final Button SIX    = new Button("SIX", GLFW_MOUSE_BUTTON_6);
    public final Button SEVEN  = new Button("SEVEN", GLFW_MOUSE_BUTTON_7);
    public final Button EIGHT  = new Button("EIGHT", GLFW_MOUSE_BUTTON_8);
    
    private boolean entered, newEntered;
    
    private final Vector2d pos       = new Vector2d();
    private final Vector2d newPos    = new Vector2d();
    private final Vector2d rel       = new Vector2d();
    private final Vector2d scroll    = new Vector2d();
    private final Vector2d newScroll = new Vector2d();
    
    private       Button   drag;
    private final Vector2d dragPos = new Vector2d();
    
    public boolean entered()
    {
        return this.entered;
    }
    
    public Vector2dc pos()
    {
        return this.pos;
    }
    
    public double x()
    {
        return this.pos.x;
    }
    
    public double y()
    {
        return this.pos.y;
    }
    
    public Vector2dc rel()
    {
        return this.rel;
    }
    
    public double relX()
    {
        return this.rel.x;
    }
    
    public double relY()
    {
        return this.rel.y;
    }
    
    public Vector2dc scroll()
    {
        return this.scroll;
    }
    
    public double scrollX()
    {
        return this.scroll.x;
    }
    
    public double scrollY()
    {
        return this.scroll.y;
    }
    
    @Override
    protected Button getDefault()
    {
        return this.NONE;
    }
    
    @Override
    public void handleEvents(long time, long delta)
    {
        if (this.entered != this.newEntered)
        {
            this.entered = this.newEntered;
            Events.post(EventMouseEntered.class, this.entered);
        }
    
        if (!this.pos.equals(this.newPos))
        {
            this.newPos.sub(this.pos, this.rel);
            this.pos.set(this.newPos);
            Events.post(EventMouseMoved.class, this.pos, this.rel);
        }
    
        if (!this.scroll.equals(this.newScroll))
        {
            this.scroll.set(this.newScroll);
            this.newScroll.set(0);
            Events.post(EventMouseScrolled.class, this.scroll);
        }
    
        super.handleEvents(time, delta);
    }
    
    @Override
    protected void postEvents(Button input, long time, long delta)
    {
        if (input.down)
        {
            Events.post(EventMouseButtonDown.class, input, this.pos);
    
            input.click.set(this.pos);
            if (this.drag == null)
            {
                this.drag = input;
                this.dragPos.set(this.pos);
            }
        }
        if (input.up)
        {
            Events.post(EventMouseButtonUp.class, input, this.pos);
    
            boolean inClickRange  = Math.abs(this.pos.x - input.click.x) < 2 && Math.abs(this.pos.y - input.click.y) < 2;
            boolean inDClickRange = Math.abs(this.pos.x - input.dClick.x) < 2 && Math.abs(this.pos.y - input.dClick.y) < 2;
    
            if (inDClickRange && time - input.pressTime < 500_000_000)
            {
                Events.post(EventMouseButtonClicked.class, input, this.pos, true);
            }
            else if (inClickRange)
            {
                Events.post(EventMouseButtonClicked.class, input, this.pos, false);
                input.dClick.set(this.pos);
                input.pressTime = time;
            }
            if (this.drag == input) this.drag = null;
        }
        if (input.held)
        {
            Events.post(EventMouseButtonHeld.class, input, this.pos);
    
            if (this.drag == input && (this.rel.x != 0 || this.rel.y != 0))
            {
                Events.post(EventMouseButtonDragged.class, input, this.dragPos, this.pos, this.rel);
            }
        }
        if (input.repeat) Events.post(EventMouseButtonRepeat.class, input, this.pos);
    }
    
    public void enteredCallback(boolean entered)
    {
        this.newEntered = entered;
    }
    
    public void positionCallback(double x, double y)
    {
        this.newPos.set(Math.max(0, Math.min(x, screenWidth() - 1)), Math.max(0, Math.min(y, screenHeight() - 1)));
    }
    
    public void scrollCallback(double x, double y)
    {
        this.newScroll.add(x, y);
    }
    
    public void captureCallback(boolean captured)
    {
        if (captured) this.newPos.set(this.pos.set(screenWidth() / 2.0, screenHeight() / 2.0));
    }
    
    public class Button extends Device.Input
    {
        private final Vector2d click  = new Vector2d();
        private final Vector2d dClick = new Vector2d();
        
        private Button(String name, int reference)
        {
            super(Mouse.this, name, reference);
        }
    }
}
