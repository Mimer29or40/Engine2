package engine.gui.interfaces;

import engine.input.Keyboard;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IKeyboardKeyUp
{
    /**
     * Called when a UIElement is focused and a key is released.
     *
     * @param key The key that was released.
     * @return If the event should be consumed.
     */
    boolean fire(Keyboard.Key key);
}
