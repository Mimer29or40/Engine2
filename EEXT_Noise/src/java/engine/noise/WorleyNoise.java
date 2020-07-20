package engine.noise;

import engine.util.Random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static engine.util.Util.fastFloor;

@SuppressWarnings("unused")
public class WorleyNoise extends Noise
{
    private static final double STD_DEV_1D = 0.1;
    private static final double STD_DEV_2D = 0.5;
    private static final double STD_DEV_3D = 0.5;
    private static final double STD_DEV_4D = 0.5;
    
    private static final int MAX_POINTS_1D = 3;
    private static final int MAX_POINTS_2D = 5;
    private static final int MAX_POINTS_3D = 5;
    private static final int MAX_POINTS_4D = 5;
    
    protected final HashMap<Short, ArrayList<double[]>> points1D = new HashMap<>();
    protected final HashMap<Short, ArrayList<double[]>> points2D = new HashMap<>();
    protected final HashMap<Short, ArrayList<double[]>> points3D = new HashMap<>();
    protected final HashMap<Short, ArrayList<double[]>> points4D = new HashMap<>();
    
    protected Random random;
    
    public WorleyNoise()
    {
        super();
    }
    
    public WorleyNoise(long seed)
    {
        super(seed);
    }
    
    @Override
    protected void setup(Random random)
    {
        super.setup(random);
        
        this.random = random;
    }
    
    private ArrayList<double[]> getPoints(int x)
    {
        short hash = this.perm[x];
        if (!this.points1D.containsKey(hash))
        {
            int count = Math.min((int) Math.abs(this.random.nextGaussian(0, WorleyNoise.STD_DEV_1D)) + 1, WorleyNoise.MAX_POINTS_1D);
            
            ArrayList<double[]> newPoints = new ArrayList<>();
            for (int i = 0; i < count; i++) newPoints.add(this.random.nextDoubles(new double[1]));
            this.points1D.put(hash, newPoints);
        }
        
        ArrayList<double[]> points = new ArrayList<>();
        for (double[] point : this.points1D.get(hash)) points.add(new double[] {x + point[0]});
        
        return points;
    }
    
    private ArrayList<double[]> getPoints(int x, int y)
    {
        short hash = this.perm[this.perm[x] + y];
        if (!this.points2D.containsKey(hash))
        {
            int count = Math.min((int) Math.abs(this.random.nextGaussian(0, WorleyNoise.STD_DEV_2D)) + 1, WorleyNoise.MAX_POINTS_2D);
            
            ArrayList<double[]> newPoints = new ArrayList<>();
            for (int i = 0; i < count; i++) newPoints.add(this.random.nextDoubles(new double[2]));
            this.points2D.put(hash, newPoints);
        }
        
        ArrayList<double[]> points = new ArrayList<>();
        for (double[] point : this.points2D.get(hash)) points.add(new double[] {x + point[0], y + point[1]});
        
        return points;
    }
    
    private ArrayList<double[]> getPoints(int x, int y, int z)
    {
        short hash = this.perm[this.perm[this.perm[x] + y] + z];
        if (!this.points3D.containsKey(hash))
        {
            int count = Math.min((int) Math.abs(this.random.nextGaussian(0, WorleyNoise.STD_DEV_3D)) + 1, WorleyNoise.MAX_POINTS_3D);
            
            ArrayList<double[]> newPoints = new ArrayList<>();
            for (int i = 0; i < count; i++) newPoints.add(this.random.nextDoubles(new double[3]));
            this.points3D.put(hash, newPoints);
        }
        
        ArrayList<double[]> points = new ArrayList<>();
        for (double[] point : this.points3D.get(hash)) points.add(new double[] {x + point[0], y + point[1], z + point[2]});
        
        return points;
    }
    
