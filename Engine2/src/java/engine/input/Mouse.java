package engine.input;

import engine.event.Event;
import engine.event.Events;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import rutils.Logger;
import rutils.joml.JOMLUnit;

import static engine.Engine.*;
import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("unused")
public class Mouse extends Device<Mouse.Button>
{
    private static final Logger LOGGER = new Logger();
    
    public final Button NONE   = new Button("NONE", -1);
    public final Button LEFT   = new Button("LEFT", GLFW_MOUSE_BUTTON_LEFT);
    public final Button RIGHT  = new Button("RIGHT", GLFW_MOUSE_BUTTON_RIGHT);
    public final Button MIDDLE = new Button("MIDDLE", GLFW_MOUSE_BUTTON_MIDDLE);
    public final Button FOUR   = new Button("FOUR", GLFW_MOUSE_BUTTON_4);
    public final Button FIVE   = new Button("FIVE", GLFW_MOUSE_BUTTON_5);
    public final Button SIX    = new Button("SIX", GLFW_MOUSE_BUTTON_6);
    public final Button SEVEN  = new Button("SEVEN", GLFW_MOUSE_BUTTON_7);
    public final Button EIGHT  = new Button("EIGHT", GLFW_MOUSE_BUTTON_8);
    
    private boolean captured, newCaptured;
    private boolean entered, newEntered;
    
    private final Vector2d pos       = new Vector2d(0);
    private final Vector2d newPos    = new Vector2d(0);
    private final Vector2d rel       = new Vector2d(0);
    private final Vector2d scroll    = new Vector2d(0);
    private final Vector2d newScroll = new Vector2d(0);
    
    private       Button   drag;
    private final Vector2d dragPos = new Vector2d();
    
    /**
     * @return If the mouse captured by the window.
     */
    public boolean captured()
    {
        return this.captured;
    }
    
    /**
     * Sets if the mouse is captured or not.
     *
     * @param captured The new captured state.
     */
    public void captured(boolean captured)
    {
        this.newCaptured = captured;
    }
    
    /**
     * Toggles the captured state.
     */
    public void toggleCaptured()
    {
        this.newCaptured = !this.captured;
    }
    
    /**
     * @return If the mouse is in the window.
     */
    public boolean entered()
    {
        return this.entered;
    }
    
    /**
     * @return The position vector of the mouse.
     */
    public Vector2dc pos()
    {
        return this.pos;
    }
    
    /**
     * @return The x position of the mouse.
     */
    public double x()
    {
        return this.pos.x;
    }
    
    /**
     * @return The y position of the mouse.
     */
    public double y()
    {
        return this.pos.y;
    }
    
    /**
     * Sets the y position of the window.
     *
     * @param y The new y position.
     */
    public void y(double y)
    {
        this.newPos.y = y;
    }
    
    /**
     * @return The relative position vector of the mouse since last frame.
     */
    public Vector2dc rel()
    {
        return this.rel;
    }
    
    /**
     * @return The relative x position of the mouse since last frame.
     */
    public double relX()
    {
        return this.rel.x;
    }
    
    /**
     * @return The relative y position of the mouse since last frame.
     */
    public double relY()
    {
        return this.rel.y;
    }
    
    /**
     * @return The direction vector of the mouse scroll wheel.
     */
    public Vector2dc scroll()
    {
        return this.scroll;
    }
    
    /**
     * @return The x direction of the mouse scroll wheel.
     */
    public double scrollX()
    {
        return this.scroll.x;
    }
    
    /**
     * @return The y direction of the mouse scroll wheel.
     */
    public double scrollY()
    {
        return this.scroll.y;
    }
    
    /**
     * @return Gets the default Input for this Device.
     */
    @Override
    protected Button getDefault()
    {
        return this.NONE;
    }
    
    /**
     * This is called by the Engine to generate the events and state changes for the Device.
     *
     * @param time  The time in nano seconds that it happened.
     * @param delta The time in nano seconds since the last frame.
     */
    @Override
    public void handleEvents(long time, long delta)
    {
        Mouse.LOGGER.finer("Handling Mouse Events");
        
        profiler().startSection("Captured");
        if (this.captured != this.newCaptured)
        {
            this.captured = this.newCaptured;
            this.newPos.set(this.pos.set(screenWidth() * 0.5, screenHeight() * 0.5));
            Events.post(Event.MOUSE_CAPTURED, this.captured);
        }
        profiler().endSection();
        
        boolean justEntered = false;
        profiler().startSection("Entered");
        if (this.entered != this.newEntered)
        {
            this.entered = this.newEntered;
            if (this.entered)
            {
                justEntered = true;
                this.pos.set(this.newPos);
            }
            Events.post(Event.MOUSE_ENTERED, this.entered);
        }
        profiler().endSection();
        
        profiler().startSection("Position");
        this.rel.set(0);
        if (Double.compare(this.pos.x, this.newPos.x) != 0 || Double.compare(this.pos.y, this.newPos.y) != 0 || justEntered)
        {
            this.newPos.sub(this.pos, this.rel);
            this.pos.set(this.newPos);
            Events.post(Event.MOUSE_MOVED, this.captured ? JOMLUnit.ZERO2d : this.pos, this.rel);
        }
        profiler().endSection();
        
        profiler().startSection("Scroll");
        if (Double.compare(this.scroll.x, this.newScroll.x) != 0 || Double.compare(this.scroll.y, this.newScroll.y) != 0)
        {
            this.scroll.set(this.newScroll);
            this.newScroll.set(0);
            Events.post(Event.MOUSE_SCROLLED, this.scroll);
        }
        profiler().endSection();
        
        profiler().startSection("Device");
        super.handleEvents(time, delta);
        profiler().endSection();
    }
    
