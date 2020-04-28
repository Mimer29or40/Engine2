package engine.gui.interfaces;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IFocus
{
    /**
     * Called whenever a UIElement is focused.
     */
    void fire();
}
