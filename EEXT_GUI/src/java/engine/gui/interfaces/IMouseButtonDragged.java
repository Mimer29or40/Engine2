package engine.gui.interfaces;

import engine.Mouse;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IMouseButtonDragged
{
    /**
     * Called whenever the mouse is dragged over an element.
     *
     * @param button   The button is down.
     * @param elementX The x position relative to the top left corner of the UIElement.
     * @param elementY The y position relative to the top left corner of the UIElement.
     * @param dragX    The absolute x position of the start of the drag.
     * @param dragY    The absolute y position of the start of the drag.
     * @param relX     The relative x position since the last frame.
     * @param relY     The relative y position since the last frame.
     * @return If the event should be consumed.
     */
    boolean fire(Mouse.Button button, double elementX, double elementY, double dragX, double dragY, double relX, double relY);
}