    private ArrayList<double[]> getPoints(int x, int y, int z, int w)
    {
        short hash = this.perm[this.perm[this.perm[this.perm[x] + y] + z] + w];
        if (!this.points4D.containsKey(hash))
        {
            int count = Math.min((int) Math.abs(this.random.nextGaussian(0, WorleyNoise.STD_DEV_4D)) + 1, WorleyNoise.MAX_POINTS_4D);
            
            ArrayList<double[]> newPoints = new ArrayList<>();
            for (int i = 0; i < count; i++) newPoints.add(this.random.nextDoubles(new double[4]));
            this.points4D.put(hash, newPoints);
        }
        
        ArrayList<double[]> points = new ArrayList<>();
        for (double[] point : this.points4D.get(hash)) points.add(new double[] {x + point[0], y + point[1], z + point[2], w + point[3]});
        
        return points;
    }
    
    @Override
    protected double noise1D(int frequency, double amplitude, double x)
    {
        int xi = fastFloor(x) - 1;
        
        ArrayList<double[]> points = new ArrayList<>();
        for (int i = 0; i < 3; i++)
        {
            points.addAll(getPoints((xi + i) & Noise.tableSizeMask));
        }
        List<Double> distances = points.stream().mapToDouble(o -> x - o[0]).sorted().boxed().collect(Collectors.toList());
        
        return distanceFunction(distances);
    }
    
    @Override
    protected double noise2D(int frequency, double amplitude, double x, double y)
    {
        int xi = fastFloor(x) - 1;
        int yi = fastFloor(y) - 1;
        
        ArrayList<double[]> points = new ArrayList<>();
        for (int j = 0; j < 3; j++)
        {
            for (int i = 0; i < 3; i++)
            {
                points.addAll(getPoints((xi + i) & Noise.tableSizeMask, (yi + j) & Noise.tableSizeMask));
            }
        }
        List<Double> distances = points.stream().mapToDouble(o -> Math.sqrt((x - o[0]) * (x - o[0]) + (y - o[1]) * (y - o[1]))).sorted().boxed().collect(Collectors.toList());
        
        return distanceFunction(distances);
    }
    
    @Override
    protected double noise3D(int frequency, double amplitude, double x, double y, double z)
    {
        int xi = fastFloor(x) - 1;
        int yi = fastFloor(y) - 1;
        int zi = fastFloor(z) - 1;
        
        ArrayList<double[]> points = new ArrayList<>();
        for (int k = 0; k < 3; k++)
        {
            for (int j = 0; j < 3; j++)
            {
                for (int i = 0; i < 3; i++)
                {
                    points.addAll(getPoints((xi + i) & Noise.tableSizeMask, (yi + j) & Noise.tableSizeMask, (zi + k) & Noise.tableSizeMask));
                }
            }
        }
        List<Double> distances = points.stream().mapToDouble(o -> Math.sqrt((x - o[0]) * (x - o[0]) + (y - o[1]) * (y - o[1]) + (z - o[2]) * (z - o[2]))).sorted().boxed().collect(Collectors.toList());
        
        return distanceFunction(distances);
    }
    
    @Override
    protected double noise4D(int frequency, double amplitude, double x, double y, double z, double w)
    {
        int xi = fastFloor(x) - 1;
        int yi = fastFloor(y) - 1;
        int zi = fastFloor(z) - 1;
        int wi = fastFloor(w) - 1;
        
        ArrayList<double[]> points = new ArrayList<>();
        for (int l = 0; l < 3; l++)
        {
            for (int k = 0; k < 3; k++)
            {
                for (int j = 0; j < 3; j++)
                {
                    for (int i = 0; i < 3; i++)
                    {
                        points.addAll(getPoints((xi + i) & Noise.tableSizeMask, (yi + j) & Noise.tableSizeMask, (zi + k) & Noise.tableSizeMask, (wi + l) & Noise.tableSizeMask));
                    }
                }
            }
        }
        List<Double> distances = points.stream().mapToDouble(o -> Math.sqrt((x - o[0]) * (x - o[0]) + (y - o[1]) * (y - o[1]) + (z - o[2]) * (z - o[2]) + (w - o[3]) * (w - o[3])))
                                       .sorted().boxed().collect(Collectors.toList());
        
        return distanceFunction(distances);
    }
    
    protected double distanceFunction(List<Double> distances)
    {
        return distances.get(0);
    }
}
