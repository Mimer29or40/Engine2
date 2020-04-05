package engine.input;

import engine.event.*;

import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings("unused")
public class Keyboard extends Device<Keyboard.Key>
{
    public final Key NONE = new Key("NONE", GLFW_KEY_UNKNOWN, 0, 0);
    
    public final Key A = new Key("A", GLFW_KEY_A, 'a', 'A');
    public final Key B = new Key("B", GLFW_KEY_B, 'b', 'B');
    public final Key C = new Key("C", GLFW_KEY_C, 'c', 'C');
    public final Key D = new Key("D", GLFW_KEY_D, 'd', 'D');
    public final Key E = new Key("E", GLFW_KEY_E, 'e', 'E');
    public final Key F = new Key("F", GLFW_KEY_F, 'f', 'F');
    public final Key G = new Key("G", GLFW_KEY_G, 'g', 'G');
    public final Key H = new Key("H", GLFW_KEY_H, 'h', 'H');
    public final Key I = new Key("I", GLFW_KEY_I, 'i', 'I');
    public final Key J = new Key("J", GLFW_KEY_J, 'j', 'J');
    public final Key K = new Key("K", GLFW_KEY_K, 'k', 'K');
    public final Key L = new Key("L", GLFW_KEY_L, 'l', 'L');
    public final Key M = new Key("M", GLFW_KEY_M, 'm', 'M');
    public final Key N = new Key("N", GLFW_KEY_N, 'n', 'N');
    public final Key O = new Key("O", GLFW_KEY_O, 'o', 'O');
    public final Key P = new Key("P", GLFW_KEY_P, 'p', 'P');
    public final Key Q = new Key("Q", GLFW_KEY_Q, 'q', 'Q');
    public final Key R = new Key("R", GLFW_KEY_R, 'r', 'R');
    public final Key S = new Key("S", GLFW_KEY_S, 's', 'S');
    public final Key T = new Key("T", GLFW_KEY_T, 't', 'T');
    public final Key U = new Key("U", GLFW_KEY_U, 'u', 'U');
    public final Key V = new Key("V", GLFW_KEY_V, 'v', 'V');
    public final Key W = new Key("W", GLFW_KEY_W, 'w', 'W');
    public final Key X = new Key("X", GLFW_KEY_X, 'x', 'X');
    public final Key Y = new Key("Y", GLFW_KEY_Y, 'y', 'Y');
    public final Key Z = new Key("Z", GLFW_KEY_Z, 'z', 'Z');
    
    public final Key K1 = new Key("K1", GLFW_KEY_1, '1', '!');
    public final Key K2 = new Key("K2", GLFW_KEY_2, '2', '@');
    public final Key K3 = new Key("K3", GLFW_KEY_3, '3', '#');
    public final Key K4 = new Key("K4", GLFW_KEY_4, '4', '$');
    public final Key K5 = new Key("K5", GLFW_KEY_5, '5', '%');
    public final Key K6 = new Key("K6", GLFW_KEY_6, '6', '^');
    public final Key K7 = new Key("K7", GLFW_KEY_7, '7', '&');
    public final Key K8 = new Key("K8", GLFW_KEY_8, '8', '*');
    public final Key K9 = new Key("K9", GLFW_KEY_9, '9', '(');
    public final Key K0 = new Key("K0", GLFW_KEY_0, '0', ')');
    
    public final Key GRAVE      = new Key("GRAVE", GLFW_KEY_GRAVE_ACCENT, '`', '~');
    public final Key MINUS      = new Key("MINUS", GLFW_KEY_MINUS, '-', '_');
    public final Key EQUALS     = new Key("EQUALS", GLFW_KEY_EQUAL, '=', '+');
    public final Key L_BRACKET  = new Key("L_BRACKET", GLFW_KEY_LEFT_BRACKET, '[', '{');
    public final Key R_BRACKET  = new Key("R_BRACKET", GLFW_KEY_RIGHT_BRACKET, ']', '}');
    public final Key BACKSLASH  = new Key("BACKSLASH", GLFW_KEY_BACKSLASH, '\\', '|');
    public final Key SEMICOLON  = new Key("SEMICOLON", GLFW_KEY_SEMICOLON, ';', ':');
    public final Key APOSTROPHE = new Key("APOSTROPHE", GLFW_KEY_APOSTROPHE, '\'', '"');
    public final Key COMMA      = new Key("COMMA", GLFW_KEY_COMMA, ',', '<');
    public final Key PERIOD     = new Key("PERIOD", GLFW_KEY_PERIOD, '.', '>');
    public final Key SLASH      = new Key("SLASH", GLFW_KEY_SLASH, '/', '?');
    
