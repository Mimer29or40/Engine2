package engine.gui.shape;

import engine.color.Color;
import engine.gui.GUI;
import engine.gui.util.Rect;
import engine.render.Font;
import engine.render.Texture;
import engine.util.PairS;
import org.joml.Vector2i;

import java.util.ArrayDeque;
import java.util.HashMap;

public class ShapeOld
{
    public final Vector2i size    = new Vector2i();
    public final Vector2i textPos = new Vector2i();
    
    public int shadowSize = 0;
    public int borderSize = 0;
    
    public final HashMap<String, Object>   themes;
    public final HashMap<String, StateOld> states = new HashMap<>();
    
    public StateOld activeState;
    public StateOld prevState;
    
    public       Texture baseTexture;
    public final Texture texture;
    
    public double transitionDuration   = 0;
    public double transitionRemaining  = 0;
    public double transitionPercentage = 0;
    
    public final HashMap<PairS, Double> transitionTimes = new HashMap<>();
    
    public boolean hasFreshSurface = true;
    public boolean needToCleanUp   = true;
    
    public boolean shouldTriggerFullRebuild              = true;
    public double  timeUntilFullRebuildAfterChangingSize = 0.35;
    public double  fullRebuildCountdown                  = this.timeUntilFullRebuildAfterChangingSize;
    
    public final ArrayDeque<String> statesToRedrawQueue = new ArrayDeque<>();
    
    public final Rect clickAreaRect  = new Rect();
    public final Rect borderRect     = new Rect();
    public final Rect backgroundRect = new Rect();
    
    public ShapeOld(int width, int height, HashMap<String, Object> themes, String[] states)
    {
        this.size.set(width, height);
        this.themes = themes;
        for (String state : states) this.states.put(state, new StateOld(state));
        
        if (!this.states.containsKey("normal")) throw new RuntimeException("No 'normal' state supplied for drawable shape");
        this.activeState = this.states.get("normal");
        this.prevState   = null;
        
        this.texture = new Texture(width, height);
    }
    
    public void setActiveState(String state)
    {
        if (this.states.containsKey(state) && !this.activeState.name.equals(state))
        {
            this.prevState       = this.activeState;
            this.activeState     = this.states.get(state);
            this.hasFreshSurface = true;
            
            PairS statePair = new PairS(this.prevState.name, this.activeState.name);
            if (this.prevState != null && this.transitionTimes.containsKey(statePair))
            {
                this.transitionDuration   = this.transitionTimes.get(statePair);
                this.transitionRemaining  = this.transitionDuration - this.transitionRemaining;
                this.transitionPercentage = 0;
            }
        }
    }
    
    public void update(double deltaTime)
    {
        if (!this.statesToRedrawQueue.isEmpty()) redrawState(this.statesToRedrawQueue.pollFirst());
        if (this.needToCleanUp && this.statesToRedrawQueue.isEmpty())
        {
            this.cleanUpTempShapes();
            this.needToCleanUp = false;
        }
        if (this.fullRebuildCountdown > 0) this.fullRebuildCountdown -= deltaTime;
        if (this.shouldTriggerFullRebuild && this.fullRebuildCountdown <= 0) fullRebuildOnSizeChange();
        
        if (this.transitionDuration > 0)
        {
            this.transitionRemaining -= deltaTime;
            if (this.transitionRemaining > 0)
            {
                this.transitionPercentage = 1 - (this.transitionRemaining / this.transitionDuration);
                this.hasFreshSurface      = true;
            }
            else
            {
                this.transitionDuration   = 0;
                this.transitionRemaining  = 0;
                this.transitionPercentage = 0;
            }
        }
    }
    
    public void fullRebuildOnSizeChange()
    {
        if (this.themes.containsKey("shadow_width")) this.shadowSize = (int) this.themes.get("shadow_width");
        if (this.themes.containsKey("border_width")) this.borderSize = (int) this.themes.get("border_width");
        this.shouldTriggerFullRebuild = false;
        this.fullRebuildCountdown     = this.timeUntilFullRebuildAfterChangingSize;
    }
    
    public void redrawAllStates()
    {
        this.statesToRedrawQueue.clear();
        this.statesToRedrawQueue.addAll(this.states.keySet());
        this.redrawState(this.statesToRedrawQueue.pollFirst());
    }
    
