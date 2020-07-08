package engine.input;

import static org.lwjgl.glfw.GLFW.*;

@SuppressWarnings({"unused"})
public class Modifiers
{
    public final Modifier NONE      = new Modifier("NONE", GLFW_FALSE);
    public final Modifier SHIFT     = new Modifier("SHIFT", GLFW_MOD_SHIFT);
    public final Modifier CONTROL   = new Modifier("CONTROL", GLFW_MOD_CONTROL);
    public final Modifier ALT       = new Modifier("ALT", GLFW_MOD_ALT);
    public final Modifier SUPER     = new Modifier("SUPER", GLFW_MOD_SUPER);
    public final Modifier CAPS_LOCK = new Modifier("CAPS_LOCK", GLFW_MOD_CAPS_LOCK);
    public final Modifier NUM_LOCK  = new Modifier("NUM_LOCK", GLFW_MOD_NUM_LOCK);
    public final Modifier ANY       = new Modifier("ANY", 0xFFFFFFFF);
    
    private boolean lockMods = false;
    
    /**
     * @return If locking modifier keys should lock.
     */
    public boolean lockMods()
    {
        return this.lockMods;
    }
    
    /**
     * Sets if locking modifier keys should lock.
     *
     * @param lockMods The new state.
     */
    public void lockMods(boolean lockMods)
    {
        this.lockMods = lockMods;
    }
    
    /**
     * Toggles if locking modifier keys should lock.
     */
    public void toggleLockMods()
    {
        this.lockMods = !this.lockMods;
    }
    
    public static class Modifier
    {
        private final String name;
        private final int    value;
        
        private Modifier(String name, int value)
        {
            this.name  = name;
            this.value = value;
        }
        
        @Override
        public String toString()
        {
            return getClass().getSimpleName() + "." + this.name;
        }
    
        /**
         * @return The bit value of the modifier.
         */
        public int value()
        {
            return this.value;
        }
        
        /**
         * Determines of the modifier is in the current mods
         *
         * @param mods The mods in a device input
         * @return If the modifier is in the mods.
         */
        public boolean down(int mods)
        {
            return (this.value & mods) > 0;
        }
    }
}
