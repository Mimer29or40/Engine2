package engine.gui.interfaces;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IMouseScrolled
{
    /**
     * Called whenever the scroll wheel is scrolled while the mouse is over the UIElement and not covered by another UIElement.
     *
     * @param scrollX The x direction that the scroll wheel was moved.
     * @param scrollY The y direction that the scroll wheel was moved.
     * @return If the event should be consumed.
     */
    boolean fire(double scrollX, double scrollY);
}
