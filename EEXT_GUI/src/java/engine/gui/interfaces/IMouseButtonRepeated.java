package engine.gui.interfaces;

import engine.Mouse;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IMouseButtonRepeated
{
    /**
     * Called when a mouse button is repeated.
     *
     * @param button   The key that was repeated.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @return If the event should be consumed.
     */
    boolean fire(Mouse.Button button, double elementX, double elementY);
}
