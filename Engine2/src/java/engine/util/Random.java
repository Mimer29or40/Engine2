package engine.util;

import engine.color.Color;
import org.joml.*;

import java.util.Collection;

/**
 * An extension to Java's built-in Random class that adds some useful methods to random
 * numbers.
 * <p>
 * It also adds random functions for arrays, collections, JOML Vectors, and {@code Color}'s
 */
@SuppressWarnings({"StatementWithEmptyBody", "unused"})
public class Random extends java.util.Random
{
    public Random()
    {
        super();
    }
    
    public Random(long seed)
    {
        super(seed);
    }
    
    /**
     * @return A random uniformly distributed {@code int} [{@code origin} - {@code bound}].
     */
    public int nextInt(int origin, int bound)
    {
        if (origin >= bound) throw new IllegalArgumentException("origin must be less than bound");
        return origin + nextInt(bound - origin);
    }
    
    /**
     * @return A random uniformly distributed {@code long} [{@code 0} - {@code bound}].
     */
    public long nextLong(long bound)
    {
        if (bound <= 0) throw new IllegalArgumentException("bound must be positive");
        long r = nextLong(), m = bound - 1;
        if ((bound & m) == 0L) { r &= m; }
        else { for (long u = r >>> 1; u + m - (r = u % bound) < 0L; u = nextLong()) ; }
        return r;
    }
    
    /**
     * @return A random uniformly distributed {@code long} [{@code origin} - {@code bound}].
     */
    public long nextLong(long origin, long bound)
    {
        if (origin >= bound) throw new IllegalArgumentException("origin must be less than bound");
        return origin + nextLong(bound - origin);
    }
    
    /**
     * @return A random uniformly distributed {@code float} [{@code 0} - {@code bound}].
     */
    public float nextFloat(float bound)
    {
        if (bound <= 0) throw new IllegalArgumentException("bound must be positive");
        return nextFloat() * bound;
    }
    
    /**
     * @return A random uniformly distributed {@code float} [{@code origin} - {@code bound}].
     */
    public float nextFloat(float origin, float bound)
    {
        if (origin >= bound) throw new IllegalArgumentException("origin must be less than bound");
        return origin + (bound - origin) * nextFloat();
    }
    
    /**
     * @return A random uniformly distributed {@code float} [{@code -1} - {@code 1}].
     */
    public float nextFloatDir()
    {
        return nextFloat(-1, 1);
    }
    
    /**
     * @return A random uniformly distributed {@code double} [{@code 0} - {@code bound}].
     */
    public double nextDouble(double bound)
    {
        if (bound <= 0) throw new IllegalArgumentException("bound must be positive");
        return nextDouble() * bound;
    }
    
    /**
     * @return A random uniformly distributed {@code double} [{@code origin} - {@code bound}].
     */
    public double nextDouble(double origin, double bound)
    {
        if (origin >= bound) throw new IllegalArgumentException("origin must be less than bound");
        return origin + (bound - origin) * nextDouble();
    }
    
    /**
     * @return A random uniformly distributed {@code double} [{@code -1} - {@code 1}].
     */
    public double nextDoubleDir()
    {
        return nextDouble(-1, 1);
    }
    
    /**
     * Returns the next pseudorandom, Gaussian ("normally") distributed
     * {@code double} value with mean {@code mean} and standard
     * deviation {@code stdDev} from this random number generator's sequence.
     *
     * @param mean   The mean value
     * @param stdDev The standard deviation value
     * @return The next pseudorandom, Gaussian ("normally") distributed
     * {@code double} value with mean {@code mean} and
     * standard deviation {@code stdDev} from this random number
     * generator's sequence
     */
    public double nextGaussian(double mean, double stdDev)
    {
        return mean + stdDev * nextGaussian();
    }
    
    /**
     * @param array The array
     * @return A random {@code int} from the array
     */
    public int nextFrom(int... array)
    {
        return array[nextInt(array.length)];
    }
    
    /**
     * @param array The array
     * @return A random {@code long} from the array
     */
    public long nextFrom(long... array)
    {
        return array[nextInt(array.length)];
    }
    
    /**
     * @param array The array
     * @return A random {@code float} from the array
     */
    public float nextFrom(float... array)
    {
        return array[nextInt(array.length)];
    }
    