    public final Key F1  = new Key("F1", GLFW_KEY_F1, 0, 0);
    public final Key F2  = new Key("F2", GLFW_KEY_F2, 0, 0);
    public final Key F3  = new Key("F3", GLFW_KEY_F3, 0, 0);
    public final Key F4  = new Key("F4", GLFW_KEY_F4, 0, 0);
    public final Key F5  = new Key("F5", GLFW_KEY_F5, 0, 0);
    public final Key F6  = new Key("F6", GLFW_KEY_F6, 0, 0);
    public final Key F7  = new Key("F7", GLFW_KEY_F7, 0, 0);
    public final Key F8  = new Key("F8", GLFW_KEY_F8, 0, 0);
    public final Key F9  = new Key("F9", GLFW_KEY_F9, 0, 0);
    public final Key F10 = new Key("F10", GLFW_KEY_F10, 0, 0);
    public final Key F11 = new Key("F11", GLFW_KEY_F11, 0, 0);
    public final Key F12 = new Key("F12", GLFW_KEY_F12, 0, 0);
    
    public final Key UP    = new Key("UP", GLFW_KEY_UP, 0, 0);
    public final Key DOWN  = new Key("DOWN", GLFW_KEY_DOWN, 0, 0);
    public final Key LEFT  = new Key("LEFT", GLFW_KEY_LEFT, 0, 0);
    public final Key RIGHT = new Key("RIGHT", GLFW_KEY_RIGHT, 0, 0);
    
    public final Key TAB       = new Key("TAB", GLFW_KEY_TAB, '\t', '\t');
    public final Key CAPS_LOCK = new Key("CAPS_LOCK", GLFW_KEY_CAPS_LOCK, 0, 0);
    public final Key ENTER     = new Key("ENTER", GLFW_KEY_ENTER, '\n', '\n');
    public final Key BACK      = new Key("BACK", GLFW_KEY_BACKSPACE, '\b', '\b');
    public final Key SPACE     = new Key("SPACE", GLFW_KEY_SPACE, ' ', ' ');
    
    public final Key L_SHIFT = new Key("L_SHIFT", GLFW_KEY_LEFT_SHIFT, 0, 0);
    public final Key R_SHIFT = new Key("R_SHIFT", GLFW_KEY_RIGHT_SHIFT, 0, 0);
    public final Key L_CTRL  = new Key("L_CTRL", GLFW_KEY_LEFT_CONTROL, 0, 0);
    public final Key R_CTRL  = new Key("R_CTRL", GLFW_KEY_RIGHT_CONTROL, 0, 0);
    public final Key L_ALT   = new Key("L_ALT", GLFW_KEY_LEFT_ALT, 0, 0);
    public final Key R_ALT   = new Key("R_ALT", GLFW_KEY_RIGHT_ALT, 0, 0);
    public final Key L_SUPER = new Key("L_SUPER", GLFW_KEY_LEFT_SUPER, 0, 0);
    public final Key R_SUPER = new Key("R_SUPER", GLFW_KEY_RIGHT_SUPER, 0, 0);
    
    public final Key MENU         = new Key("MENU", GLFW_KEY_MENU, 0, 0);
    public final Key ESCAPE       = new Key("ESCAPE", GLFW_KEY_ESCAPE, 0, 0);
    public final Key PRINT_SCREEN = new Key("PRINT_SCREEN", GLFW_KEY_PRINT_SCREEN, 0, 0);
    public final Key SCROLL_LOCK  = new Key("SCROLL_LOCK", GLFW_KEY_SCROLL_LOCK, 0, 0);
    public final Key PAUSE        = new Key("PAUSE", GLFW_KEY_PAUSE, 0, 0);
    public final Key INS          = new Key("INS", GLFW_KEY_INSERT, 0, 0);
    public final Key DEL          = new Key("DEL", GLFW_KEY_DELETE, 0, 0);
    public final Key HOME         = new Key("HOME", GLFW_KEY_HOME, 0, 0);
    public final Key END          = new Key("END", GLFW_KEY_END, 0, 0);
    public final Key PAGE_UP      = new Key("PAGE_UP", GLFW_KEY_PAGE_UP, 0, 0);
    public final Key PAGE_DN      = new Key("PAGE_DN", GLFW_KEY_PAGE_DOWN, 0, 0);
    
