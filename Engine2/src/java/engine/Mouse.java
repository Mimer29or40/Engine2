package engine;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2dc;
import org.lwjgl.system.Platform;
import rutils.Logger;
import rutils.group.Pair;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse extends InputDevice
{
    private static final Logger LOGGER = new Logger();
    
    // -------------------- State Objects -------------------- //
    
    // -------------------- Callback Objects -------------------- //
    
    protected boolean entered, _entered;
    
    protected final Vector2d pos  = new Vector2d();
    protected final Vector2d _pos = new Vector2d();
    
    protected final Vector2d rel = new Vector2d();
    
    protected final Vector2d scroll  = new Vector2d();
    protected final Vector2d _scroll = new Vector2d();
    
    // -------------------- Internal Objects -------------------- //
    
    protected final Queue<Pair<Button, Integer>> buttonStateChanges = new ConcurrentLinkedQueue<>();
    
    protected final Map<Button, ButtonInput> buttonMap;
    
    Mouse()
    {
        this.buttonMap = new LinkedHashMap<>();
        for (Button button : Mouse.Button.values()) this.buttonMap.put(button, new ButtonInput());
    }
    
    @Override
    public String toString()
    {
        return "Mouse{" + '}';
    }
    
    // -------------------- Properties -------------------- //
    
    /**
     * Makes the cursor visible and behaving normally.
     */
    public void show()
    {
        Engine.runTask(() -> {
            if (glfwGetInputMode(Engine.window.handle, GLFW_CURSOR) == GLFW_CURSOR_DISABLED) this._pos.set(this.pos.set(Engine.window.size).mul(0.5));
            glfwSetInputMode(Engine.window.handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        });
    }
    
    /**
     * @return Retrieves if the mouse is visible and behaving normally in its window.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean isShown()
    {
        return Engine.waitReturnTask(() -> glfwGetInputMode(Engine.window.handle, GLFW_CURSOR) == GLFW_CURSOR_NORMAL);
    }
    
    /**
     * Makes the cursor invisible when it is over the content area of the
     * window but does not restrict the cursor from leaving.
     */
    public void hide()
    {
        Engine.runTask(() -> glfwSetInputMode(Engine.window.handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN));
    }
    
    /**
     * @return Retrieves if the mouse is hidden over its window.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean isHidden()
    {
        return Engine.waitReturnTask(() -> glfwGetInputMode(Engine.window.handle, GLFW_CURSOR) == GLFW_CURSOR_HIDDEN);
    }
    
    /**
     * Hides and grabs the cursor, providing virtual and unlimited cursor
     * movement. This is useful for implementing for example 3D camera
     * controls.
     */
    public void capture()
    {
        this._pos.set(this.pos.set(Engine.window.width() * 0.5, Engine.window.height() * 0.5));
        Engine.runTask(() -> {
            glfwSetCursorPos(Engine.window.handle, this._pos.x, this._pos.y);
            glfwSetInputMode(Engine.window.handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        });
    }
    
    /**
     * @return Retrieves if the mouse is captured by its window.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean isCaptured()
    {
        return Engine.waitReturnTask(() -> glfwGetInputMode(Engine.window.handle, GLFW_CURSOR) == GLFW_CURSOR_DISABLED);
    }
    
    /**
     * Sets the raw mouse motion flag. Set {@code true} to enable raw (unscaled
     * and un-accelerated) mouse motion when the cursor is disabled, or
     * {@code false} to disable it. If raw motion is not supported, attempting
     * to set this will log a warning.
     *
     * @param rawInput {@code true} to enable raw mouse motion mode, otherwise {@code false}.
     */
    public void rawInput(boolean rawInput)
    {
        Engine.runTask(() -> {
            if (!glfwRawMouseMotionSupported())
            {
                Mouse.LOGGER.warning("Raw Mouse Motion is not support on", Platform.get());
                return;
            }
            glfwSetInputMode(Engine.window.handle, GLFW_RAW_MOUSE_MOTION, rawInput ? GLFW_TRUE : GLFW_FALSE);
        });
    }
    
    /**
     * @return Retrieves the raw mouse motion flag.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean rawInputEnabled()
    {
        return Engine.waitReturnTask(() -> {
            if (!glfwRawMouseMotionSupported())
            {
                Mouse.LOGGER.warning("Raw Mouse Motion is not support on", Platform.get());
                return false;
            }
            return glfwGetInputMode(Engine.window.handle, GLFW_RAW_MOUSE_MOTION) == GLFW_TRUE;
        });
    }
    
    /**
     * Sets the sticky mouse buttons flag. If sticky mouse buttons are enabled,
     * a mouse button press will ensure that {@link EventMouseButtonPressed}
     * is posted even if the mouse button had been released before the call.
     * This is useful when you are only interested in whether mouse buttons
     * have been pressed but not when or in which order.
     *
     * @param sticky {@code true} to enable sticky mode, otherwise {@code false}.
     */
    public void sticky(boolean sticky)
    {
        Engine.runTask(() -> glfwSetInputMode(Engine.window.handle, GLFW_STICKY_MOUSE_BUTTONS, sticky ? GLFW_TRUE : GLFW_FALSE));
    }
    
    /**
     * @return Retrieves the sticky mouse buttons flag.
     */
    @SuppressWarnings("ConstantConditions")
    public boolean stickyEnabled()
    {
        return Engine.waitReturnTask(() -> glfwGetInputMode(Engine.window.handle, GLFW_STICKY_MOUSE_BUTTONS) == GLFW_TRUE);
    }
    
    // -------------------- Callback Related Things -------------------- //
    
    /**
     * Returns the position of the cursor, in screen coordinates, relative to
     * the upper-left corner of the content area of the last window the mouse
     * was reported in.
     * <p>
     * If the cursor is captured (with {@link #capture()} ) then the cursor
     * position is unbounded and limited only by the minimum and maximum values
     * of a <b>{@code double}</b>.
     * <p>
     * The coordinates can be converted to their integer equivalents with the
     * {@link Math#floor} function. Casting directly to an integer type works
     * for positive coordinates, but fails for negative ones.
     *
     * @return The position of the cursor, in screen coordinates, relative to the upper-left corner of the content area of the last window the mouse was reported in.
     */
    public @NotNull Vector2dc pos()
    {
        return this.pos;
    }
    
    /**
     * Returns the x position of the cursor, in screen coordinates, relative to
     * the upper-left corner of the content area of the last window the mouse
     * was reported in.
     * <p>
     * If the cursor is captured (with {@link #capture()} ) then the cursor
     * position is unbounded and limited only by the minimum and maximum values
     * of a <b>{@code double}</b>.
     * <p>
     * The coordinates can be converted to their integer equivalents with the
     * {@link Math#floor} function. Casting directly to an integer type works
     * for positive coordinates, but fails for negative ones.
     *
     * @return The x position of the cursor, in screen coordinates, relative to the upper-left corner of the content area of the last window the mouse was reported in.
     */
    public double x()
    {
        return this.pos.x;
    }
    
    /**
     * Returns the y position of the cursor, in screen coordinates, relative to
     * the upper-left corner of the content area of the last window the mouse
     * was reported in.
     * <p>
     * If the cursor is captured (with {@link #capture()} ) then the cursor
     * position is unbounded and limited only by the minimum and maximum values
     * of a <b>{@code double}</b>.
     * <p>
     * The coordinates can be converted to their integer equivalents with the
     * {@link Math#floor} function. Casting directly to an integer type works
     * for positive coordinates, but fails for negative ones.
     *
     * @return The y position of the cursor, in screen coordinates, relative to the upper-left corner of the content area of the last window the mouse was reported in.
     */
    public double y()
    {
        return this.pos.y;
    }
    
    /**
     * Returns the difference in position of the cursor, in screen coordinates,
     * relative to the upper-left corner of the content area of the last window
     * the mouse was reported in since the last time the mouse was updated.
     * <p>
     * This will be {@code {0.0, 0.0}} for the majority of the time as the
     * mouse can update more that once per window frame. It would be better to
     * subscribe to {@link EventMouseMoved} events to get actual relative
     * values.
     * <p>
     * If the cursor is captured (with {@link #capture()} ) then the cursor
     * position is unbounded and limited only by the minimum and maximum values
     * of a <b>{@code double}</b>.
     * <p>
     * The coordinates can be converted to their integer equivalents with the
     * {@link Math#floor} function. Casting directly to an integer type works
     * for positive coordinates, but fails for negative ones.
     *
     * @return The difference in position of the cursor, in screen coordinates, since the last time the mouse was updated.
     */
    public @NotNull Vector2dc rel()
    {
        return this.rel;
    }
    
    /**
     * Returns the difference in x position of the cursor, in screen
     * coordinates, relative to the upper-left corner of the content area of
     * the last window the mouse was reported in since the last time the mouse
     * was updated.
     * <p>
     * This will be {@code 0.0} for the majority of the time as the mouse can
     * update more that once per window frame. It would be better to subscribe
     * to {@link EventMouseMoved} events to get actual relative values.
     * <p>
     * If the cursor is captured (with {@link #capture()} ) then the cursor
     * position is unbounded and limited only by the minimum and maximum values
     * of a <b>{@code double}</b>.
     * <p>
     * The coordinates can be converted to their integer equivalents with the
     * {@link Math#floor} function. Casting directly to an integer type works
     * for positive coordinates, but fails for negative ones.
     *
     * @return The difference in x position of the cursor, in screen coordinates, since the last time the mouse was updated.
     */
    public double dx()
    {
        return this.rel.x;
    }
    
    /**
     * Returns the difference in y position of the cursor, in screen
     * coordinates, relative to the upper-left corner of the content area of
     * the last window the mouse was reported in since the last time the mouse
     * was updated.
     * <p>
     * This will be {@code 0.0} for the majority of the time as the mouse can
     * update more that once per window frame. It would be better to subscribe
     * to {@link EventMouseMoved} events to get actual relative values.
     * <p>
     * If the cursor is captured (with {@link #capture()} ) then the cursor
     * position is unbounded and limited only by the minimum and maximum values
     * of a <b>{@code double}</b>.
     * <p>
     * The coordinates can be converted to their integer equivalents with the
     * {@link Math#floor} function. Casting directly to an integer type works
     * for positive coordinates, but fails for negative ones.
     *
     * @return The difference in y position of the cursor, in screen coordinates, since the last time the mouse was updated.
     */
    public double dy()
    {
        return this.rel.y;
    }
    
    /**
     * Returns the amount that the mouse wheel, or touch-pad, was scrolled in
     * last window the mouse was reported in since the last time the mouse was
     * updated.
     * <p>
     * This will be {@code {0.0, 0.0}} for the majority of the time as the
     * mouse can update more that once per window frame. It would be better to
     * subscribe to {@link EventMouseScrolled} events to get actual scrolled
     * values.
     *
     * @return The amount that the mouse wheel, or touch-pad, was scrolled since the last time the mouse was updated.
     */
    public @NotNull Vector2dc scroll()
    {
        return this.scroll;
    }
    
    /**
     * Returns the amount that the mouse wheel, or touch-pad, was scrolled
     * horizontally in last window the mouse was reported in since the last
     * time the mouse was updated.
     * <p>
     * This will be {@code {0.0, 0.0}} for the majority of the time as the
     * mouse can update more that once per window frame. It would be better to
     * subscribe to {@link EventMouseScrolled} events to get actual scrolled
     * values.
     *
     * @return The amount that the mouse wheel, or touch-pad, was scrolled horizontally since the last time the mouse was updated.
     */
    public double scrollX()
    {
        return this.scroll.x;
    }
    
    /**
     * Returns the amount that the mouse wheel, or touch-pad, was scrolled
     * vertically in last window the mouse was reported in since the last time
     * the mouse was updated.
     * <p>
     * This will be {@code {0.0, 0.0}} for the majority of the time as the
     * mouse can update more that once per window frame. It would be better to
     * subscribe to {@link EventMouseScrolled} events to get actual scrolled
     * values.
     *
     * @return The amount that the mouse wheel, or touch-pad, was scrolled vertically since the last time the mouse was updated.
     */
    public double scrollY()
    {
        return this.scroll.y;
    }
    
    /**
     * This method is called by the window it is attached to. This is where
     * events should be posted to when something has changed.
     *
     * @param time   The system time in nanoseconds.
     * @param deltaT The time in nanoseconds since the last time this method was called.
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    protected void postEvents(long time, long deltaT)
    {
        boolean entered = false;
        if (this.entered != this._entered)
        {
            this.entered = this._entered;
            // GLFW.EVENT_BUS.post(EventMouseEntered.create(this.entered)); // TODO
            if (this.entered)
            {
                entered = true;
                
                this.pos.set(this._pos);
            }
        }
        
        this.rel.set(0);
        if (Double.compare(this.pos.x, this._pos.x) != 0 || Double.compare(this.pos.y, this._pos.y) != 0 || entered)
        {
            this._pos.sub(this.pos, this.rel);
            this.pos.set(this._pos);
            // GLFW.EVENT_BUS.post(EventMouseMoved.create(this._posW, this.pos, this.rel)); // TODO
        }
        
        this.scroll.set(0);
        if (Double.compare(this.scroll.x, this._scroll.x) != 0 || Double.compare(this.scroll.y, this._scroll.y) != 0)
        {
            this.scroll.set(this._scroll);
            this._scroll.set(0);
            // GLFW.EVENT_BUS.post(EventMouseScrolled.create(this._scrollW, this.scroll)); // TODO
        }
        
        Pair<Button, Integer> buttonStateChange;
        while ((buttonStateChange = this.buttonStateChanges.poll()) != null)
        {
            Mouse.ButtonInput buttonObj = this.buttonMap.get(buttonStateChange.getA());
            
            buttonObj._state = buttonStateChange.getB();
        }
        
        for (Button button : this.buttonMap.keySet())
        {
            ButtonInput input = this.buttonMap.get(button);
            
            input.state  = input._state;
            input._state = -1;
            switch (input.state)
            {
                case GLFW_PRESS -> {
                    input.held     = true;
                    input.holdTime = time + InputDevice.holdFrequency;
                    // GLFW.EVENT_BUS.post(EventMouseButtonDown.create(input._window, button, this.pos)); // TODO
                    
                    input.click.set(this.pos);
                }
                case GLFW_RELEASE -> {
                    input.held     = false;
                    input.holdTime = Long.MAX_VALUE;
                    // GLFW.EVENT_BUS.post(EventMouseButtonUp.create(input._window, button, this.pos)); // TODO
                    
                    boolean inClickRange  = Math.abs(this.pos.x - input.click.x) < 2 && Math.abs(this.pos.y - input.click.y) < 2;
                    boolean inDClickRange = Math.abs(this.pos.x - input.dClick.x) < 2 && Math.abs(this.pos.y - input.dClick.y) < 2;
                    
                    if (inDClickRange && time - input.pressTime < InputDevice.doublePressedDelay)
                    {
                        input.pressTime = 0;
                        // GLFW.EVENT_BUS.post(EventMouseButtonPressed.create(input._window, button, this.pos, true)); // TODO
                    }
                    else if (inClickRange)
                    {
                        input.dClick.set(this.pos);
                        input.pressTime = time;
                        // GLFW.EVENT_BUS.post(EventMouseButtonPressed.create(input._window, button, this.pos, false)); // TODO
                    }
                }
                // case GLFW_REPEAT -> GLFW.EVENT_BUS.post(EventMouseButtonRepeated.create(input._window, button, this.pos)); // TODO
            }
            if (input.held)
            {
                if (time - input.holdTime >= InputDevice.holdFrequency)
                {
                    input.holdTime += InputDevice.holdFrequency;
                    // GLFW.EVENT_BUS.post(EventMouseButtonHeld.create(input._window, button, this.pos)); // TODO
                }
                // if (this.rel.x != 0 || this.rel.y != 0) GLFW.EVENT_BUS.post(EventMouseButtonDragged.create(input._window, button, this.pos, this.rel, input.click)); // TODO
            }
        }
    }
    
    static final class ButtonInput extends Input
    {
        final Vector2d click  = new Vector2d();
        final Vector2d dClick = new Vector2d();
    }
    
    public enum Button
    {
        NONE(-1),
        
        LEFT(GLFW_MOUSE_BUTTON_LEFT),
        RIGHT(GLFW_MOUSE_BUTTON_RIGHT),
        MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE),
        
        FOUR(GLFW_MOUSE_BUTTON_4),
        FIVE(GLFW_MOUSE_BUTTON_5),
        SIX(GLFW_MOUSE_BUTTON_6),
        SEVEN(GLFW_MOUSE_BUTTON_7),
        EIGHT(GLFW_MOUSE_BUTTON_8),
        
        ;
        
        private static final HashMap<Integer, Button> BUTTON_MAP = new HashMap<>();
        
        final int ref;
        
        Button(int ref)
        {
            this.ref = ref;
        }
        
        /**
         * @return Gets the ButtonInput that corresponds to the GLFW constant.
         */
        public static Button get(int ref)
        {
            return Mouse.Button.BUTTON_MAP.getOrDefault(ref, Mouse.Button.NONE);
        }
        
        static
        {
            for (Button button : values())
            {
                Mouse.Button.BUTTON_MAP.put(button.ref, button);
            }
        }
    }
}
