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
    protected double calculate_impl(int dimension, int frequency, double amplitude, double[] coord)
    {
        int xi, yi, zi, wi;
        int rx0, ry0, rz0, rw0;
        int rx1, ry1, rz1, rw1;
        
        double x, y, z, w;
        double xf, yf, zf, wf;
        double x0, y0, z0, w0;
        double x1, y1, z1, w1;
        
        switch (dimension)
        {
            case 1:
                x = coord[0];
                
                xi = (int) Math.floor(x);
                
                xf = x - xi;
                
                rx0 = xi & Noise.tableSizeMask;
                rx1 = (rx0 + 1) & Noise.tableSizeMask;
                
                x0 = this.r[this.p[rx0]];
                x1 = this.r[this.p[rx1]];
                
                return smoothstep(x0, x1, xf);
            case 2:
                x = coord[0];
                y = coord[1];
                
                xi = (int) Math.floor(x);
                yi = (int) Math.floor(y);
                
                xf = x - xi;
                yf = y - yi;
                
                rx0 = xi & Noise.tableSizeMask;
                rx1 = (rx0 + 1) & Noise.tableSizeMask;
                ry0 = yi & Noise.tableSizeMask;
                ry1 = (ry0 + 1) & Noise.tableSizeMask;
                
                x0 = this.r[this.p[this.p[rx0] + ry0]];
                x1 = this.r[this.p[this.p[rx1] + ry0]];
                y0 = smoothstep(x0, x1, xf);
                
                x0 = this.r[this.p[this.p[rx0] + ry1]];
                x1 = this.r[this.p[this.p[rx1] + ry1]];
                y1 = smoothstep(x0, x1, xf);
                
                return smoothstep(y0, y1, yf);
            case 3:
                x = coord[0];
                y = coord[1];
                z = coord[2];
                
                xi = (int) Math.floor(x);
                yi = (int) Math.floor(y);
                zi = (int) Math.floor(z);
                
                xf = x - xi;
                yf = y - yi;
                zf = z - zi;
                
                rx0 = xi & Noise.tableSizeMask;
                rx1 = (rx0 + 1) & Noise.tableSizeMask;
                ry0 = yi & Noise.tableSizeMask;
                ry1 = (ry0 + 1) & Noise.tableSizeMask;
                rz0 = zi & Noise.tableSizeMask;
                rz1 = (rz0 + 1) & Noise.tableSizeMask;
                
                x0 = this.r[this.p[this.p[this.p[rx0] + ry0] + rz0]];
                x1 = this.r[this.p[this.p[this.p[rx1] + ry0] + rz0]];
                y0 = smoothstep(x0, x1, xf);
                x0 = this.r[this.p[this.p[this.p[rx0] + ry1] + rz0]];
                x1 = this.r[this.p[this.p[this.p[rx1] + ry1] + rz0]];
                y1 = smoothstep(x0, x1, xf);
                z0 = smoothstep(y0, y1, yf);
                
                x0 = this.r[this.p[this.p[this.p[rx0] + ry0] + rz1]];
                x1 = this.r[this.p[this.p[this.p[rx1] + ry0] + rz1]];
                y0 = smoothstep(x0, x1, xf);
                x0 = this.r[this.p[this.p[this.p[rx0] + ry1] + rz1]];
                x1 = this.r[this.p[this.p[this.p[rx1] + ry1] + rz1]];
                y1 = smoothstep(x0, x1, xf);
                z1 = smoothstep(y0, y1, yf);
                
                return smoothstep(z0, z1, zf);
            case 4:
                x = coord[0];
                y = coord[1];
                z = coord[2];
                w = coord[3];
                
                xi = (int) Math.floor(x);
                yi = (int) Math.floor(y);
                zi = (int) Math.floor(z);
                wi = (int) Math.floor(w);
                
                xf = x - xi;
                yf = y - yi;
                zf = z - zi;
                wf = w - wi;
                
                rx0 = xi & Noise.tableSizeMask;
                rx1 = (rx0 + 1) & Noise.tableSizeMask;
                ry0 = yi & Noise.tableSizeMask;
                ry1 = (ry0 + 1) & Noise.tableSizeMask;
                rz0 = zi & Noise.tableSizeMask;
                rz1 = (rz0 + 1) & Noise.tableSizeMask;
                rw0 = wi & Noise.tableSizeMask;
                rw1 = (rw0 + 1) & Noise.tableSizeMask;
                
                x0 = this.r[this.p[this.p[this.p[this.p[rx0] + ry0] + rz0] + rw0]];
                x1 = this.r[this.p[this.p[this.p[this.p[rx1] + ry0] + rz0] + rw0]];
                y0 = smoothstep(x0, x1, xf);
                x0 = this.r[this.p[this.p[this.p[this.p[rx0] + ry1] + rz0] + rw0]];
                x1 = this.r[this.p[this.p[this.p[this.p[rx1] + ry1] + rz0] + rw0]];
                y1 = smoothstep(x0, x1, xf);
                z0 = smoothstep(y0, y1, yf);
                x0 = this.r[this.p[this.p[this.p[this.p[rx0] + ry0] + rz1] + rw0]];
                x1 = this.r[this.p[this.p[this.p[this.p[rx1] + ry0] + rz1] + rw0]];
                y0 = smoothstep(x0, x1, xf);
                x0 = this.r[this.p[this.p[this.p[this.p[rx0] + ry1] + rz1] + rw0]];
                x1 = this.r[this.p[this.p[this.p[this.p[rx1] + ry1] + rz1] + rw0]];
                y1 = smoothstep(x0, x1, xf);
                z1 = smoothstep(y0, y1, yf);
                w0 = smoothstep(z0, z1, zf);
                
                x0 = this.r[this.p[this.p[this.p[this.p[rx0] + ry0] + rz0] + rw1]];
                x1 = this.r[this.p[this.p[this.p[this.p[rx1] + ry0] + rz0] + rw1]];
                y0 = smoothstep(x0, x1, xf);
                x0 = this.r[this.p[this.p[this.p[this.p[rx0] + ry1] + rz0] + rw1]];
                x1 = this.r[this.p[this.p[this.p[this.p[rx1] + ry1] + rz0] + rw1]];
                y1 = smoothstep(x0, x1, xf);
                z0 = smoothstep(y0, y1, yf);
                x0 = this.r[this.p[this.p[this.p[this.p[rx0] + ry0] + rz1] + rw1]];
                x1 = this.r[this.p[this.p[this.p[this.p[rx1] + ry0] + rz1] + rw1]];
                y0 = smoothstep(x0, x1, xf);
                x0 = this.r[this.p[this.p[this.p[this.p[rx0] + ry1] + rz1] + rw1]];
                x1 = this.r[this.p[this.p[this.p[this.p[rx1] + ry1] + rz1] + rw1]];
                y1 = smoothstep(x0, x1, xf);
                z1 = smoothstep(y0, y1, yf);
                w1 = smoothstep(z0, z1, zf);
                
                return smoothstep(w0, w1, wf);
            default:
                int[] vi = new int[dimension];
                double[] vf = new double[dimension];
                
                for (int axis = 0; axis < dimension; axis++)
                {
                    vi[axis] = (int) Math.floor(coord[axis]);
                    vf[axis] = coord[axis] - vi[axis];
                }
                
                double[] nodes = new double[1 << dimension];
                int[] nodeCoord = new int[dimension];
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
}
