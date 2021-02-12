package engine.event;

import engine.Keyboard;

abstract class AbstractEventKeyboardKey extends AbstractEventInputDevice implements EventKeyboardKey
{
    private final Keyboard.Key key;
    
    AbstractEventKeyboardKey(Keyboard.Key key)
    {
        super();
        
        this.key = key;
    }
    
    @Override
    public Keyboard.Key key()
    {
        return this.key;
    }
}
