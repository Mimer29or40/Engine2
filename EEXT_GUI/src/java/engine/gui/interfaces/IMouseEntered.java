package engine.gui.interfaces;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IMouseEntered
{
    /**
     * Called when the mouse entered the element and it not covered by another element under the mouse.
     */
    void fire();
}
