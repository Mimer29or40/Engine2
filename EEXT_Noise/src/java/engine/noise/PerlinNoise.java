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
    protected double calculate1D(int frequency, double amplitude, double[] coord)
    {
        double x = coord[0];
        
        int xi0 = fastFloor(x) & Noise.tableSizeMask;
        
        int xi1 = (xi0 + 1) & Noise.tableSizeMask;
        
        double dx0 = x - xi0;
        
        double dx1 = dx0 - 1.0;
        
        double[] g0 = this.grad1[this.perm[xi0]];
        double[] g1 = this.grad1[this.perm[xi1]];
        
        double x0 = g0[0] * dx0;
        double x1 = g1[0] * dx1;
        
        return smoothstep(x0, x1, dx0);
    }
    
    @Override
    protected double calculate2D(int frequency, double amplitude, double[] coord)
    {
        double x = coord[0];
        double y = coord[1];
        
        int xi0 = fastFloor(x) & Noise.tableSizeMask;
        int yi0 = fastFloor(y) & Noise.tableSizeMask;
        
        int xi1 = (xi0 + 1) & Noise.tableSizeMask;
        int yi1 = (yi0 + 1) & Noise.tableSizeMask;
        
        double dx0 = x - xi0;
        double dy0 = y - yi0;
        
        double dx1 = dx0 - 1.0;
        double dy1 = dy0 - 1.0;
        
        double[] g0 = this.grad2[this.perm[this.perm[xi0] + yi0]];
        double[] g1 = this.grad2[this.perm[this.perm[xi1] + yi0]];
        double[] g2 = this.grad2[this.perm[this.perm[xi0] + yi1]];
        double[] g3 = this.grad2[this.perm[this.perm[xi1] + yi1]];
        
        double x0 = g0[0] * dx0 + g0[1] * dy0;
        double x1 = g1[0] * dx1 + g1[1] * dy0;
        double x2 = g2[0] * dx0 + g2[1] * dy1;
        double x3 = g3[0] * dx1 + g3[1] * dy1;
        
        double y0 = smoothstep(x0, x1, dx0);
        double y1 = smoothstep(x2, x3, dx0);
        
        return smoothstep(y0, y1, dy0);
    }
    
    @Override
    protected double calculate3D(int frequency, double amplitude, double[] coord)
    {
        double x = coord[0];
        double y = coord[1];
        double z = coord[2];
        
        int xi0 = fastFloor(x) & Noise.tableSizeMask;
        int yi0 = fastFloor(y) & Noise.tableSizeMask;
        int zi0 = fastFloor(z) & Noise.tableSizeMask;
        
        int xi1 = (xi0 + 1) & Noise.tableSizeMask;
        int yi1 = (yi0 + 1) & Noise.tableSizeMask;
        int zi1 = (zi0 + 1) & Noise.tableSizeMask;
        
        double dx0 = x - xi0;
        double dy0 = y - yi0;
        double dz0 = z - zi0;
        
        double dx1 = dx0 - 1.0;
        double dy1 = dy0 - 1.0;
        double dz1 = dz0 - 1.0;
        
        double[] g0 = this.grad3[this.perm[this.perm[this.perm[xi0] + yi0] + zi0]];
        double[] g1 = this.grad3[this.perm[this.perm[this.perm[xi1] + yi0] + zi0]];
        double[] g2 = this.grad3[this.perm[this.perm[this.perm[xi0] + yi1] + zi0]];
        double[] g3 = this.grad3[this.perm[this.perm[this.perm[xi1] + yi1] + zi0]];
        double[] g4 = this.grad3[this.perm[this.perm[this.perm[xi0] + yi0] + zi1]];
        double[] g5 = this.grad3[this.perm[this.perm[this.perm[xi1] + yi0] + zi1]];
        double[] g6 = this.grad3[this.perm[this.perm[this.perm[xi0] + yi1] + zi1]];
        double[] g7 = this.grad3[this.perm[this.perm[this.perm[xi1] + yi1] + zi1]];
        
        double x0 = g0[0] * dx0 + g0[1] * dy0 + g0[2] * dz0;
        double x1 = g1[0] * dx1 + g1[1] * dy0 + g1[2] * dz0;
        double x2 = g2[0] * dx0 + g2[1] * dy1 + g2[2] * dz0;
        double x3 = g3[0] * dx1 + g3[1] * dy1 + g3[2] * dz0;
        double x4 = g4[0] * dx0 + g4[1] * dy0 + g4[2] * dz1;
        double x5 = g5[0] * dx1 + g5[1] * dy0 + g5[2] * dz1;
        double x6 = g6[0] * dx0 + g6[1] * dy1 + g6[2] * dz1;
        double x7 = g7[0] * dx1 + g7[1] * dy1 + g7[2] * dz1;
        
        double y0 = smoothstep(x0, x1, dx0);
        double y1 = smoothstep(x2, x3, dx0);
        double y2 = smoothstep(x4, x5, dx0);
        double y3 = smoothstep(x6, x7, dx0);
        
        double z0 = smoothstep(y0, y1, dy0);
        double z1 = smoothstep(y2, y3, dy0);
        
        return smoothstep(z0, z1, dz0);
    }
    
    @Override
    protected double calculate4D(int frequency, double amplitude, double[] coord)
    {
        double x = coord[0];
        double y = coord[1];
        double z = coord[2];
        double w = coord[3];
        
        int xi0 = fastFloor(x) & Noise.tableSizeMask;
        int yi0 = fastFloor(y) & Noise.tableSizeMask;
        int zi0 = fastFloor(z) & Noise.tableSizeMask;
        int wi0 = fastFloor(w) & Noise.tableSizeMask;
        
        int xi1 = (xi0 + 1) & Noise.tableSizeMask;
        int yi1 = (yi0 + 1) & Noise.tableSizeMask;
        int zi1 = (zi0 + 1) & Noise.tableSizeMask;
        int wi1 = (wi0 + 1) & Noise.tableSizeMask;
        
        double dx0 = x - xi0;
        double dy0 = y - yi0;
        double dz0 = z - zi0;
        double dw0 = w - wi0;
        
        double dx1 = dx0 - 1.0;
        double dy1 = dy0 - 1.0;
        double dz1 = dz0 - 1.0;
        double dw1 = dw0 - 1.0;
        
        double[] g0  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi0] + yi0] + zi0] + wi0]];
        double[] g1  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi1] + yi0] + zi0] + wi0]];
        double[] g2  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi0] + yi1] + zi0] + wi0]];
        double[] g3  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi1] + yi1] + zi0] + wi0]];
        double[] g4  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi0] + yi0] + zi1] + wi0]];
        double[] g5  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi1] + yi0] + zi1] + wi0]];
        double[] g6  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi0] + yi1] + zi1] + wi0]];
        double[] g7  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi1] + yi1] + zi1] + wi0]];
        double[] g8  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi0] + yi0] + zi0] + wi1]];
        double[] g9  = this.grad4[this.perm[this.perm[this.perm[this.perm[xi1] + yi0] + zi0] + wi1]];
        double[] g10 = this.grad4[this.perm[this.perm[this.perm[this.perm[xi0] + yi1] + zi0] + wi1]];
        double[] g11 = this.grad4[this.perm[this.perm[this.perm[this.perm[xi1] + yi1] + zi0] + wi1]];
        double[] g12 = this.grad4[this.perm[this.perm[this.perm[this.perm[xi0] + yi0] + zi1] + wi1]];
        double[] g13 = this.grad4[this.perm[this.perm[this.perm[this.perm[xi1] + yi0] + zi1] + wi1]];
        double[] g14 = this.grad4[this.perm[this.perm[this.perm[this.perm[xi0] + yi1] + zi1] + wi1]];
        double[] g15 = this.grad4[this.perm[this.perm[this.perm[this.perm[xi1] + yi1] + zi1] + wi1]];
        
        double x0  = g0[0] * dx0 + g0[1] * dy0 + g0[2] * dz0 + g0[3] * dw0;
        double x1  = g1[0] * dx1 + g1[1] * dy0 + g1[2] * dz0 + g1[3] * dw0;
        double x2  = g2[0] * dx0 + g2[1] * dy1 + g2[2] * dz0 + g2[3] * dw0;
        double x3  = g3[0] * dx1 + g3[1] * dy1 + g3[2] * dz0 + g3[3] * dw0;
        double x4  = g4[0] * dx0 + g4[1] * dy0 + g4[2] * dz1 + g4[3] * dw0;
        double x5  = g5[0] * dx1 + g5[1] * dy0 + g5[2] * dz1 + g5[3] * dw0;
        double x6  = g6[0] * dx0 + g6[1] * dy1 + g6[2] * dz1 + g6[3] * dw0;
        double x7  = g7[0] * dx1 + g7[1] * dy1 + g7[2] * dz1 + g7[3] * dw0;
        double x8  = g8[0] * dx0 + g8[1] * dy0 + g8[2] * dz0 + g8[3] * dw1;
        double x9  = g9[0] * dx1 + g9[1] * dy0 + g9[2] * dz0 + g9[3] * dw1;
        double x10 = g10[0] * dx0 + g10[1] * dy1 + g10[2] * dz0 + g10[3] * dw1;
        double x11 = g11[0] * dx1 + g11[1] * dy1 + g11[2] * dz0 + g11[3] * dw1;
        double x12 = g12[0] * dx0 + g12[1] * dy0 + g12[2] * dz1 + g12[3] * dw1;
        double x13 = g13[0] * dx1 + g13[1] * dy0 + g13[2] * dz1 + g13[3] * dw1;
        double x14 = g14[0] * dx0 + g14[1] * dy1 + g14[2] * dz1 + g14[3] * dw1;
        double x15 = g15[0] * dx1 + g15[1] * dy1 + g15[2] * dz1 + g15[3] * dw1;
        
        double y0 = smoothstep(x0, x1, dx0);
        double y1 = smoothstep(x2, x3, dx0);
        double y2 = smoothstep(x4, x5, dx0);
        double y3 = smoothstep(x6, x7, dx0);
        double y4 = smoothstep(x8, x9, dx0);
        double y5 = smoothstep(x10, x11, dx0);
        double y6 = smoothstep(x12, x13, dx0);
        double y7 = smoothstep(x14, x15, dx0);
        
        double z0 = smoothstep(y0, y1, dy0);
        double z1 = smoothstep(y2, y3, dy0);
        double z2 = smoothstep(y4, y5, dy0);
        double z3 = smoothstep(y6, y7, dy0);
        
        double w0 = smoothstep(z0, z1, dz0);
        double w1 = smoothstep(z2, z3, dz0);
        
        return smoothstep(w0, w1, dw0);
    }
}
