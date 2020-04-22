package engine.gui.shapes;

import engine.color.Blend;
import engine.color.Colorc;
import engine.gui.EEXT_GUI;
import engine.gui.util.Rect;
import engine.gui.util.Rectc;
import engine.render.Font;
import engine.render.Renderer;
import engine.render.Texture;
import engine.util.Pair;

import java.util.ArrayDeque;
import java.util.HashMap;

public class Shape
{
    public final Rect containingRect = new Rect();
    
    public final HashMap<String, Object> themeing;
    
    public int shadowWidth = 0;
    public int borderWidth = 0;
    
    public final HashMap<String, ShapeState> states = new HashMap<>();
    
    public ShapeState activeState, previousState;
    
    public final HashMap<Pair<String, String>, Double> stateTransitionTimes = new HashMap<>();
    
    public final Rect alignedTextRect = new Rect();
    
    public final ArrayDeque<String> statesToRedrawQueue = new ArrayDeque<>();
    
    public boolean needToCleanUp = true;
    
    public boolean shouldTriggerFullRebuild              = true;
    public double  timeUntilFullRebuildAfterChangingSize = 0.35;
    public double  fullRebuildCountdown                  = this.timeUntilFullRebuildAfterChangingSize;
    
    public Shape(Rectc containingRect, HashMap<String, Object> themeing, String[] states)
    {
        this.containingRect.set(containingRect);
        
        if (this.containingRect.width() < 1) this.containingRect.width(1);
        if (this.containingRect.height() < 1) this.containingRect.height(1);
        
        this.themeing = themeing;
        
        for (String state : states) this.states.put(state, new ShapeState(state));
        
        this.activeState   = this.states.get("normal");
        this.previousState = null;
    }
    
    public void setActiveState(String state)
    {
        if (this.states.containsKey(state) && !this.activeState.id().equals(state))
        {
            this.previousState            = this.activeState;
            this.activeState              = this.states.get(state);
            this.activeState.freshSurface = true;
            
            Pair<String, String> statePair = new Pair<>(this.previousState.id(), this.activeState.id());
            if (this.previousState != null && this.stateTransitionTimes.containsKey(statePair))
            {
                double duration = this.stateTransitionTimes.get(statePair);
                if (this.previousState.transition == null)
                {
                    this.activeState.transition = new StateTransition(this.states, this.previousState.id(), this.activeState.id(), duration, 0);
                }
                else
                {
                    double progress = this.previousState.transition.remaining;
                    this.activeState.transition = new StateTransition(this.states, this.previousState.id(), this.activeState.id(), duration, progress);
                }
            }
        }
    }
    
    public void update(double deltaTime)
    {
        if (this.statesToRedrawQueue.size() > 0) redrawState(this.statesToRedrawQueue.pollFirst());
        if (this.needToCleanUp && !this.statesToRedrawQueue.isEmpty())
        {
            cleanUpTempShapes();
            this.needToCleanUp = false;
        }
        
        if (this.fullRebuildCountdown > 0) this.fullRebuildCountdown -= deltaTime;
        
        if (this.shouldTriggerFullRebuild && this.fullRebuildCountdown <= 0) fullRebuildOnSizeChange();
        
        this.activeState.update(deltaTime);
    }
    
    public void fullRebuildOnSizeChange()
    {
        if (this.themeing.containsKey("shadow_width")) this.shadowWidth = (int) this.themeing.get("shadow_width");
        if (this.themeing.containsKey("border_width")) this.borderWidth = (int) this.themeing.get("border_width");
        this.shouldTriggerFullRebuild = false;
        this.fullRebuildCountdown     = this.timeUntilFullRebuildAfterChangingSize;
    }
    
    public void redrawAllStates()
    {
        this.statesToRedrawQueue.clear();
        this.statesToRedrawQueue.addAll(this.states.keySet());
        redrawState(this.statesToRedrawQueue.pollFirst());
    }
    
    public void computeAlignedTextRect()
    {
        if (!this.themeing.containsKey("text") || ((String) this.themeing.get("text")).length() <= 0 || !this.themeing.containsKey("font")) return;
        
        String text = (String) this.themeing.get("text");
        Font   font = (Font) this.themeing.get("font");
        this.alignedTextRect.set(0, 0, (int) font.getStringWidth(text), (int) font.getStringHeight(text));
        
        String hAlign = (String) this.themeing.get("text_horiz_alignment");
        switch (hAlign)
        {
            case "left":
                this.alignedTextRect.x((int) this.themeing.get("text_horiz_alignment_padding") + this.shadowWidth + this.borderWidth);
                break;
            case "right":
                this.alignedTextRect.x(this.containingRect.width() - (int) this.themeing.get("text_horiz_alignment_padding") - this.alignedTextRect.width() - this.shadowWidth - this.borderWidth);
                break;
            case "center":
            default:
                this.alignedTextRect.centerX(this.containingRect.width() >> 1);
                break;
        }
        String vAlign = (String) this.themeing.get("text_vert_alignment");
        switch (vAlign)
        {
            case "top":
                this.alignedTextRect.y((int) this.themeing.get("text_vert_alignment_padding") + this.shadowWidth + this.borderWidth);
                break;
            case "bottom":
                this.alignedTextRect.y(this.containingRect.height() - (int) this.themeing.get("text_vert_alignment_padding") - this.alignedTextRect.height() - this.shadowWidth - this.borderWidth);
                break;
            case "center":
            default:
                this.alignedTextRect.centerY(this.containingRect.height() >> 1);
                break;
        }
    }
    
