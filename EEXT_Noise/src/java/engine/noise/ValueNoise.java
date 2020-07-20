package engine.noise;

import engine.util.Random;

import static engine.util.Util.smoothstep;

@SuppressWarnings("unused")
public class ValueNoise extends Noise
{
    protected double[] r;
    
    public ValueNoise()
    {
        super();
    }
    
    public ValueNoise(long seed)
    {
        super(seed);
    }
    
    @Override
    protected void setup(Random random)
    {
        super.setup(random);
    
        this.r = new double[Noise.tableSize];
    
        random.nextDoubles(this.r, -1.0, 1.0);
    }
    
    @Override
    protected double calculate1D(int frequency, double amplitude, double x)
    {
        int xi = fastFloor(x);
    
        double xf = x - xi;
    
        int rx0 = xi & Noise.tableSizeMask;
        int rx1 = (rx0 + 1) & Noise.tableSizeMask;
    
        double x0 = this.r[this.perm[rx0]];
        double x1 = this.r[this.perm[rx1]];
        
        return smoothstep(x0, x1, xf);
    }
    
    @Override
    protected double calculate2D(int frequency, double amplitude, double x, double y)
    {
        int xi = fastFloor(x);
        int yi = fastFloor(y);
        
        double xf = x - xi;
        double yf = y - yi;
        
        int rx0 = xi & Noise.tableSizeMask;
        int rx1 = (rx0 + 1) & Noise.tableSizeMask;
        int ry0 = yi & Noise.tableSizeMask;
        int ry1 = (ry0 + 1) & Noise.tableSizeMask;
        
        double x0 = this.r[this.perm[this.perm[rx0] + ry0]];
        double x1 = this.r[this.perm[this.perm[rx1] + ry0]];
        double x2 = this.r[this.perm[this.perm[rx0] + ry1]];
        double x3 = this.r[this.perm[this.perm[rx1] + ry1]];
        
        double y0 = smoothstep(x0, x1, xf);
        double y1 = smoothstep(x2, x3, xf);
        
        return smoothstep(y0, y1, yf);
    }
    
    @Override
    protected double calculate3D(int frequency, double amplitude, double x, double y, double z)
    {
        int xi = fastFloor(x);
        int yi = fastFloor(y);
        int zi = fastFloor(z);
        
        double xf = x - xi;
        double yf = y - yi;
        double zf = z - zi;
        
        int rx0 = xi & Noise.tableSizeMask;
        int rx1 = (rx0 + 1) & Noise.tableSizeMask;
        int ry0 = yi & Noise.tableSizeMask;
        int ry1 = (ry0 + 1) & Noise.tableSizeMask;
        int rz0 = zi & Noise.tableSizeMask;
        int rz1 = (rz0 + 1) & Noise.tableSizeMask;
        
        double x0 = this.r[this.perm[this.perm[this.perm[rx0] + ry0] + rz0]];
        double x1 = this.r[this.perm[this.perm[this.perm[rx1] + ry0] + rz0]];
        double x2 = this.r[this.perm[this.perm[this.perm[rx0] + ry1] + rz0]];
        double x3 = this.r[this.perm[this.perm[this.perm[rx1] + ry1] + rz0]];
        double x4 = this.r[this.perm[this.perm[this.perm[rx0] + ry0] + rz1]];
        double x5 = this.r[this.perm[this.perm[this.perm[rx1] + ry0] + rz1]];
        double x6 = this.r[this.perm[this.perm[this.perm[rx0] + ry1] + rz1]];
        double x7 = this.r[this.perm[this.perm[this.perm[rx1] + ry1] + rz1]];
        
        double y0 = smoothstep(x0, x1, xf);
        double y1 = smoothstep(x2, x3, xf);
        double y2 = smoothstep(x4, x5, xf);
        double y3 = smoothstep(x6, x7, xf);
        
        double z0 = smoothstep(y0, y1, yf);
        double z1 = smoothstep(y2, y3, yf);
        
        return smoothstep(z0, z1, zf);
    }
    
