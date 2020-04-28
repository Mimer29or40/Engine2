package engine.gui.interfaces;

import engine.input.Mouse;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IMouseButtonHeld
{
    /**
     * Called when a mouse button is held.
     *
     * @param button   The key that was held.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    boolean fire(Mouse.Button button, double elementX, double elementY);
}
