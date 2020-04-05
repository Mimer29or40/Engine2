package engine.event;

import engine.Engine;

import java.util.ArrayList;
import java.util.List;

import static engine.util.Util.map;

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
    protected void setup()
    {
        size(800, 400, 2, 2, OPENGL);
        frameRate(10);
        enableBlend(true);
        enableProfiler();
        
        textSize(20);
        
        int lines = (int) ((screenHeight() - 2) / textFont().getStringHeight(""));
        for (int i = 0; i < lines; i++) events.add("");
        
        // Events.subscribe(Events.INPUT_EVENTS, this::onInputEvent);
        // Events.subscribe(EventMouseCaptured.class, this::onInputEvent);
    }
    
    @Override
    protected void draw(double elapsedTime)
    {
        if (keyboard().P.held()) printFrameData("");
        if (keyboard().SPACE.down()) window().toggleFullscreen();
        if (keyboard().C.down()) mouse().toggleCaptured();
        
        // for (Event event : Events.get())
        // for (Event event : Events.get(EventMouseButtonDragged.class, EventMouseButtonDown.class))
        // for (Event event : Events.get(EventKeyboardKeyTyped.class))
        // for (Event event : Events.get(Events.MOUSE_EVENTS))
        // {
        //     println(event.toString());
        // }
        
        for (Event event : Events.get(EventMouseCaptured.class, EventMouseMoved.class))
        {
            addEvent(event.toString());
        }
        
        clear();
        
        int nLog = 0;
        for (String s : events)
        {
            int c = (int) map(nLog, 0, events.size() - 1, 60, 255);
            fill(255, c);
            text(s, 2, nLog * textFont().getStringHeight("") + 2);
            nLog++;
        }
    }
    
    @Override
    protected void destroy()
    {
        
    }
    
    public static void main(String[] args)
    {
        start(new EventTests());
    }
}