    @Override
    protected double calculate4D(int frequency, double amplitude, double x, double y, double z, double w)
    {
        int xi = fastFloor(x);
        int yi = fastFloor(y);
        int zi = fastFloor(z);
        int wi = fastFloor(w);
        
        double xf = x - xi;
        double yf = y - yi;
        double zf = z - zi;
        double wf = w - wi;
        
        int rx0 = xi & Noise.tableSizeMask;
        int rx1 = (rx0 + 1) & Noise.tableSizeMask;
        int ry0 = yi & Noise.tableSizeMask;
        int ry1 = (ry0 + 1) & Noise.tableSizeMask;
        int rz0 = zi & Noise.tableSizeMask;
        int rz1 = (rz0 + 1) & Noise.tableSizeMask;
        int rw0 = wi & Noise.tableSizeMask;
        int rw1 = (rw0 + 1) & Noise.tableSizeMask;
        
        double x0  = this.r[this.perm[this.perm[this.perm[this.perm[rx0] + ry0] + rz0] + rw0]];
        double x1  = this.r[this.perm[this.perm[this.perm[this.perm[rx1] + ry0] + rz0] + rw0]];
        double x2  = this.r[this.perm[this.perm[this.perm[this.perm[rx0] + ry1] + rz0] + rw0]];
        double x3  = this.r[this.perm[this.perm[this.perm[this.perm[rx1] + ry1] + rz0] + rw0]];
        double x4  = this.r[this.perm[this.perm[this.perm[this.perm[rx0] + ry0] + rz1] + rw0]];
        double x5  = this.r[this.perm[this.perm[this.perm[this.perm[rx1] + ry0] + rz1] + rw0]];
        double x6  = this.r[this.perm[this.perm[this.perm[this.perm[rx0] + ry1] + rz1] + rw0]];
        double x7  = this.r[this.perm[this.perm[this.perm[this.perm[rx1] + ry1] + rz1] + rw0]];
        double x8  = this.r[this.perm[this.perm[this.perm[this.perm[rx0] + ry0] + rz0] + rw1]];
        double x9  = this.r[this.perm[this.perm[this.perm[this.perm[rx1] + ry0] + rz0] + rw1]];
        double x10 = this.r[this.perm[this.perm[this.perm[this.perm[rx0] + ry1] + rz0] + rw1]];
        double x11 = this.r[this.perm[this.perm[this.perm[this.perm[rx1] + ry1] + rz0] + rw1]];
        double x12 = this.r[this.perm[this.perm[this.perm[this.perm[rx0] + ry0] + rz1] + rw1]];
        double x13 = this.r[this.perm[this.perm[this.perm[this.perm[rx1] + ry0] + rz1] + rw1]];
        double x14 = this.r[this.perm[this.perm[this.perm[this.perm[rx0] + ry1] + rz1] + rw1]];
        double x15 = this.r[this.perm[this.perm[this.perm[this.perm[rx1] + ry1] + rz1] + rw1]];
        
        double y0 = smoothstep(x0, x1, xf);
        double y1 = smoothstep(x2, x3, xf);
        double y2 = smoothstep(x4, x5, xf);
        double y3 = smoothstep(x6, x7, xf);
        double y4 = smoothstep(x8, x9, xf);
        double y5 = smoothstep(x10, x11, xf);
        double y6 = smoothstep(x12, x13, xf);
        double y7 = smoothstep(x14, x15, xf);
        
        double z0 = smoothstep(y0, y1, yf);
        double z1 = smoothstep(y2, y3, yf);
        double z2 = smoothstep(y4, y5, yf);
        double z3 = smoothstep(y6, y7, yf);
        
        double w0 = smoothstep(z0, z1, zf);
        double w1 = smoothstep(z2, z3, zf);
        
        return smoothstep(w0, w1, wf);
    }
    
    @Override
    protected double calculateND(int dimension, int frequency, double amplitude, double[] coord)
    {
        int[]    vi = new int[dimension];
        double[] vf = new double[dimension];
        
        for (int axis = 0; axis < dimension; axis++)
        {
            vi[axis] = fastFloor(coord[axis]);
            vf[axis] = coord[axis] - vi[axis];
        }
        
        double[] nodes     = new double[1 << dimension];
        int[]    nodeCoord = new int[dimension];
        for (int node = 0, n = nodes.length; node < n; node++)
        {
            for (int axis = 0; axis < dimension; axis++) nodeCoord[axis] = ((vi[axis]) + ((node >> axis) & 1)) & Noise.tableSizeMask;
            nodes[node] = this.r[hash(nodeCoord)];
        }
        
        for (int axis = 0, n = nodes.length; axis < dimension; axis++)
        {
            n >>= 1;
            for (int i = 0, j; i < n; i++)
            {
                j        = i << 1;
                nodes[i] = smoothstep(nodes[j], nodes[j + 1], vf[axis]);
            }
        }
        
        return nodes[0];
    }
}
