package engine.gui.interfaces;

import engine.input.Keyboard;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IKeyboardKeyHeld
{
    /**
     * Called when a UIElement is focused and a key is held.
     *
     * @param key The key that was held.
     * @return If the event should be consumed.
     */
    boolean fire(Keyboard.Key key);
}
