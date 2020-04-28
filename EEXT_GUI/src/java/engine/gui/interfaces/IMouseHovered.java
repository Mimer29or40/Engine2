package engine.gui.interfaces;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IMouseHovered
{
    /**
     * Called when the mouse is over an element and is hover-able.
     *
     * @param hoverTime The time in seconds that the mouse has been over the element.
     * @param elementX  The x position relative to the top left corner of the UIElement.
     * @param elementY  The y position relative to the top left corner of the UIElement.
     */
    void fire(double hoverTime, double elementX, double elementY);
}