    public final Key NP0 = new Key("NP0", GLFW_KEY_KP_0, '0', 0);
    public final Key NP1 = new Key("NP1", GLFW_KEY_KP_1, '1', 0);
    public final Key NP2 = new Key("NP2", GLFW_KEY_KP_2, '2', 0);
    public final Key NP3 = new Key("NP3", GLFW_KEY_KP_3, '3', 0);
    public final Key NP4 = new Key("NP4", GLFW_KEY_KP_4, '4', 0);
    public final Key NP5 = new Key("NP5", GLFW_KEY_KP_5, '5', 0);
    public final Key NP6 = new Key("NP6", GLFW_KEY_KP_6, '6', 0);
    public final Key NP7 = new Key("NP7", GLFW_KEY_KP_7, '7', 0);
    public final Key NP8 = new Key("NP8", GLFW_KEY_KP_8, '8', 0);
    public final Key NP9 = new Key("NP9", GLFW_KEY_KP_9, '9', 0);
    
    public final Key NUM_LOCK   = new Key("NUM_LOCK", GLFW_KEY_NUM_LOCK, 0, 0);
    public final Key NP_DIV     = new Key("NP_DIV", GLFW_KEY_KP_DIVIDE, '/', '/');
    public final Key NP_MUL     = new Key("NP_MUL", GLFW_KEY_KP_MULTIPLY, '*', '*');
    public final Key NP_SUB     = new Key("NP_SUB", GLFW_KEY_KP_SUBTRACT, '-', '-');
    public final Key NP_ADD     = new Key("NP_ADD", GLFW_KEY_KP_ADD, '+', '+');
    public final Key NP_DECIMAL = new Key("NP_DECIMAL", GLFW_KEY_KP_DECIMAL, '.', '.');
    public final Key NP_EQUALS  = new Key("NP_EQUALS", GLFW_KEY_KP_EQUAL, '=', '=');
    public final Key NP_ENTER   = new Key("NP_ENTER", GLFW_KEY_KP_ENTER, '\n', '\n');
    
    private String capturedText = "";
    
    /**
     * @return Gets the default Input for this Device.
     */
    @Override
    protected Key getDefault()
    {
        return this.NONE;
    }
    
    /**
     * This is called by the Engine to generate the events and state changes for the Device.
     *
     * @param time  The time in nano seconds that it happened.
     * @param delta The time in nano seconds since the last frame.
     */
    @Override
    public void handleEvents(long time, long delta)
    {
        for (int i = 0, n = this.capturedText.length(); i < n; i++)
        {
            Events.post(EventKeyboardKeyTyped.class, this.capturedText.charAt(i));
        }
        this.capturedText = "";
        
        super.handleEvents(time, delta);
    }
    
    /**
     * This is called by the Device to post any events that it may have generated this frame.
     *
     * @param input The Input
     * @param time  The time in nano seconds that it happened.
     * @param delta The time in nano seconds since the last frame.
     */
    @Override
    protected void postEvents(Key input, long time, long delta)
    {
        if (input.down) Events.post(EventKeyboardKeyDown.class, input);
        if (input.up)
        {
            Events.post(EventKeyboardKeyUp.class, input);
            
            if (time - input.pressTime < Device.doubleDelay)
            {
                Events.post(EventKeyboardKeyPressed.class, input, true);
            }
            else
            {
                Events.post(EventKeyboardKeyPressed.class, input, false);
                input.pressTime = time;
            }
        }
        if (input.held) Events.post(EventKeyboardKeyHeld.class, input);
        if (input.repeat) Events.post(EventKeyboardKeyRepeat.class, input);
    }
    
    /**
     * This is a callback for when {@link org.lwjgl.glfw.GLFW#glfwSetCharCallback} is called.
     *
     * @param codePoint the {@code codePoint} to be converted
     */
    public void charCallback(int codePoint)
    {
        capturedText += Character.toString(codePoint);
    }
    
    /**
     * This class represents a key on the keyboard.
     */
    public class Key extends Device.Input
    {
        // private final int  scancode;
        private final char baseChar;
        private final char shiftChar;
        
        private Key(String name, int reference, int baseChar, int shiftChar)
        {
            super(Keyboard.this, name, reference);
            
            // this.scancode  = reference > 0 ? glfwGetKeyScancode(reference) : 0;
            this.baseChar  = (char) baseChar;
            this.shiftChar = (char) shiftChar;
        }
        
        // /**
        //  * @return The keyboard scancode that represents this Key.
        //  */
        // public int scancode()
        // {
        //     return this.scancode;
        // }
        
        /**
         * @return The baseChar of the keyboard.
         */
        public char baseChar()
        {
            return this.baseChar;
        }
        
        /**
         * @return The character of the keyboard when shift is pressed.
         */
        public char shiftChar()
        {
            return this.shiftChar;
        }
    }
}