    /**
     * @param array The array
     * @return A random {@code double} from the array
     */
    public double nextFrom(double... array)
    {
        return array[nextInt(array.length)];
    }
    
    /**
     * @param array The array
     * @return A random {@code T} from the array
     */
    @SafeVarargs
    public final <T> T nextFrom(T... array)
    {
        return array[nextInt(array.length)];
    }
    
    /**
     * @param collection The collection
     * @return A random {@code T} from the collection
     */
    public <T> T nextFrom(Collection<T> collection)
    {
        int index = nextInt(collection.size());
        for (T value : collection)
        {
            if (index == 0) return value;
            index--;
        }
        return null;
    }
    
    /**
     * @return A random Vector2i with {@code int}'s [{@code Integer.MIN_VALUE} - {@code Integer.MAX_VALUE}].
     */
    public Vector2i nextVector2i()
    {
        return new Vector2i(nextInt(), nextInt());
    }
    
    /**
     * @return A random Vector2i with {@code int}'s [{@code 0} - {@code bound}].
     */
    public Vector2i nextVector2i(int bound)
    {
        return new Vector2i(nextInt(bound), nextInt(bound));
    }
    
    /**
     * @return A random Vector2i with {@code int}'s [{@code origin} - {@code bound}].
     */
    public Vector2i nextVector2i(int origin, int bound)
    {
        return new Vector2i(nextInt(origin, bound), nextInt(origin, bound));
    }
    
    /**
     * @return A random Vector3i with {@code int}'s [{@code Integer.MIN_VALUE} - {@code Integer.MAX_VALUE}].
     */
    public Vector3i nextVector3i()
    {
        return new Vector3i(nextInt(), nextInt(), nextInt());
    }
    
    /**
     * @return A random Vector3i with {@code int}'s [{@code 0} - {@code bound}].
     */
    public Vector3i nextVector3i(int bound)
    {
        return new Vector3i(nextInt(bound), nextInt(bound), nextInt(bound));
    }
    
    /**
     * @return A random Vector3i with {@code int}'s [{@code origin} - {@code bound}].
     */
    public Vector3i nextVector3i(int origin, int bound)
    {
        return new Vector3i(nextInt(origin, bound), nextInt(origin, bound), nextInt(origin, bound));
    }
    
    /**
     * @return A random Vector4i with {@code int}'s [{@code Integer.MIN_VALUE} - {@code Integer.MAX_VALUE}].
     */
    public Vector4i nextVector4i()
    {
        return new Vector4i(nextInt(), nextInt(), nextInt(), nextInt());
    }
    
    /**
     * @return A random Vector4i with {@code int}'s [{@code 0} - {@code bound}].
     */
    public Vector4i nextVector4i(int bound)
    {
        return new Vector4i(nextInt(bound), nextInt(bound), nextInt(bound), nextInt(bound));
    }
    
    /**
     * @return A random Vector4i with {@code int}'s [{@code origin} - {@code bound}].
     */
    public Vector4i nextVector4i(int origin, int bound)
    {
        return new Vector4i(nextInt(origin, bound), nextInt(origin, bound), nextInt(origin, bound), nextInt(origin, bound));
    }
    
    /**
     * @return A random Vector2f with {@code float}'s [{@code 0} - {@code 1}].
     */
    public Vector2f nextVector2f()
    {
        return new Vector2f(nextFloat(), nextFloat());
    }
    
    /**
     * @return A random Vector2f with {@code float}'s [{@code 0} - {@code bound}].
     */
    public Vector2f nextVector2f(float bound)
    {
        return new Vector2f(nextFloat(bound), nextFloat(bound));
    }
    
    /**
     * @return A random Vector2f with {@code float}'s [{@code origin} - {@code bound}].
     */
    public Vector2f nextVector2f(float origin, float bound)
    {
        return new Vector2f(nextFloat(origin, bound), nextFloat(origin, bound));
    }
    
    /**
     * @return A random Vector23 with {@code float}'s [{@code 0} - {@code 1}].
     */
    public Vector3f nextVector3f()
    {
        return new Vector3f(nextFloat(), nextFloat(), nextFloat());
    }
    
