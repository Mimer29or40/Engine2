package engine.util.noise;

import java.util.Arrays;
import java.util.function.Function;

import static rutils.NumUtil.fastFloor;

@SuppressWarnings("unused")
public class WorleyNoise extends Noise
{
    private static final double MAX_1D = 2;
    private static final double MAX_2D = 2 * Math.sqrt(2);
    private static final double MAX_3D = 2 * Math.sqrt(4);
    private static final double MAX_4D = 4;
    
    protected double[][] points1;
    protected double[][] points2;
    protected double[][] points3;
    protected double[][] points4;
    
    protected Function<double[], Double> distanceFunction = arr -> arr[0];
    
    /**
     * Sets a property of the Noise Implementation. This should be handles directly by each implementation to set a property without casting the object.
     * If the string does not correspond to a value then it will do nothing.
     *
     * @param property The name of the property.
     * @param object   The new value of the property.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setProperty(String property, Object object)
    {
        if (property.equals("distanceFunction"))
        {
            if (object instanceof Function) this.distanceFunction = (Function<double[], Double>) object;
        }
    }
    
    /**
     * This function creates and generates the permutation tables.
     */
    @Override
    protected void init()
    {
        super.init();
        
        if (this.points1 == null) this.points1 = new double[Noise.TABLE_SIZE][1];
        if (this.points2 == null) this.points2 = new double[Noise.TABLE_SIZE][2];
        if (this.points3 == null) this.points3 = new double[Noise.TABLE_SIZE][3];
        if (this.points4 == null) this.points4 = new double[Noise.TABLE_SIZE][4];
        
        double[] cords = new double[4];
        for (int i = 0; i < Noise.TABLE_SIZE; i++)
        {
            this.random.nextDoubles(cords, 1.0);
            
            this.points1[i][0] = cords[0];
            
            this.points2[i][0] = cords[0];
            this.points2[i][1] = cords[1];
            
            this.points3[i][0] = cords[0];
            this.points3[i][1] = cords[1];
            this.points3[i][2] = cords[2];
            
            this.points4[i][0] = cords[0];
            this.points4[i][1] = cords[1];
            this.points4[i][2] = cords[2];
            this.points4[i][3] = cords[3];
        }
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
    @Override
    public double noise1D(int octave, int frequency, double amplitude, double x)
    {
        int xi = fastFloor(x);
        
        double[] distances = new double[3];
        
        int    gx;
        double px;
        
        int index = 0;
        for (int i = -1; i <= 1; i++)
        {
            gx = (xi + i) & Noise.TABLE_SIZE_MASK;
            
            double[] point = this.points1[this.perm[gx]];
            
            px = x - (xi + i + point[0]);
            
            distances[index++] = px;
        }
        Arrays.sort(distances);
    
        return this.distanceFunction.apply(distances) / WorleyNoise.MAX_1D * 2.0 - 1.0;
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
    @Override
    public double noise2D(int octave, int frequency, double amplitude, double x, double y)
    {
        int xi = fastFloor(x);
        int yi = fastFloor(y);
        
        double[] distances = new double[3 * 3];
        
        int    gx, gy;
        double px, py;
        
        int index = 0;
        for (int j = -1; j <= 1; j++)
        {
            for (int i = -1; i <= 1; i++)
            {
                gx = (xi + i) & Noise.TABLE_SIZE_MASK;
                gy = (yi + j) & Noise.TABLE_SIZE_MASK;
                
                double[] point = this.points2[this.perm[this.perm[gx] + gy]];
                
                px = x - (xi + i + point[0]);
                py = y - (yi + j + point[1]);
                
                distances[index++] = Math.sqrt(px * px + py * py);
            }
        }
        Arrays.sort(distances);
    
        return this.distanceFunction.apply(distances) / WorleyNoise.MAX_2D * 2.0 - 1.0;
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
    @Override
    public double noise3D(int octave, int frequency, double amplitude, double x, double y, double z)
    {
        int xi = fastFloor(x);
        int yi = fastFloor(y);
        int zi = fastFloor(z);
        
        double[] distances = new double[3 * 3 * 3];
        
        int    gx, gy, gz;
        double px, py, pz;
        
        int index = 0;
        for (int k = -1; k <= 1; k++)
        {
            for (int j = -1; j <= 1; j++)
            {
                for (int i = -1; i <= 1; i++)
                {
                    gx = (xi + i) & Noise.TABLE_SIZE_MASK;
                    gy = (yi + j) & Noise.TABLE_SIZE_MASK;
                    gz = (zi + k) & Noise.TABLE_SIZE_MASK;
                    
                    double[] point = this.points3[this.perm[this.perm[this.perm[gx] + gy] + gz]];
                    
                    px = x - (xi + i + point[0]);
                    py = y - (yi + j + point[1]);
                    pz = z - (zi + k + point[2]);
                    
                    distances[index++] = Math.sqrt(px * px + py * py + pz * pz);
                }
            }
        }
        Arrays.sort(distances);
    
        return this.distanceFunction.apply(distances) / WorleyNoise.MAX_3D * 2.0 - 1.0;
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
    @Override
    public double noise4D(int octave, int frequency, double amplitude, double x, double y, double z, double w)
    {
        int xi = fastFloor(x);
        int yi = fastFloor(y);
        int zi = fastFloor(z);
        int wi = fastFloor(w);
        
        double[] distances = new double[3 * 3 * 3 * 3];
        
        int    gx, gy, gz, gw;
        double px, py, pz, pw;
        
        int index = 0;
        for (int l = -1; l <= 1; l++)
        {
            for (int k = -1; k <= 1; k++)
            {
                for (int j = -1; j <= 1; j++)
                {
                    for (int i = -1; i <= 1; i++)
                    {
                        gx = (xi + i) & Noise.TABLE_SIZE_MASK;
                        gy = (yi + j) & Noise.TABLE_SIZE_MASK;
                        gz = (zi + k) & Noise.TABLE_SIZE_MASK;
                        gw = (wi + l) & Noise.TABLE_SIZE_MASK;
                        
                        double[] point = this.points4[this.perm[this.perm[this.perm[this.perm[gx] + gy] + gz] + gw]];
                        
                        px = x - (xi + i + point[0]);
                        py = y - (yi + j + point[1]);
                        pz = z - (zi + k + point[2]);
                        pw = w - (wi + l + point[3]);
                        
                        distances[index++] = Math.sqrt(px * px + py * py + pz * pz + pw * pw);
                    }
                }
            }
        }
        Arrays.sort(distances);
    
        return this.distanceFunction.apply(distances) / WorleyNoise.MAX_4D * 2.0 - 1.0;
    }
}
