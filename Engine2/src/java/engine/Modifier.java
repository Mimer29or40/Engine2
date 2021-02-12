package engine;

import java.util.function.Predicate;

import static org.lwjgl.glfw.GLFW.*;

public enum Modifier implements Predicate<Integer>
{
    NONE(0x00000000),
    SHIFT(GLFW_MOD_SHIFT),
    CONTROL(GLFW_MOD_CONTROL),
    ALT(GLFW_MOD_ALT),
    SUPER(GLFW_MOD_SUPER),
    CAPS_LOCK(GLFW_MOD_CAPS_LOCK),
    NUM_LOCK(GLFW_MOD_NUM_LOCK),
    ANY(0xFFFFFFFF),
    ;
    
    private static int activeMods = 0;
    
    public static int activeMods()
    {
        return Modifier.activeMods;
    }
    
    /**
     * Internal methods to update the active modifier bitmap.
     *
     * @param mods The updated modifier bitmap.
     */
    static void updateMods(int mods)
    {
        Modifier.activeMods = mods;
    }
    
    private static boolean lockMods = false;
    
    /**
     * @return Retrieves the lock mods flag.
     */
    public static boolean lockMods()
    {
        return Modifier.lockMods;
    }
    
    /**
     * Sets the lock mods flag. Set {@code true} to enable lock key modifier
     * bits, or {@code false} to disable them. If enabled, callbacks that
     * receive modifier bits will also have the {@link Modifier#CAPS_LOCK}
     * set when the event was generated with Caps Lock on, and the
     * {@link Modifier#NUM_LOCK} set when Num Lock was on.
     *
     * @param lockMods {@code true} to enable lockMods mode, otherwise {@code false}.
     */
    public static void lockMods(boolean lockMods)
    {
        Modifier.lockMods = lockMods;
        Engine.runTask(() -> glfwSetInputMode(Engine.window.handle, GLFW_LOCK_KEY_MODS, lockMods ? GLFW_TRUE : GLFW_FALSE));
    }
    
    private final int value;
    
    Modifier(int value)
    {
        this.value = value;
    }
    
    public int value()
    {
        return this.value;
    }
    
    /**
     * Checks if this modifier is active.
     *
     * @return {@code true} if this modifier is active, otherwise {@code false}
     */
    public boolean test()
    {
        return test(Modifier.activeMods);
    }
    
    /**
     * Checks if this modifier is active in the provided bitmap.
     *
     * @param mods The modifier bitmap.
     * @return {@code true} if this modifier is active in the bitmap, otherwise {@code false}
     */
    @Override
    public boolean test(Integer mods)
    {
        return this.value == -1 || (mods == 0 && this.value == 0) || (mods & this.value) == this.value;
    }
    
    public static boolean test(Modifier... modifiers)
    {
        if (modifiers.length == 0) return Modifier.activeMods == 0;
        Predicate<Integer> predicate = null;
        for (Modifier modifier : modifiers) predicate = predicate != null ? predicate.and(modifier) : modifier;
        return predicate.test(Modifier.activeMods);
    }
}
