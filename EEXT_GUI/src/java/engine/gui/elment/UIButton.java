package engine.gui.elment;

import engine.gui.GUI;
import engine.gui.UIElement;
import engine.gui.interfaces.IUIContainerLike;
import engine.gui.util.Rectc;
import engine.input.Mouse;
import engine.render.Font;
import engine.render.Texture;

public class UIButton extends UIElement
{
    protected String text, tooltipText;
    
    protected boolean selected;
    
    public UIButton(Rectc rect, IUIContainerLike container, String text, String toolTipText, UIElement parent, String objectID)
    {
        super(rect, container, parent, objectID, "button");
        
        this.text        = text;
        this.tooltipText = toolTipText;
        
        rebuildTheme();
    }
    
    public void select()
    {
        this.selected = true;
        setState("selected");
    }
    
    public void unselect()
    {
        this.selected = false;
        setState(canHover() && hovered() ? "hovered" : "normal");
    }
    
    // ----------------------
    // ----- PROPERTIES -----
    // ----------------------
    
    @Override
    protected void enabledChanged(boolean prevState, boolean newState)
    {
        pushed(false);
        setState(!newState ? "disabled" : canHover() && hovered() ? "hovered" : "normal");
        redraw();
    }
    
    public boolean pushed;
    
    public boolean pushed()
    {
        return this.pushed;
    }
    
    public void pushed(boolean pushed)
    {
        if (this.pushed != pushed)
        {
            this.pushed = pushed;
            setState(pushed() ? "active" : canHover() && hovered() ? "hovered" : "normal");
            redraw();
        }
    }
    
    public void togglePushed()
    {
        pushed(!pushed());
    }
    
    private boolean toggleable = false;
    
    public boolean toggleable()
    {
        return this.toggleable;
    }
    
    public void toggleable(boolean toggleable)
    {
        if (this.toggleable != toggleable)
        {
            this.toggleable = toggleable;
            pushed(false);
        }
    }
    
    public void toggleToggleable()
    {
        toggleable(!toggleable());
    }
    
    // --------------------
    // ----- THEMEING -----
    // --------------------
    
    private Font font;
    
    // private final HashMap<String, Colorc> colors = new HashMap<>();
    
    private Texture normalImage;
    private Texture hoveredImage;
    private Texture selectedImage;
    private Texture disabledImage;
    
    private String textHAlignment        = "center";
    private String textVAlignment        = "center";
    private int    textHAlignmentPadding = 1;
    private int    textVAlignmentPadding = 1;
    
    public Font font()
    {
        return this.font;
    }
    
    public Texture normalImage()
    {
        return this.normalImage;
    }
    
    public Texture hoveredImage()
    {
        return this.hoveredImage;
    }
    
    public Texture selectedImage()
    {
        return this.selectedImage;
    }
    
    public Texture disabledImage()
    {
        return this.disabledImage;
    }
    
    public boolean rebuildImages()
    {
        boolean changed = false;
        
        Texture normalImage = GUI.theme().getImage(this.objectIDs, this.elementIDs, "normal_image");
        if (normalImage != null && !normalImage.equals(this.normalImage))
        {
            this.normalImage   = normalImage;
            this.hoveredImage  = normalImage;
            this.selectedImage = normalImage;
            this.disabledImage = normalImage;
            changed            = true;
        }
        
        Texture hoveredImage = GUI.theme().getImage(this.objectIDs, this.elementIDs, "hovered_image");
        if (hoveredImage != null && !hoveredImage.equals(this.hoveredImage))
        {
            this.hoveredImage = hoveredImage;
            changed           = true;
        }
        
        Texture selectedImage = GUI.theme().getImage(this.objectIDs, this.elementIDs, "selected_image");
        if (selectedImage != null && !selectedImage.equals(this.selectedImage))
        {
            this.selectedImage = selectedImage;
            changed            = true;
        }
        
        Texture disabledImage = GUI.theme().getImage(this.objectIDs, this.elementIDs, "disabled_image");
        if (disabledImage != null && !disabledImage.equals(this.disabledImage))
        {
            this.disabledImage = disabledImage;
            changed            = true;
        }
        
        return changed;
    }
    
