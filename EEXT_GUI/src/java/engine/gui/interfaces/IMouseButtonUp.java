package engine.gui.interfaces;

import engine.input.Mouse;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IMouseButtonUp
{
    /**
     * Called when a mouse button is released.
     *
     * @param button   The key that was released.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    boolean fire(Mouse.Button button, double elementX, double elementY);
}