    /**
     * @return A random Vector3f with {@code float}'s [{@code 0} - {@code bound}].
     */
    public Vector3f nextVector3f(float bound)
    {
        return new Vector3f(nextFloat(bound), nextFloat(bound), nextFloat(bound));
    }
    
    /**
     * @return A random Vector3f with {@code float}'s [{@code origin} - {@code bound}].
     */
    public Vector3f nextVector3f(float origin, float bound)
    {
        return new Vector3f(nextFloat(origin, bound), nextFloat(origin, bound), nextFloat(origin, bound));
    }
    
    /**
     * @return A random Vector24 with {@code float}'s [{@code 0} - {@code 1}].
     */
    public Vector4f nextVector4f()
    {
        return new Vector4f(nextFloat(), nextFloat(), nextFloat(), nextFloat());
    }
    
    /**
     * @return A random Vector4f with {@code float}'s [{@code 0} - {@code bound}].
     */
    public Vector4f nextVector4f(float bound)
    {
        return new Vector4f(nextFloat(bound), nextFloat(bound), nextFloat(bound), nextFloat(bound));
    }
    
    /**
     * @return A random Vector4f with {@code float}'s [{@code origin} - {@code bound}].
     */
    public Vector4f nextVector4f(float origin, float bound)
    {
        return new Vector4f(nextFloat(origin, bound), nextFloat(origin, bound), nextFloat(origin, bound), nextFloat(origin, bound));
    }
    
    /**
     * @return A random Vector2d with {@code double}'s [{@code 0} - {@code 1}].
     */
    public Vector2d nextVector2d()
    {
        return new Vector2d(nextDouble(), nextDouble());
    }
    
    /**
     * @return A random Vector2d with {@code double}'s [{@code 0} - {@code bound}].
     */
    public Vector2d nextVector2d(float bound)
    {
        return new Vector2d(nextDouble(bound), nextDouble(bound));
    }
    
    /**
     * @return A random Vector2d with {@code double}'s [{@code origin} - {@code bound}].
     */
    public Vector2d nextVector2d(float origin, float bound)
    {
        return new Vector2d(nextDouble(origin, bound), nextDouble(origin, bound));
    }
    
    /**
     * @return A random Vector3d with {@code double}'s [{@code 0} - {@code 1}].
     */
    public Vector3d nextVector3d()
    {
        return new Vector3d(nextDouble(), nextDouble(), nextDouble());
    }
    
    /**
     * @return A random Vector3d with {@code double}'s [{@code 0} - {@code bound}].
     */
    public Vector3d nextVector3d(float bound)
    {
        return new Vector3d(nextDouble(bound), nextDouble(bound), nextDouble(bound));
    }
    
    /**
     * @return A random Vector3d with {@code double}'s [{@code origin} - {@code bound}].
     */
    public Vector3d nextVector3d(float origin, float bound)
    {
        return new Vector3d(nextDouble(origin, bound), nextDouble(origin, bound), nextDouble(origin, bound));
    }
    
    /**
     * @return A random Vector4d with {@code double}'s [{@code 0} - {@code 1}].
     */
    public Vector4d nextVector4d()
    {
        return new Vector4d(nextDouble(), nextDouble(), nextDouble(), nextDouble());
    }
    
    /**
     * @return A random Vector4d with {@code double}'s [{@code 0} - {@code bound}].
     */
    public Vector4d nextVector4d(float bound)
    {
        return new Vector4d(nextDouble(bound), nextDouble(bound), nextDouble(bound), nextDouble(bound));
    }
    
    /**
     * @return A random Vector4d with {@code double}'s [{@code origin} - {@code bound}].
     */
    public Vector4d nextVector4d(float origin, float bound)
    {
        return new Vector4d(nextDouble(origin, bound), nextDouble(origin, bound), nextDouble(origin, bound), nextDouble(origin, bound));
    }
    
    /**
     * @return A random unit Vector2f
     */
    public Vector2f nextUnit2f()
    {
        return new Vector2f(nextFloatDir(), nextFloatDir()).normalize();
    }
    
    /**
     * @return A random unit Vector3f
     */
    public Vector3f nextUnit3f()
    {
        return new Vector3f(nextFloatDir(), nextFloatDir(), nextFloatDir()).normalize();
    }
    
