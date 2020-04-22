package engine.gui.shapes;

public class StateTransition
{
    public final State start, target;
    
    public StateTransition(State start, State target, double duration, double progress)
    {
        this.start = start;
        this.target = target;
    }
}