    /**
     * This is called by the Device to post any events that it may have generated this frame.
     *
     * @param input The Input
     * @param time  The time in nano seconds that it happened.
     * @param delta The time in nano seconds since the last frame.
     */
    @Override
    protected void postEvents(Button input, long time, long delta)
    {
        if (input.down)
        {
            Events.post(Event.MOUSE_BUTTON_DOWN, input, this.pos);
            
            input.click.set(this.pos);
            if (this.drag == null)
            {
                this.drag = input;
                this.dragPos.set(this.pos);
            }
        }
        if (input.up)
        {
            Events.post(Event.MOUSE_BUTTON_UP, input, this.pos);
            
            boolean inClickRange  = Math.abs(this.pos.x - input.click.x) < 2 && Math.abs(this.pos.y - input.click.y) < 2;
            boolean inDClickRange = Math.abs(this.pos.x - input.dClick.x) < 2 && Math.abs(this.pos.y - input.dClick.y) < 2;
            
            if (inDClickRange && time - input.pressTime < 500_000_000)
            {
                Events.post(Event.MOUSE_BUTTON_CLICKED, input, this.pos, true);
            }
            else if (inClickRange)
            {
                Events.post(Event.MOUSE_BUTTON_CLICKED, input, this.pos, false);
                input.dClick.set(this.pos);
                input.pressTime = time;
            }
            if (this.drag == input) this.drag = null;
        }
        input.dragged = false;
        if (input.held)
        {
            Events.post(Event.MOUSE_BUTTON_HELD, input, this.pos);
    
            if (this.drag == input && (this.rel.x != 0 || this.rel.y != 0))
            {
                input.dragged = true;
        
                Events.post(Event.MOUSE_BUTTON_DRAGGED, input, this.dragPos, this.pos, this.rel);
            }
        }
        if (input.repeat) Events.post(Event.MOUSE_BUTTON_REPEAT, input, this.pos);
    }
    
    /**
     * This is a callback for when {@link org.lwjgl.glfw.GLFW#glfwSetCursorEnterCallback} is called.
     *
     * @param entered If the mouse in over the window
     */
    public void enteredCallback(boolean entered)
    {
        Mouse.LOGGER.finest("Mouse Entered Callback:", entered);
        
        this.newEntered = entered;
    }
    
    /**
     * This is a callback for when {@link org.lwjgl.glfw.GLFW#glfwSetCursorPosCallback} is called.
     *
     * @param x The scaled unbounded mouse x coordinate
     * @param y The scaled unbounded mouse y coordinate
     */
    public void positionCallback(double x, double y)
    {
        Mouse.LOGGER.finest("Mouse Moved Callback:", x, y);
        
        if (!Double.isFinite(x) || !Double.isFinite(y)) return;
        this.newPos.set(x, y);
    }
    
    /**
     * This is a callback for when {@link org.lwjgl.glfw.GLFW#glfwSetScrollCallback} is called.
     *
     * @param x The scaled unbounded mouse scroll x direction
     * @param y The scaled unbounded mouse scroll y direction
     */
    public void scrollCallback(double x, double y)
    {
        Mouse.LOGGER.finest("Mouse Scrolled Callback:", x, y);
        
        this.newScroll.add(x, y);
    }
    
    /**
     * This class represents a button on the mouse.
     */
    public class Button extends Device.Input
    {
        protected boolean dragged;
        
        private final Vector2d click  = new Vector2d();
        private final Vector2d dClick = new Vector2d();
        
        private Button(String name, int reference)
        {
            super(Mouse.this, name, reference);
        }
    
        /**
         * @return If the Button is being dragged with optional modifiers.
         */
        public boolean dragged(Modifiers.Modifier... modifiers)
        {
            return this.dragged && checkModifiers(modifiers);
        }
    }
}
