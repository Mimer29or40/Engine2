package engine.gui.interfaces;

import engine.Mouse;

/**
 * Interface to provide a way to attach a function to UIButton events
 */
public interface IButtonClicked
{
    void fire(Mouse.Button mouse, double elementX, double elementY, boolean doubleClicked);
}
