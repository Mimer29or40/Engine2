package engine.gui.interfaces;

import engine.input.Mouse;

/**
 * Interface to provide a way to attach a function to UIButton events
 */
public interface IButtonDown
{
    void fire(Mouse.Button mouse, double elementX, double elementY);
}
