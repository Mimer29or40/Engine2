package engine.gui.shapes;

import engine.color.Blend;
import engine.color.Color;
import engine.gui.EEXT_GUI;
import engine.render.Renderer;
import engine.render.Texture;

import java.util.HashMap;

import static engine.util.Util.round;

public class StateTransitionPGGUI
{
    public final HashMap<String, ShapeStatePGGUI> states = new HashMap<>();
    
    public double duration, remaining, startStatePercent, targetStatePercent;
    
    public final String startState, targetState;
    
    public boolean finished = false;
    
    public StateTransitionPGGUI(HashMap<String, ShapeStatePGGUI> states, String startState, String targetState, double duration, double progress)
    {
        this.states.putAll(states);
        this.duration           = duration;
        this.remaining          = this.duration - progress;
        this.startStatePercent  = 1.0;
        this.targetStatePercent = 0.0;
        this.startState         = startState;
        this.targetState        = targetState;
    }
    
    public boolean finished()
    {
        return this.finished;
    }
    
    public void update(double deltaTime)
    {
        this.remaining -= deltaTime;
        if (this.remaining > 0 && this.duration > 0)
        {
            this.startStatePercent  = this.remaining / this.duration;
            this.targetStatePercent = 1 - this.startStatePercent;
        }
        else
        {
            this.finished = true;
        }
    }
    
    public Texture produceBlendedResult()
    {
        Texture result        = this.states.get(this.startState).texture().copy();
        Texture blendedTarget = this.states.get(this.targetState).texture().copy();
        
        int sAlpha = (int) (round(255 * this.startStatePercent));
        int tAlpha = 255 - sAlpha;
        
        Texture sMul = new Texture(result.width(), result.height(), result.channels(), new Color(sAlpha, sAlpha, sAlpha, 255));
        Texture tMul = new Texture(result.width(), result.height(), result.channels(), new Color(tAlpha, tAlpha, tAlpha, 255));
        
        Renderer renderer = EEXT_GUI.INSTANCE.renderer();
        
        renderer.push();
        
        renderer.blend().blendFunc(Blend.Func.ZERO, Blend.Func.SRC_COLOR);
        renderer.blend().blendEquation(Blend.Equation.ADD);
        
        renderer.target(blendedTarget);
        renderer.texture(tMul, 0, 0);
        
        renderer.target(result);
        renderer.texture(sMul, 0, 0);
        
        renderer.blend().blendFunc(Blend.Func.SRC_ALPHA, Blend.Func.ONE_MINUS_SRC_ALPHA);
        renderer.blend().blendEquation(Blend.Equation.ADD);
        
        renderer.texture(blendedTarget, 0, 0);
        
        renderer.pop();
        
        return result;
    }
}
