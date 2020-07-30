package engine.util.noise;

import engine.util.Random;

import static engine.util.Util.clamp;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Noise
{
    public static final String VALUE        = "value";
    public static final String PERLIN       = "perlin";
    public static final String SIMPLEX      = "simplex";
    public static final String OPEN_SIMPLEX = "open-simplex";
    public static final String WORLEY       = "worley";
    
    protected static final int TABLE_SIZE      = 1 << 8;
    protected static final int TABLE_SIZE_MASK = Noise.TABLE_SIZE - 1;
    
    protected final Random random = new Random();
    
    protected short[] perm;
    
    protected int    octaves     = 1;
    protected double persistence = 0.5;
    
    protected boolean initialized = false;
    
    /**
     * Sets the seed of the random instance. Will cause the re-rolling of the permutation tables.
     *
     * @param seed The new seed.
     */
    public void setSeed(long seed)
    {
        this.random.setSeed(seed);
        this.initialized = false;
    }
    
    /**
     * Gets the number of iterations to calculate the noise. Each additional iteration will be
     * {@code Noise.persistence} the amplitude of the previous one.
     *
     * @return The number of octaves.
     */
    public int octaves()
    {
        return this.octaves;
    }
    
    /**
     * Gets the number of iterations to calculate the noise. Each additional iteration will be
     * {@code Noise.persistence} the amplitude of the previous one.
     *
     * @param octaves The amount of octaves.
     */
    public void octaves(int octaves)
    {
        this.octaves = Math.max(1, octaves);
    }
    
    /**
     * Gets the amount to scale succeeding octave amplitudes.
     *
     * @return The persistence.
     */
    public double persistence()
    {
        return this.persistence;
    }
    
    /**
     * Sets the amount to scale succeeding octave amplitudes.
     *
     * @param persistence The persistence.
     */
    public void persistence(double persistence)
    {
        this.persistence = persistence;
    }
    
    /**
     * Sets a property of the Noise Implementation. This should be handles directly by each implementation to set a property without casting the object.
     * If the string does not correspond to a value then it will do nothing.
     *
     * @param property The name of the property.
     * @param object   The new value of the property.
     */
    public void setProperty(String property, Object object)
    {
    
    }
    
    /**
     * This function creates and generates the permutation tables.
     */
    protected void init()
    {
        if (this.perm == null) this.perm = new short[Noise.TABLE_SIZE << 1];
        
        for (short i = 0; i < Noise.TABLE_SIZE; i++) this.perm[i] = i;
        
        for (int i = 0; i < Noise.TABLE_SIZE; i++)
        {
            int index = this.random.nextInt() & Noise.TABLE_SIZE_MASK;
            
            short swap = this.perm[i];
            this.perm[i]     = this.perm[index];
            this.perm[index] = swap;
            
            this.perm[i + Noise.TABLE_SIZE] = this.perm[i];
        }
        
        this.initialized = true;
    }
    
    /**
     * Calculates the noise value at a coordinate. Can handle coordinates of any dimension if its supported by the implementation.
     * <p>
     * This function will handle the scaling and shifting the coordinates between each octave
     *
     * @param coord The coordinate
     * @return The noise value
     */
    public double noise(double... coord)
    {
        int dimension = coord.length;
        
        double value     = 0.0;
        double maxValue  = 0.0;
        int    frequency = 1;
        double amplitude = 1.0;
        
        double[] transformedCoord = new double[dimension];
        
        if (!this.initialized) init();
        for (int i = 0; i < this.octaves; i++)
        {
            for (int j = 0; j < dimension; j++) transformedCoord[j] = coord[j] * frequency + coord[j];
            
            value += noise(i, frequency, amplitude, transformedCoord) * amplitude;
            maxValue += amplitude;
            frequency <<= 1;
            amplitude *= this.persistence;
        }
        
        return clamp(value / maxValue, -1.0, 1.0);
    }
    
    /**
     * Determines the correct dimensional noise function for the data and calls it.
     *
     * @param octave    The current octave
     * @param frequency The frequency of the octave level.
     * @param amplitude The amplitude of the octave level.
     * @param coord     The scaled coordinate.
     * @return The noise value.
     */
    private double noise(int octave, int frequency, double amplitude, double[] coord)
    {
        return switch (coord.length)
                {
                    case 1 -> noise1D(octave, frequency, amplitude, coord[0]);
                    case 2 -> noise2D(octave, frequency, amplitude, coord[0], coord[1]);
                    case 3 -> noise3D(octave, frequency, amplitude, coord[0], coord[1], coord[2]);
                    case 4 -> noise4D(octave, frequency, amplitude, coord[0], coord[1], coord[2], coord[3]);
                    default -> noiseND(octave, frequency, amplitude, coord);
                };
    }
    
    /**
     * Calculates the 1D noise value
     *
     * @param octave    The current octave
     * @param frequency The frequency of the octave level.
     * @param amplitude The amplitude of the octave level.
     * @param x         The scaled x coordinate.
     * @return The noise value.
     */
    protected double noise1D(int octave, int frequency, double amplitude, double x)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    /**
     * Calculates the 2D noise value
     *
     * @param octave    The current octave
     * @param frequency The frequency of the octave level.
     * @param amplitude The amplitude of the octave level.
     * @param x         The scaled x coordinate.
     * @param y         The scaled y coordinate.
     * @return The noise value.
     */
    protected double noise2D(int octave, int frequency, double amplitude, double x, double y)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    /**
     * Calculates the 3D noise value
     *
     * @param octave    The current octave
     * @param frequency The frequency of the octave level.
     * @param amplitude The amplitude of the octave level.
     * @param x         The scaled x coordinate.
     * @param y         The scaled y coordinate.
     * @param z         The scaled z coordinate.
     * @return The noise value.
     */
    protected double noise3D(int octave, int frequency, double amplitude, double x, double y, double z)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    /**
     * Calculates the 4D noise value
     *
     * @param octave    The current octave
     * @param frequency The frequency of the octave level.
     * @param amplitude The amplitude of the octave level.
     * @param x         The scaled x coordinate.
     * @param y         The scaled y coordinate.
     * @param z         The scaled z coordinate.
     * @param w         The scaled w coordinate.
     * @return The noise value.
     */
    protected double noise4D(int octave, int frequency, double amplitude, double x, double y, double z, double w)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    /**
     * Calculates the N-Dimensional noise value
     *
     * @param octave    The current octave
     * @param frequency The frequency of the octave level.
     * @param amplitude The amplitude of the octave level.
     * @param coord     The scaled coordinate.
     * @return The noise value.
     */
    protected double noiseND(int octave, int frequency, double amplitude, double[] coord)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    /**
     * Gets the permutation table value for a given coordinate.
     *
     * @param coord The integer coordinate.
     * @return The permutation table value.
     */
    protected int hash(int... coord)
    {
        int result = 0;
        for (int v : coord) result = this.perm[result + v & Noise.TABLE_SIZE_MASK];
        return result;
    }
}
