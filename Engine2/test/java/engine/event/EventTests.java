package engine.event;

import engine.Engine;
import engine.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static rutils.NumUtil.map;

public class EventTests extends Engine
{
    List<String> events = new ArrayList<>();
    
    void onInputEvent(Event event)
    {
        addEvent(event.toString());
    }
    
    void addEvent(String event)
    {
        events.add(event);
        events.remove(0);
    }
    
    @Override
    public void setup()
    {
        size(800, 400, 2, 2);
        // frameRate(10);
        
        textSize(20);
        
        int lines = (int) ((screenHeight() - 2) / textFont().getTextHeight("", textSize()));
        for (int i = 0; i < lines; i++) events.add("");
        
        // Events.subscribe(Events.INPUT_EVENTS, this::onInputEvent);
        // Events.subscribe(EventMouseCaptured.class, this::onInputEvent);
        profiler().enabled(true);
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        if (keyboard().down(Keyboard.Key.SPACE)) window().windowed(!window().windowed());
        if (keyboard().down(Keyboard.Key.C))
        {
            if (mouse().isCaptured())
            {
                mouse().show();
            }
            else
            {
                mouse().capture();
            }
        }
    
        // for (Event event : Events.get())
        // for (Event event : Events.get(EventMouseButtonDragged.class, EventMouseButtonDown.class))
        // for (Event event : Events.get(EventKeyboardKeyTyped.class))
        // for (Event event : Events.get(Events.MOUSE_EVENTS))
        // {
        //     println(event.toString());
        // }
    
        // for (Event event : Events.get(EventGroup.MOUSE))
        // {
        //     addEvent(event.toString());
        // }
        
        clear();
        
        int nLog = 0;
        for (String s : events)
        {
            int c = (int) map(nLog, 0, events.size() - 1, 60, 255);
            fill(255, c);
            text(s, 2, nLog * textFont().getTextHeight("", textSize()) + 2);
            nLog++;
        }
    }
    
    @EventBus.Subscribe
    public void mouseEvent(EventMouse event)
    {
        addEvent(event.toString());
    }
    
    @Override
    public void destroy()
    {
        
    }
    
    public static void main(String[] args)
    {
        start(new EventTests(), Level.INFO);
    }
}
