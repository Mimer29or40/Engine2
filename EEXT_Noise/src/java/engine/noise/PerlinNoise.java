package engine.noise;

import engine.util.Random;

import static engine.util.Util.smoothstep;

@SuppressWarnings("unused")
public class PerlinNoise extends Noise
{
    protected double[][] grad1;
    protected double[][] grad2;
    protected double[][] grad3;
    protected double[][] grad4;
    
    public PerlinNoise()
    {
        super();
    }
    
    public PerlinNoise(long seed)
    {
        super(seed);
    }
    
    @Override
    protected void setup(Random random)
    {
        super.setup(random);
        
        this.grad1 = new double[Noise.tableSize][1];
        this.grad2 = new double[Noise.tableSize][2];
        this.grad3 = new double[Noise.tableSize][3];
        this.grad4 = new double[Noise.tableSize][4];
        
        double[] cords = new double[4];
        double   x, y, z, w, len;
        for (int i = 0; i < Noise.tableSize; i++)
        {
            random.nextDoubles(cords, -1.0, 1.0);
            
            x = cords[0];
            
            this.grad1[i][0] = x;
            
            x   = cords[0];
            y   = cords[1];
            len = Math.sqrt(x * x + y * y);
            
            this.grad2[i][0] = x / len;
            this.grad2[i][1] = y / len;
            
            x   = cords[0];
            y   = cords[1];
            z   = cords[2];
            len = Math.sqrt(x * x + y * y + z * z);
            
            this.grad3[i][0] = x / len;
            this.grad3[i][1] = y / len;
            this.grad3[i][2] = z / len;
            
            x   = cords[0];
            y   = cords[1];
            z   = cords[2];
            w   = cords[3];
            len = Math.sqrt(x * x + y * y + z * z + w * w);
            
            this.grad4[i][0] = x / len;
            this.grad4[i][1] = y / len;
            this.grad4[i][2] = z / len;
            this.grad4[i][3] = w / len;
        }
    }
    
