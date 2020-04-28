package engine.gui.interfaces;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IMouseExited
{
    /**
     * Called whenever the mouse has left the element or covered by another element.
     */
    void fire();
}
