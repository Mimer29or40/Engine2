package engine.input;

import engine.event.Events;
import engine.event.*;
import org.joml.Vector2i;
import org.joml.Vector2ic;

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
    
    private final Vector2i pos       = new Vector2i();
    private final Vector2i newPos    = new Vector2i();
    private final Vector2i rel       = new Vector2i();
    private final Vector2i scroll    = new Vector2i();
    private final Vector2i newScroll = new Vector2i();
    
    private       Button   drag;
    private final Vector2i dragPos = new Vector2i();
    
    public boolean entered()
    {
        return this.entered;
    }
    
    public Vector2ic pos()
    {
        return this.pos;
    }
    
    public int x()
    {
        return this.pos.x;
    }
    
    public int y()
    {
        return this.pos.y;
    }
    
    public Vector2ic rel()
    {
        return this.rel;
    }
    
    public int relX()
    {
        return this.rel.x;
    }
    
    public int relY()
    {
        return this.rel.y;
    }
    
    public Vector2ic scroll()
    {
        return this.scroll;
    }
    
    public int scrollX()
    {
        return this.scroll.x;
    }
    
    public int scrollY()
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
        this.newPos.set(Math.max(0, Math.min((int) x, screenWidth() - 1)), Math.max(0, Math.min((int) y, screenHeight() - 1)));
    }
    
    public void scrollCallback(double x, double y)
    {
        this.newScroll.add((int) x, (int) y);
    }
    
    public void captureCallback(boolean captured)
    {
        if (captured) this.newPos.set(this.pos.set(screenWidth() / 2, screenHeight() / 2));
    }
    
    public class Button extends Device.Input
    {
        private final Vector2i click  = new Vector2i();
        private final Vector2i dClick = new Vector2i();
        
        private Button(String name, int reference)
        {
            super(Mouse.this, name, reference);
        }
    }
}
