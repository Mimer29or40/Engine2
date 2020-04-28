package engine.gui.interfaces;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IUnfocus
{
    /**
     * Called whenever a UIElement is unfocused.
     */
    void fire();
}