    private boolean rebuildTextAlignment()
    {
        boolean anyChanged = false;
        
        String textHAlignment = GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_h_alignment");
        if (!this.textHAlignment.equals(textHAlignment))
        {
            this.textHAlignment = textHAlignment;
            anyChanged          = true;
        }
    
        String textVAlignment = GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_v_alignment");
        if (!this.textVAlignment.equals(textVAlignment))
        {
            this.textVAlignment = textVAlignment;
            anyChanged          = true;
        }
        
        String textHAlignmentPaddingString = GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_h_alignment_padding");
        if (textHAlignmentPaddingString != null)
        {
            int textHAlignmentPadding = Integer.parseInt(textHAlignmentPaddingString);
            if (this.textHAlignmentPadding != textHAlignmentPadding)
            {
                this.textHAlignmentPadding = textHAlignmentPadding;
                anyChanged                 = true;
            }
        }
        
        String textVAlignmentPaddingString = GUI.theme().getMiscData(this.objectIDs, this.elementIDs, "text_v_alignment_padding");
        if (textVAlignmentPaddingString != null)
        {
            int textVAlignmentPadding = Integer.parseInt(textVAlignmentPaddingString);
            if (this.textVAlignmentPadding != textVAlignmentPadding)
            {
                this.textVAlignmentPadding = textVAlignmentPadding;
                anyChanged                 = true;
            }
        }
        
        return anyChanged;
    }
    
    @Override
    public boolean rebuildTheme(boolean anyChanged)
    {
        anyChanged = super.rebuildTheme(anyChanged);
        
        Font font = GUI.theme().getFont(this.objectIDs, this.elementIDs);
        if (this.font == null || this.font.equals(font))
        {
            this.font  = font;
            anyChanged = true;
        }
        
        // this.colors.clear();
        // for (String colorTag : new String[]
        //         {
        //                 "normal_bg",
        //                 "hovered_bg",
        //                 "disabled_bg",
        //                 "selected_bg",
        //                 "active_bg",
        //                 "normal_text",
        //                 "hovered_text",
        //                 "disabled_text",
        //                 "selected_text",
        //                 "active_text",
        //                 "normal_border",
        //                 "hovered_border",
        //                 "disabled_border",
        //                 "selected_border",
        //                 "active_border"
        //         })
        // {
        //     Colorc color = GUI.theme().getColor(this.objectIDs, this.elementIDs, colorTag);
        //     if (!this.colors.containsKey(colorTag) || !this.colors.get(colorTag).equals(color))
        //     {
        //         this.colors.put(colorTag, color);
        //         anyChanged = true;
        //     }
        // }
        
        if (rebuildImages()) anyChanged = true;
        
        if (rebuildTextAlignment()) anyChanged = true;
        
        return anyChanged;
    }
    
    // -------------------
    // ----- UPDATES -----
    // -------------------
    
    // -------------------
    // ----- Drawing -----
    // -------------------
    
    @Override
    public void drawState(String state)
    {
        super.drawState(state);
    }
    
    // ------------------
    // ----- EVENTS -----
    // ------------------
    
    @Override
    public void onMouseEnter()
    {
        super.onMouseEnter();
        
        if (enabled() && canHover() && !pushed()) setState("hovered");
    }
    
    @Override
    public void onMouseExit()
    {
        super.onMouseExit();
        
        if (enabled() && (!toggleable() || !pushed()))
        {
            setState("normal");
            pushed(false);
        }
        
        // if (this.tooltip != null)
        // {
        //     this.tooltip.kill();
        //     this.tooltip = null;
        // }
    }
    
    @Override
    protected void onMouseHover(double hoverTime, double elementX, double elementY)
    {
        super.onMouseHover(hoverTime, elementX, elementY);
        
        // if (this.toolTip == null && this.tooltipText != null && hoverTime > this.toolTipDelay)
        // {
        //     int hoverHeight = this.rect.height() >> 1;
        //     self.tooltip = self.ui_manager.create_tool_tip(text = this.tooltipText,
        //                                                     position = (mouse_pos[0], self.rect.centery),
        //                                                     hover_distance = (0, hoverHeight));
        // }
    }
    
    @Override
    public boolean onMouseButtonDown(Mouse.Button button, double elementX, double elementY)
    {
        if (!super.onMouseButtonDown(button, elementX, elementY) && enabled())
        {
            if (toggleable())
            {
                togglePushed();
                // onButtonDown(button, elementX, elementY);
                // onButtonToggled(button, elementX, elementY, pushed());
            }
            else if (!pushed())
            {
                pushed(true);
                // onButtonDown(button, elementX, elementY);
            }
            // if (this.tooltip != null)
            // {
            //     this.tooltip.kill();
            //     this.tooltip = null;
            // }
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean onMouseButtonUp(Mouse.Button button, double elementX, double elementY)
    {
        if (!super.onMouseButtonUp(button, elementX, elementY) && enabled())
        {
            if (toggleable())
            {
                // onButtonUp(button, elementX, elementY);
            }
            else if (pushed())
            {
                pushed(false);
                // onButtonUp(button, elementX, elementY);
            }
            // if (this.tooltip != null)
            // {
            //     this.tooltip.kill();
            //     this.tooltip = null;
            // }
            return true;
        }
        
        return false;
    }
}
