package engine.gui.interfaces;

import engine.Mouse;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IMouseButtonClicked
{
    /**
     * Called when a mouse button is pressed, then released in an amount of time.
     *
     * @param button        The key that was pressed, then released.
     * @param elementX      The x position relative to the top left corner of the UIElement.
     * @param elementY      The y position relative to the top left corner of the UIElement.
     * @param doubleClicked If the button was double clicked
     * @return If the event should be consumed.
     */
    boolean fire(Mouse.Button button, double elementX, double elementY, boolean doubleClicked);
}