    @Override
    protected double calculate_impl(int dimension, int frequency, double amplitude, double[] coord)
    {
        int xi0, yi0, zi0, wi0;
        int xi1, yi1, zi1, wi1;
        
        double x, y, z, w;
        double dx0, dy0, dz0, dw0;
        double dx1, dy1, dz1, dw1;
        double x0, y0, z0, w0;
        double x1, y1, z1, w1;
        
        double[] g0, g1;
        switch (dimension)
        {
            case 1:
                x = coord[0];
                
                dx0 = x - Math.floor(x);
                
                dx1 = dx0 - 1.0;
                
                xi0 = (int) Math.floor(x) & Noise.tableSizeMask;
                
                xi1 = (xi0 + 1) & Noise.tableSizeMask;
                
                g0 = this.grad1[this.p[xi0]];
                g1 = this.grad1[this.p[xi1]];
                x0 = g0[0] * dx0;
                x1 = g1[0] * dx1;
                
                return smoothstep(x0, x1, dx0);
            case 2:
                x = coord[0];
                y = coord[1];
                
                dx0 = x - Math.floor(x);
                dy0 = y - Math.floor(y);
                
                dx1 = dx0 - 1.0;
                dy1 = dy0 - 1.0;
                
                xi0 = (int) Math.floor(x) & Noise.tableSizeMask;
                yi0 = (int) Math.floor(y) & Noise.tableSizeMask;
                
                xi1 = (xi0 + 1) & Noise.tableSizeMask;
                yi1 = (yi0 + 1) & Noise.tableSizeMask;
                
                g0 = this.grad2[this.p[this.p[xi0] + yi0]];
                g1 = this.grad2[this.p[this.p[xi1] + yi0]];
                x0 = g0[0] * dx0 + g0[1] * dy0;
                x1 = g1[0] * dx1 + g1[1] * dy0;
                y0 = smoothstep(x0, x1, dx0);
                
                g0 = this.grad2[this.p[this.p[xi0] + yi1]];
                g1 = this.grad2[this.p[this.p[xi1] + yi1]];
                x0 = g0[0] * dx0 + g0[1] * dy1;
                x1 = g1[0] * dx1 + g1[1] * dy1;
                y1 = smoothstep(x0, x1, dx0);
                
                return smoothstep(y0, y1, dy0);
            case 3:
                x = coord[0];
                y = coord[1];
                z = coord[2];
                
                xi0 = (int) Math.floor(x) & Noise.tableSizeMask;
                yi0 = (int) Math.floor(y) & Noise.tableSizeMask;
                zi0 = (int) Math.floor(z) & Noise.tableSizeMask;
                
                xi1 = (xi0 + 1) & Noise.tableSizeMask;
                yi1 = (yi0 + 1) & Noise.tableSizeMask;
                zi1 = (zi0 + 1) & Noise.tableSizeMask;
                
                dx0 = x - xi0;
                dy0 = y - yi0;
                dz0 = z - zi0;
                
                dx1 = dx0 - 1.0;
                dy1 = dy0 - 1.0;
                dz1 = dz0 - 1.0;
                
                g0 = this.grad3[this.p[this.p[this.p[xi0] + yi0] + zi0]];
                g1 = this.grad3[this.p[this.p[this.p[xi1] + yi0] + zi0]];
                x0 = g0[0] * dx0 + g0[1] * dy0 + g0[2] * dz0;
                x1 = g1[0] * dx1 + g1[1] * dy0 + g1[2] * dz0;
                y0 = smoothstep(x0, x1, dx0);
                g0 = this.grad3[this.p[this.p[this.p[xi0] + yi1] + zi0]];
                g1 = this.grad3[this.p[this.p[this.p[xi1] + yi1] + zi0]];
                x0 = g0[0] * dx0 + g0[1] * dy1 + g0[2] * dz0;
                x1 = g1[0] * dx1 + g1[1] * dy1 + g1[2] * dz0;
                y1 = smoothstep(x0, x1, dx0);
                z0 = smoothstep(y0, y1, dy0);
                
                g0 = this.grad3[this.p[this.p[this.p[xi0] + yi0] + zi1]];
                g1 = this.grad3[this.p[this.p[this.p[xi1] + yi0] + zi1]];
                x0 = g0[0] * dx0 + g0[1] * dy0 + g0[2] * dz1;
                x1 = g1[0] * dx1 + g1[1] * dy0 + g1[2] * dz1;
                y0 = smoothstep(x0, x1, dx0);
                g0 = this.grad3[this.p[this.p[this.p[xi0] + yi1] + zi1]];
                g1 = this.grad3[this.p[this.p[this.p[xi1] + yi1] + zi1]];
                x0 = g0[0] * dx0 + g0[1] * dy1 + g0[2] * dz1;
                x1 = g1[0] * dx1 + g1[1] * dy1 + g1[2] * dz1;
                y1 = smoothstep(x0, x1, dx0);
                z1 = smoothstep(y0, y1, dy0);
                
                return smoothstep(z0, z1, dz0);
            case 4:
                x = coord[0];
                y = coord[1];
                z = coord[2];
                w = coord[3];
                
                xi0 = (int) Math.floor(x) & Noise.tableSizeMask;
                yi0 = (int) Math.floor(y) & Noise.tableSizeMask;
                zi0 = (int) Math.floor(z) & Noise.tableSizeMask;
                wi0 = (int) Math.floor(w) & Noise.tableSizeMask;
                
                xi1 = (xi0 + 1) & Noise.tableSizeMask;
                yi1 = (yi0 + 1) & Noise.tableSizeMask;
                zi1 = (zi0 + 1) & Noise.tableSizeMask;
                wi1 = (wi0 + 1) & Noise.tableSizeMask;
                
                dx0 = x - xi0;
                dy0 = y - yi0;
                dz0 = z - zi0;
                dw0 = w - wi0;
                
                dx1 = dx0 - 1.0;
                dy1 = dy0 - 1.0;
                dz1 = dz0 - 1.0;
                dw1 = dw0 - 1.0;
                
                g0 = this.grad3[this.p[this.p[this.p[this.p[xi0] + yi0] + zi0] + wi0]];
                g1 = this.grad3[this.p[this.p[this.p[this.p[xi1] + yi0] + zi0] + wi0]];
                x0 = g0[0] * dx0 + g0[1] * dy0 + g0[2] * dz0 + g0[3] * dw0;
                x1 = g1[0] * dx1 + g1[1] * dy0 + g1[2] * dz0 + g1[3] * dw0;
                y0 = smoothstep(x0, x1, dx0);
                g0 = this.grad3[this.p[this.p[this.p[this.p[xi0] + yi1] + zi0] + wi0]];
                g1 = this.grad3[this.p[this.p[this.p[this.p[xi1] + yi1] + zi0] + wi0]];
                x0 = g0[0] * dx0 + g0[1] * dy1 + g0[2] * dz0 + g0[3] * dw0;
                x1 = g1[0] * dx1 + g1[1] * dy1 + g1[2] * dz0 + g1[3] * dw0;
                y1 = smoothstep(x0, x1, dx0);
                z0 = smoothstep(y0, y1, dy0);
                g0 = this.grad3[this.p[this.p[this.p[this.p[xi0] + yi0] + zi1] + wi0]];
                g1 = this.grad3[this.p[this.p[this.p[this.p[xi1] + yi0] + zi1] + wi0]];
                x0 = g0[0] * dx0 + g0[1] * dy0 + g0[2] * dz1 + g0[3] * dw0;
                x1 = g1[0] * dx1 + g1[1] * dy0 + g1[2] * dz1 + g1[3] * dw0;
                y0 = smoothstep(x0, x1, dx0);
                g0 = this.grad3[this.p[this.p[this.p[this.p[xi0] + yi1] + zi1] + wi0]];
                g1 = this.grad3[this.p[this.p[this.p[this.p[xi1] + yi1] + zi1] + wi0]];
                x0 = g0[0] * dx0 + g0[1] * dy1 + g0[2] * dz1 + g0[3] * dw0;
                x1 = g1[0] * dx1 + g1[1] * dy1 + g1[2] * dz1 + g1[3] * dw0;
                y1 = smoothstep(x0, x1, dx0);
                z1 = smoothstep(y0, y1, dy0);
                w0 = smoothstep(z0, z1, dz0);
                
                
                g0 = this.grad3[this.p[this.p[this.p[this.p[xi0] + yi0] + zi0] + wi1]];
                g1 = this.grad3[this.p[this.p[this.p[this.p[xi1] + yi0] + zi0] + wi1]];
                x0 = g0[0] * dx0 + g0[1] * dy0 + g0[2] * dz0 + g0[3] * dw1;
                x1 = g1[0] * dx1 + g1[1] * dy0 + g1[2] * dz0 + g1[3] * dw1;
                y0 = smoothstep(x0, x1, dx0);
                g0 = this.grad3[this.p[this.p[this.p[this.p[xi0] + yi1] + zi0] + wi1]];
                g1 = this.grad3[this.p[this.p[this.p[this.p[xi1] + yi1] + zi0] + wi1]];
                x0 = g0[0] * dx0 + g0[1] * dy1 + g0[2] * dz0 + g0[3] * dw1;
                x1 = g1[0] * dx1 + g1[1] * dy1 + g1[2] * dz0 + g1[3] * dw1;
                y1 = smoothstep(x0, x1, dx0);
                z0 = smoothstep(y0, y1, dy0);
                g0 = this.grad3[this.p[this.p[this.p[this.p[xi0] + yi0] + zi1] + wi1]];
                g1 = this.grad3[this.p[this.p[this.p[this.p[xi1] + yi0] + zi1] + wi1]];
                x0 = g0[0] * dx0 + g0[1] * dy0 + g0[2] * dz1 + g0[3] * dw1;
                x1 = g1[0] * dx1 + g1[1] * dy0 + g1[2] * dz1 + g1[3] * dw1;
                y0 = smoothstep(x0, x1, dx0);
                g0 = this.grad3[this.p[this.p[this.p[this.p[xi0] + yi1] + zi1] + wi1]];
                g1 = this.grad3[this.p[this.p[this.p[this.p[xi1] + yi1] + zi1] + wi1]];
                x0 = g0[0] * dx0 + g0[1] * dy1 + g0[2] * dz1 + g0[3] * dw1;
                x1 = g1[0] * dx1 + g1[1] * dy1 + g1[2] * dz1 + g1[3] * dw1;
                y1 = smoothstep(x0, x1, dx0);
                z1 = smoothstep(y0, y1, dy0);
                w1 = smoothstep(z0, z1, dz0);
                
                return smoothstep(w0, w1, dw0);
            default:
                throw new RuntimeException("Not Implemented for Dimension: " + dimension);
        }
    }
}