    public void computeAlignedTextRect()
    {
        if (!this.themes.containsKey("text") || ((String) this.themes.get("text")).length() <= 0 || !this.themes.containsKey("font")) return;
        
        String text       = (String) this.themes.get("text");
        Font   font       = (Font) this.themes.get("font");
        int    textWidth  = (int) font.getStringWidth(text);
        int    textHeight = (int) font.getStringHeight(text);
        
        String hAlign = (String) this.themes.get("text_horiz_alignment");
        switch (hAlign)
        {
            case "left":
                this.textPos.x = (int) this.themes.get("text_horiz_alignment_padding") + this.shadowSize + this.borderSize;
                break;
            case "right":
                this.textPos.x = this.size.x() - (int) this.themes.get("text_horiz_alignment_padding") - textWidth - this.shadowSize - this.borderSize;
                break;
            case "center":
            default:
                this.textPos.x = (this.size.x() - textWidth) >> 1;
                break;
        }
        String vAlign = (String) this.themes.get("text_vert_alignment");
        switch (vAlign)
        {
            case "top":
                this.textPos.y = (int) this.themes.get("text_vert_alignment_padding") + this.shadowSize + this.borderSize;
                break;
            case "bottom":
                this.textPos.y = this.size.y() - (int) this.themes.get("text_vert_alignment_padding") - textHeight - this.shadowSize - this.borderSize;
                break;
            case "center":
            default:
                this.textPos.y = (this.size.y() - textHeight) >> 1;
                break;
        }
    }
    
    public Texture getActiveStateTexture()
    {
        if (this.activeState == null) return GUI.INSTANCE.getEmptyTexture();
        if (this.transitionDuration > 0)
        {
            GUI.INSTANCE.renderer().push();
            GUI.INSTANCE.renderer().target(this.texture);
            GUI.INSTANCE.renderer().interpolateTexture(this.prevState.texture, this.activeState.texture, this.transitionPercentage, 0, 0);
            GUI.INSTANCE.renderer().pop();
            return this.texture;
        }
        return this.activeState.texture;
    }
    
    public Texture getTexture(String state)
    {
        Texture texture;
        if (this.states.containsKey(state) && (texture = this.states.get(state).texture) != null) return texture;
        if ((texture = this.states.get("normal").texture) != null) return texture;
        return this.texture;
    }
    
    public Texture getFreshSurface()
    {
        this.hasFreshSurface = false;
        return getActiveStateTexture();
    }
    
    public void rebuildImageAndText(String imageState, String state, String textColorState)
    {
        GUI.INSTANCE.renderer().push();
        GUI.INSTANCE.renderer().target(this.states.get(state).texture);
        
        Texture texture;
        if (this.themes.containsKey(imageState) && (texture = (Texture) this.themes.get(imageState)) != null)
        {
            int x = (this.size.x() - texture.width()) >> 1;
            int y = (this.size.y() - texture.height()) >> 1;
            GUI.INSTANCE.renderer().texture(texture, x, y);
        }
        if (this.themes.containsKey("text") && this.themes.containsKey("font") && this.themes.get("text") != null)
        {
            String text = (String) this.themes.get("text");
            if (text.length() > 0)
            {
                GUI.INSTANCE.renderer().textFont((Font) this.themes.get("font"));
                if (this.themes.containsKey("text_shadow"))
                {
                    GUI.INSTANCE.renderer().stroke((Color) this.themes.get("text_shadow"));
                    GUI.INSTANCE.renderer().text(text, this.textPos.x(), this.textPos.y() + 1);
                    GUI.INSTANCE.renderer().text(text, this.textPos.x(), this.textPos.y() - 1);
                    GUI.INSTANCE.renderer().text(text, this.textPos.x() + 1, this.textPos.y());
                    GUI.INSTANCE.renderer().text(text, this.textPos.x() - 1, this.textPos.y());
                }
                
                if (this.themes.containsKey(textColorState))
                {
                    GUI.INSTANCE.renderer().stroke((Color) this.themes.get(textColorState));
                    GUI.INSTANCE.renderer().text(text, this.textPos.x(), this.textPos.y());
                }
            }
            
        }
        GUI.INSTANCE.renderer().pop();
    }
    
    public void redrawState(String state)
    {
    
    }
    
    public void cleanUpTempShapes()
    {
    
    }
    
    public boolean collidePoint(int x, int y)
    {
        return false;
    }
    
    public void setDimensions(int width, int height)
    {
    
    }
}