    /**
     * @return A random unit Vector4f
     */
    public Vector4f nextUnit4f()
    {
        return new Vector4f(nextFloatDir(), nextFloatDir(), nextFloatDir(), nextFloatDir()).normalize();
    }
    
    /**
     * @return A random unit Vector2d
     */
    public Vector2d nextUnit2d()
    {
        return new Vector2d(nextDoubleDir(), nextDoubleDir()).normalize();
    }
    
    /**
     * @return A random unit Vector3d
     */
    public Vector3d nextUnit3d()
    {
        return new Vector3d(nextDoubleDir(), nextDoubleDir(), nextDoubleDir()).normalize();
    }
    
    /**
     * @return A random unit Vector4d
     */
    public Vector4d nextUnit4d()
    {
        return new Vector4d(nextDoubleDir(), nextDoubleDir(), nextDoubleDir(), nextDoubleDir()).normalize();
    }
    
    /**
     * Randomizes a {@code Color}'s values.
     *
     * @param lower The lower value in the range
     * @param upper The upper value in the range
     * @param alpha If alpha should be randomized too.
     * @param out   The color instance to set the random values to.
     * @return out
     */
    public Color nextColor(int lower, int upper, boolean alpha, Color out)
    {
        out.r(nextInt(lower, upper));
        out.g(nextInt(lower, upper));
        out.b(nextInt(lower, upper));
        if (alpha) out.a(nextInt(lower, upper));
        return out;
    }
    
    /**
     * Randomizes a {@code Color}'s values, except for its alpha value.
     *
     * @param lower The lower value in the range
     * @param upper The upper value in the range
     * @param out   The color instance to set the random values to.
     * @return out
     */
    public Color nextColor(int lower, int upper, Color out)
    {
        return nextColor(lower, upper, false, out);
    }
    
    /**
     * Creates a new random {@code Color}.
     *
     * @param lower The lower value in the range
     * @param upper The upper value in the range
     * @param alpha If alpha should be randomized too.
     * @return A new random {@code Color} instance.
     */
    public Color nextColor(int lower, int upper, boolean alpha)
    {
        return nextColor(lower, upper, alpha, new Color());
    }
    
    /**
     * Creates a new random {@code Color}.
     *
     * @param lower The lower value in the range
     * @param upper The upper value in the range
     * @return A new random {@code Color} instance.
     */
    public Color nextColor(int lower, int upper)
    {
        return nextColor(lower, upper, false, new Color());
    }
    
    /**
     * Randomizes a {@code Color}'s values, except for its alpha value, from [{@code 0} - {@code upper}.
     *
     * @param upper The upper value in the range
     * @param out   The color instance to set the random values to.
     * @return out
     */
    public Color nextColor(int upper, Color out)
    {
        return nextColor(0, upper, out);
    }
    
    /**
     * Creates a new random {@code Color} from [{@code 0} - {@code upper}.
     *
     * @param upper The upper value in the range
     * @param alpha If alpha should be randomized too.
     * @return A new random {@code Color} instance.
     */
    public Color nextColor(int upper, boolean alpha)
    {
        return nextColor(0, upper, alpha, new Color());
    }
    
    /**
     * Creates a new random {@code Color} from [{@code 0} - {@code upper}.
     *
     * @param upper The upper value in the range
     * @return A new random {@code Color} instance.
     */
    public Color nextColor(int upper)
    {
        return nextColor(0, upper, new Color());
    }
    
    /**
     * Randomizes a {@code Color}'s values, except for its alpha value, from [{@code 0} - {@code 255}.
     *
     * @param out The color instance to set the random values to.
     * @return out
     */
    public Color nextColor(Color out)
    {
        return nextColor(0, 255, out);
    }
    
    /**
     * Creates a new random {@code Color} from [{@code 0} - {@code 255}.
     *
     * @param alpha If alpha should be randomized too.
     * @return A new random {@code Color} instance.
     */
    public Color nextColor(boolean alpha)
    {
        return nextColor(0, 255, alpha, new Color());
    }
    
    /**
     * Creates a new random {@code Color} from [{@code 0} - {@code 255}.
     *
     * @return A new random {@code Color} instance.
     */
    public Color nextColor()
    {
        return nextColor(0, 255, false, new Color());
    }
}
