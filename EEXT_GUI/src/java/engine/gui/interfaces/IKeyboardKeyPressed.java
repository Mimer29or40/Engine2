package engine.gui.interfaces;

import engine.Keyboard;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IKeyboardKeyPressed
{
    /**
     * Called when a UIElement is focused and a key is pressed, then released in an amount of time.
     *
     * @param key           The key that was pressed, then released.
     * @param doublePressed If the key was double pressed.
     * @return If the event should be consumed.
     */
    boolean fire(Keyboard.Key key, boolean doublePressed);
}
