package engine.gui.interfaces;

/**
 * Interface to provide a way to attach a function to UIElement events
 */
public interface IKeyboardKeyTyped
{
    /**
     * Fired whenever a UIElement is focused and a key is pressed that has a character associated with it.
     *
     * @param character The key's character.
     * @return If the event should be consumed.
     */
    boolean fire(char character);
}