    public Texture getActiveStateTexture()
    {
        if (this.activeState != null) return this.activeState.texture();
        return EEXT_GUI.INSTANCE.getEmptyTexture();
    }
    
    public Texture getTexture(String state)
    {
        if (this.states.containsKey(state) && this.states.get(state).texture() != null) return this.states.get(state).texture();
        if (this.states.containsKey(state) && this.states.get("normal").texture() != null) return this.states.get("normal").texture();
        return new Texture(0, 0);
    }
    
    public Texture getFreshTexture()
    {
        this.activeState.freshSurface = false;
        return getActiveStateTexture();
    }
    
    public boolean hasFreshSurface()
    {
        return this.activeState.freshSurface;
    }
    
    public void rebuildImageAndText(String imageState, String state, String textColorState)
    {
        // #Draw any themed images
        // if image_state_str in self.theming and self.theming[image_state_str] is not None:
        //     image_rect = self.theming[image_state_str].get_rect()
        //         image_rect.center = (int(self.containing_rect.width / 2),
        //                                  int(self.containing_rect.height / 2))
        //         self.states[state_str].surface.blit(self.theming[image_state_str], image_rect)
        //         #Draw any text
        // if 'text' in self.theming and 'font' in self.theming and self.theming['text'] is not None:
        //     if len(self.theming['text']) > 0 and text_colour_state_str in self.theming:
        //     if not isinstance (self.theming[text_colour_state_str], ColourGradient):
        //         text_surface = self.theming['font'].render(self.theming['text'], True,
        //                                                    self.theming[text_colour_state_str])
        //                 else:
        //         text_surface = self.theming['font'].render(self.theming['text'], True,
        //                                                    pygame.Color('#FFFFFFFF'))
        //         self.theming[text_colour_state_str].apply_gradient_to_surface(text_surface)
        //             else:
        //         text_surface = None
        //
        //         if 'text_shadow' in self.theming:
        //     text_shadow = self.theming['font'].render(self.theming['text'],
        //                                               True, self.theming['text_shadow'])
        //
        //         self.states[state_str].surface.blit(text_shadow,
        //                                             (self.aligned_text_rect.x,
        //                                             self.aligned_text_rect.y + 1))
        //         self.states[state_str].surface.blit(text_shadow,
        //                                             (self.aligned_text_rect.x,
        //                                             self.aligned_text_rect.y - 1))
        //         self.states[state_str].surface.blit(text_shadow,
        //                                             (self.aligned_text_rect.x + 1,
        //                                             self.aligned_text_rect.y))
        //         self.states[state_str].surface.blit(text_shadow,
        //                                             (self.aligned_text_rect.x - 1,
        //                                             self.aligned_text_rect.y))
        //
        //         if text_surface is not None and self.aligned_text_rect is not None:
        //         self.states[state_str].surface.blit(text_surface, self.aligned_text_rect)
    }
    
    public void redrawState(String state)
    {
    
    }
    
    public void cleanUpTempShapes()
    {
    
    }
    
    public void collidePoint(double x, double y)
    {
    
    }
    
    public void setPosition(double x, double y)
    {
    
    }
    
    public void setDimensions(double width, double height)
    {
    
    }
    
    public static void applyColorToSurface(Colorc color, Texture shapeTexture, Rectc rect)
    {
        Renderer renderer = EEXT_GUI.INSTANCE.renderer();
        
        renderer.push();
        
        renderer.blend().blendFunc(Blend.Func.ZERO, Blend.Func.SRC_COLOR);
        renderer.blend().blendEquation(Blend.Equation.ADD);
        
        Texture colorTexture;
        if (rect != null)
        {
            colorTexture = new Texture(rect.width(), rect.height(), 4, color);
        }
        else
        {
            colorTexture = new Texture(shapeTexture.width(), shapeTexture.height(), 4, color);
        }
        renderer.target(shapeTexture);
        renderer.texture(colorTexture, 0, 0);
        
        renderer.pop();
    }
}
