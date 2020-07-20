package engine.noise;

import engine.util.Random;

import static engine.util.Util.clamp;

@SuppressWarnings("unused")
public abstract class Noise
{
    protected static final int tableSize     = 1 << 8;
    protected static final int tableSizeMask = Noise.tableSize - 1;
    
    protected short[] perm;
    
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
        this.perm = new short[Noise.tableSize << 1];
        
        for (short i = 0; i < Noise.tableSize; i++) this.perm[i] = i;
        
        for (int i = 0; i < Noise.tableSize; i++)
        {
            int index = random.nextInt() & Noise.tableSizeMask;
            
            short swap = this.perm[i];
            this.perm[i]     = this.perm[index];
            this.perm[index] = swap;
            
            this.perm[i + Noise.tableSize] = this.perm[i];
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
            value += calculate(dimension, frequency, amplitude, coord) * amplitude;
            for (int j = 0; j < dimension; j++) coord[j] = coord[j] * 2 + coord[j];
            maxValue += amplitude;
            frequency <<= 1;
            amplitude *= this.persistence;
        }
        
        return clamp(value / maxValue, -1.0, 1.0);
    }
    
    protected double calculate(int dimension, int frequency, double amplitude, double[] coord)
    {
        switch (dimension)
        {
            case 1:
                return calculate1D(frequency, amplitude, coord[0]);
            case 2:
                return calculate2D(frequency, amplitude, coord[0], coord[1]);
            case 3:
                return calculate3D(frequency, amplitude, coord[0], coord[1], coord[2]);
            case 4:
                return calculate4D(frequency, amplitude, coord[0], coord[1], coord[2], coord[3]);
            default:
                return calculateND(dimension, frequency, amplitude, coord);
        }
    }
    
    protected double calculate1D(int frequency, double amplitude, double x)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    protected double calculate2D(int frequency, double amplitude, double x, double y)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    protected double calculate3D(int frequency, double amplitude, double x, double y, double z)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    protected double calculate4D(int frequency, double amplitude, double x, double y, double z, double w)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    protected double calculateND(int dimension, int frequency, double amplitude, double[] coord)
    {
        throw new RuntimeException("Not Implemented");
    }
    
    protected int hash(int... coord)
    {
        int result = 0;
        for (int v : coord) result = this.perm[result + v];
        return result;
    }
    
    protected static int fastFloor(double x)
    {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }
}
