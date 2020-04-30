package engine.gui;

import engine.Engine;
import engine.gui.elment.UIButton;
import engine.gui.elment.UILabel;
import engine.gui.util.Rect;
import engine.util.IPair;

import java.util.logging.Level;

import static engine.gui.GUI.createGUI;

public class GuiTest extends Engine
{
    UIElement element;
    UILabel label;
    
    /**
     * This method is called once the engine's environment is fully setup. This is only called once.
     * <p>
     * This is where you call {@link #size} to enable rendering. At which point you can create textures and render to them.
     */
    @Override
    public void setup()
    {
        size(100, 100, 8, 8);
        createGUI(200, 200);
        
        element = new UIButton(new Rect(0, 0, 50, 50), null, "Button", "ToolTip", null, "#button");
        label = new UILabel("This is a label", new Rect(0, 0, 100, 10), null, null, "#label");
    }
    
    /**
     * This method is called once per frame.
     *
     * @param elapsedTime The time in seconds since the last frame.
     */
    @Override
    public void draw(double elapsedTime)
    {
        clear();
        
        translate(screenWidth() >> 1, screenHeight() >> 1);
        
        circle(0, 0, 100);
    
        if (keyboard().T.down()) ((UIButton) element).toggleToggleable();
        if (keyboard().V.down()) element.toggleVisibility();
        if (keyboard().SPACE.down()) element.toggleEnabled();
        // if (keyboard().A.down()) element.setState("active");
        // if (keyboard().S.down()) element.setState("normal");
    
        IPair<Double, Double> gui = GUI.screenToGUI(mouse().x(), mouse().y());
        
        element.position((int) ((double) gui.getA()), (int) ((double) gui.getB()));
    }
    
    /**
     * This method is called after the render loop has exited for any reason, exception or otherwise. This is only called once.
     */
    @Override
    public void destroy()
    {
        
    }
    
    public static void main(String[] args)
    {
        start(new GuiTest(), Level.FINER);
    }
}