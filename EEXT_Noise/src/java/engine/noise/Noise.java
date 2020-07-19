package engine.noise;

import engine.util.Random;

import static engine.util.Util.clamp;

public abstract class Noise
{
    protected static final int tableSize     = 512;
    protected static final int tableSizeMask = Noise.tableSize - 1;
    
    protected final int[] p = new int[Noise.tableSize << 1];
    
    protected int    octaves     = 1;
    protected double persistence = 0.5;
    
    public Noise()
    {
        setup(new Random());
    }
    
    public Noise(long seed)
    {
        setup(new Random(seed));
    }
    
    public int octaves()
    {
        return this.octaves;
    }
    
    public void octaves(int octaves)
    {
        this.octaves = octaves;
    }
    
    public double persistence()
    {
        return this.persistence;
    }
    
    public void persistence(double persistence)
    {
        this.persistence = persistence;
    }
    
    protected void setup(Random random)
    {
        for (int i = 0; i < Noise.tableSize; i++) this.p[i] = i;
        
        for (int i = 0; i < Noise.tableSize; i++)
        {
            int index = random.nextInt() & Noise.tableSizeMask;
            
            int swap = this.p[i];
            this.p[i]     = this.p[index];
            this.p[index] = swap;
            
            this.p[i + Noise.tableSize] = this.p[i];
        }
    }
    
    public double calculate(double... coord)
    {
        int    dimension = coord.length;
        double value     = 0.0;
        double maxValue  = 0.0;
        int    frequency = 1;
        double amplitude = 1.0;
        
        for (int i = 0; i < this.octaves; i++)
        {
            value += calculate_impl(dimension, frequency, amplitude, coord) * amplitude;
            for (int j = 0; j < dimension; j++) coord[j] = coord[j] * 2 + coord[j];
            maxValue += amplitude;
            frequency <<= 1;
            amplitude *= this.persistence;
        }
        
        return clamp(value / maxValue, -1.0, 1.0);
    }
    
    protected int hash(int... coord)
    {
        int result = 0;
        for (int v : coord)
        {
            result = this.p[result + v];
        }
        return result;
    }
    
    protected abstract double calculate_impl(int dimension, int frequency, double amplitude, double[] coord);
}
