package engine.gui.interfaces;

import engine.Keyboard;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IKeyboardKeyDown
{
    /**
     * Called when a UIElement is focused and a key is pressed down.
     *
     * @param key The key that was pressed down.
     * @return If the event should be consumed.
     */
    boolean fire(Keyboard.Key key);
}
