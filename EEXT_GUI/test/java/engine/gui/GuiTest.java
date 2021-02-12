package engine.gui;

import engine.Engine;
import engine.Keyboard;
import engine.gui.elment.UIButton;
import engine.gui.elment.UILabel;
import engine.gui.elment.UIVerticalScrollBar;
import engine.gui.util.Rect;

import java.util.logging.Level;

import static engine.gui.EEXT_GUI.*;

public class GuiTest extends Engine
{
    UIButton            button;
    UILabel             label;
    UIVerticalScrollBar scrollBar;
    
    /**
     * This method is called once the engine's environment is fully setup. This is only called once.
     * <p>
     * This is where you call {@link #size} to enable rendering. At which point you can create textures and render to them.
     */
    @Override
    public void setup()
    {
        size(100, 100, 8, 8);
        createGUI(800, 800);
        // createGUI(100, 100);
    
        // button    = new UIButton("Button", new Rect(0, 0, 50, 50), null, "ToolTip", null, "#button");
        // label     = new UILabel("This is a label", new Rect(0, 0, 100, 10), null, null, "#label");
        scrollBar = new UIVerticalScrollBar(0, new Rect(guiWidth() / 2, 0, guiWidth() / 3, guiHeight()), null, null, "#scrollbar");
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
    
        if (keyboard().down(Keyboard.Key.T)) button.toggleToggleable();
        if (keyboard().down(Keyboard.Key.V)) button.toggleVisibility();
        if (keyboard().down(Keyboard.Key.SPACE)) button.toggleEnabled();
        // if (keyboard().A.down()) element.setState("active");
        // if (keyboard().S.down()) element.setState("normal");
    
        // Vector2dc gui = EEXT_GUI.screenToGUI(mouse().x(), mouse().y());
        // element.position((int) gui.x(), (int) gui.y());
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
        start(new GuiTest(), Level.FINE);
    }
}
