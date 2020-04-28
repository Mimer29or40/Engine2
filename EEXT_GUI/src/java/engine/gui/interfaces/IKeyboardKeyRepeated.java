package engine.gui.interfaces;

import engine.input.Keyboard;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IKeyboardKeyRepeated
{
    /**
     * Called when a UIElement is focused and a key is repeated.
     *
     * @param key The key that was repeated.
     * @return If the event should be consumed.
     */
    boolean fire(Keyboard.Key key);
}
