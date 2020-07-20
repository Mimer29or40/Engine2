package engine.noise;

import engine.util.Random;

@SuppressWarnings("unused")
public class SimplexNoise extends Noise
{
    // Skewing and unskewing factors for 2, 3, and 4 dimensions
    private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
    private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
    private static final double F3 = 1.0 / 3.0;
    private static final double G3 = 1.0 / 6.0;
    private static final double F4 = (Math.sqrt(5.0) - 1.0) / 4.0;
    private static final double G4 = (5.0 - Math.sqrt(5.0)) / 20.0;
    
    protected short[] permMod12;
    
    public SimplexNoise()
    {
        super();
    }
    
    public SimplexNoise(long seed)
    {
        super(seed);
    }
    
    @Override
    protected void setup(Random random)
    {
        super.setup(random);
        
        int n = this.perm.length;
        
        this.permMod12 = new short[n];
        
        for (int i = 0; i < n; i++)
        {
            this.permMod12[i] = (short) (this.perm[i] % 12);
        }
    }
    
    @Override
    protected double calculate1D(int frequency, double amplitude, double x)
    {
        return calculate2D(frequency, amplitude, x, 0.0);
    }
    
    @Override
    protected double calculate2D(int frequency, double amplitude, double x, double y)
    {
        double n0, n1, n2; // Noise contributions from the three corners
        // Skew the input space to determine which simplex cell we're in
        double s  = (x + y) * SimplexNoise.F2; // Hairy factor for 2D
        int    i  = fastFloor(x + s);
        int    j  = fastFloor(y + s);
        double t  = (i + j) * SimplexNoise.G2;
        double X0 = i - t; // Unskew the cell origin back to (x,y) space
        double Y0 = j - t;
        double x0 = x - X0; // The x,y distances from the cell origin
        double y0 = y - Y0;
        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.
        int i1, j1; // Offsets for second (middle) corner of simplex in (i,j) coords
        if (x0 > y0) // lower triangle, XY order: (0,0)->(1,0)->(1,1)
        {
            i1 = 1;
            j1 = 0;
        }
        else // upper triangle, YX order: (0,0)->(0,1)->(1,1)
        {
            i1 = 0;
            j1 = 1;
        }
        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6
        double x1 = x0 - i1 + SimplexNoise.G2; // Offsets for middle corner in (x,y) unskewed coords
        double y1 = y0 - j1 + SimplexNoise.G2;
        double x2 = x0 - 1.0 + 2.0 * SimplexNoise.G2; // Offsets for last corner in (x,y) unskewed coords
        double y2 = y0 - 1.0 + 2.0 * SimplexNoise.G2;
        // Work out the hashed gradient indices of the three simplex corners
        int ii  = i & Noise.tableSizeMask;
        int jj  = j & Noise.tableSizeMask;
        int gi0 = this.permMod12[ii + this.perm[jj]];
        int gi1 = this.permMod12[ii + i1 + this.perm[jj + j1]];
        int gi2 = this.permMod12[ii + 1 + this.perm[jj + 1]];
        // Calculate the contribution from the three corners
        double t0 = 0.5 - x0 * x0 - y0 * y0;
        if (t0 < 0)
        {
            n0 = 0.0;
        }
        else
        {
            t0 *= t0;
            n0 = t0 * t0 * (x0 * SimplexNoise.GRAD3[gi0][0] + y0 * SimplexNoise.GRAD3[gi0][1]);  // (x,y) of grad3 used for 2D gradient
        }
        double t1 = 0.5 - x1 * x1 - y1 * y1;
        if (t1 < 0)
        {
            n1 = 0.0;
        }
        else
        {
            t1 *= t1;
            n1 = t1 * t1 * (x1 * SimplexNoise.GRAD3[gi1][0] + y1 * SimplexNoise.GRAD3[gi1][1]);
        }
        double t2 = 0.5 - x2 * x2 - y2 * y2;
        if (t2 < 0)
        {
            n2 = 0.0;
        }
        else
        {
            t2 *= t2;
            n2 = t2 * t2 * (x2 * SimplexNoise.GRAD3[gi2][0] + y2 * SimplexNoise.GRAD3[gi2][1]);
        }
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to return values in the interval [-1,1].
        return 70.0 * (n0 + n1 + n2);
    }
    
    @Override
    protected double calculate3D(int frequency, double amplitude, double x, double y, double z)
    {
        double n0, n1, n2, n3; // Noise contributions from the four corners
        // Skew the input space to determine which simplex cell we're in
        double s  = (x + y + z) * SimplexNoise.F3; // Very nice and simple skew factor for 3D
        int    i  = fastFloor(x + s);
        int    j  = fastFloor(y + s);
        int    k  = fastFloor(z + s);
        double t  = (i + j + k) * SimplexNoise.G3;
        double X0 = i - t; // Unskew the cell origin back to (x,y,z) space
        double Y0 = j - t;
        double Z0 = k - t;
        double x0 = x - X0; // The x,y,z distances from the cell origin
        double y0 = y - Y0;
        double z0 = z - Z0;
        // For the 3D case, the simplex shape is a slightly irregular tetrahedron.
        // Determine which simplex we are in.
        int i1, j1, k1; // Offsets for second corner of simplex in (i,j,k) coords
        int i2, j2, k2; // Offsets for third corner of simplex in (i,j,k) coords
        if (x0 >= y0)
        {
            if (y0 >= z0) // X Y Z order
            {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
            else if (x0 >= z0) // X Z Y order
            {
                i1 = 1;
                j1 = 0;
                k1 = 0;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
            else // Z X Y order
            {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 1;
                j2 = 0;
                k2 = 1;
            }
        }
        else // x0<y0
        {
            if (y0 < z0) // Z Y X order
            {
                i1 = 0;
                j1 = 0;
                k1 = 1;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            }
            else if (x0 < z0) // Y Z X order
            {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 0;
                j2 = 1;
                k2 = 1;
            }
            else // Y X Z order
            {
                i1 = 0;
                j1 = 1;
                k1 = 0;
                i2 = 1;
                j2 = 1;
                k2 = 0;
            }
        }
        // A step of (1,0,0) in (i,j,k) means a step of (1-c,-c,-c) in (x,y,z),
        // a step of (0,1,0) in (i,j,k) means a step of (-c,1-c,-c) in (x,y,z), and
        // a step of (0,0,1) in (i,j,k) means a step of (-c,-c,1-c) in (x,y,z), where
        // c = 1/6.
        double x1 = x0 - i1 + SimplexNoise.G3; // Offsets for second corner in (x,y,z) coords
        double y1 = y0 - j1 + SimplexNoise.G3;
        double z1 = z0 - k1 + SimplexNoise.G3;
        double x2 = x0 - i2 + 2.0 * SimplexNoise.G3; // Offsets for third corner in (x,y,z) coords
        double y2 = y0 - j2 + 2.0 * SimplexNoise.G3;
        double z2 = z0 - k2 + 2.0 * SimplexNoise.G3;
        double x3 = x0 - 1.0 + 3.0 * SimplexNoise.G3; // Offsets for last corner in (x,y,z) coords
        double y3 = y0 - 1.0 + 3.0 * SimplexNoise.G3;
        double z3 = z0 - 1.0 + 3.0 * SimplexNoise.G3;
        // Work out the hashed gradient indices of the four simplex corners
        int ii  = i & Noise.tableSizeMask;
        int jj  = j & Noise.tableSizeMask;
        int kk  = k & Noise.tableSizeMask;
        int gi0 = this.permMod12[ii + this.perm[jj + this.perm[kk]]];
        int gi1 = this.permMod12[ii + i1 + this.perm[jj + j1 + this.perm[kk + k1]]];
        int gi2 = this.permMod12[ii + i2 + this.perm[jj + j2 + this.perm[kk + k2]]];
        int gi3 = this.permMod12[ii + 1 + this.perm[jj + 1 + this.perm[kk + 1]]];
        // Calculate the contribution from the four corners
        double t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
        if (t0 < 0)
        {
            n0 = 0.0;
        }
        else
        {
            t0 *= t0;
            n0 = t0 * t0 * (x0 * SimplexNoise.GRAD3[gi0][0] + y0 * SimplexNoise.GRAD3[gi0][1] + z0 * SimplexNoise.GRAD3[gi0][2]);
        }
        double t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
        if (t1 < 0)
        {
            n1 = 0.0;
        }
        else
        {
            t1 *= t1;
            n1 = t1 * t1 * (x1 * SimplexNoise.GRAD3[gi1][0] + y1 * SimplexNoise.GRAD3[gi1][1] + z1 * SimplexNoise.GRAD3[gi1][2]);
        }
        double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
        if (t2 < 0)
        {
            n2 = 0.0;
        }
        else
        {
            t2 *= t2;
            n2 = t2 * t2 * (x2 * SimplexNoise.GRAD3[gi2][0] + y2 * SimplexNoise.GRAD3[gi2][1] + z2 * SimplexNoise.GRAD3[gi2][2]);
        }
        double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
        if (t3 < 0)
        {
            n3 = 0.0;
        }
        else
        {
            t3 *= t3;
            n3 = t3 * t3 * (x3 * SimplexNoise.GRAD3[gi3][0] + y3 * SimplexNoise.GRAD3[gi3][1] + z3 * SimplexNoise.GRAD3[gi3][2]);
        }
        // Add contributions from each corner to get the final noise value.
        // The result is scaled to stay just inside [-1,1]
        return 32.0 * (n0 + n1 + n2 + n3);
    }
    
    @Override
    protected double calculate4D(int frequency, double amplitude, double x, double y, double z, double w)
    {
        double n0, n1, n2, n3, n4; // Noise contributions from the five corners
        // Skew the (x,y,z,w) space to determine which cell of 24 simplices we're in
        double s  = (x + y + z + w) * SimplexNoise.F4; // Factor for 4D skewing
        int    i  = fastFloor(x + s);
        int    j  = fastFloor(y + s);
        int    k  = fastFloor(z + s);
        int    l  = fastFloor(w + s);
        double t  = (i + j + k + l) * SimplexNoise.G4; // Factor for 4D unskewing
        double X0 = i - t; // Unskew the cell origin back to (x,y,z,w) space
        double Y0 = j - t;
        double Z0 = k - t;
        double W0 = l - t;
        double x0 = x - X0;  // The x,y,z,w distances from the cell origin
        double y0 = y - Y0;
        double z0 = z - Z0;
        double w0 = w - W0;
        // For the 4D case, the simplex is a 4D shape I won't even try to describe.
        // To find out which of the 24 possible simplices we're in, we need to
        // determine the magnitude ordering of x0, y0, z0 and w0.
        // Six pair-wise comparisons are performed between each possible pair
        // of the four coordinates, and the results are used to rank the numbers.
        int rankx = 0;
        int ranky = 0;
        int rankz = 0;
        int rankw = 0;
        if (x0 > y0)
        {
            rankx++;
        }
        else
        {
            ranky++;
        }
        if (x0 > z0)
        {
            rankx++;
        }
        else
        {
            rankz++;
        }
        if (x0 > w0)
        {
            rankx++;
        }
        else
        {
            rankw++;
        }
        if (y0 > z0)
        {
            ranky++;
        }
        else
        {
            rankz++;
        }
        if (y0 > w0)
        {
            ranky++;
        }
        else
        {
            rankw++;
        }
        if (z0 > w0)
        {
            rankz++;
        }
        else
        {
            rankw++;
        }
        int i1, j1, k1, l1; // The integer offsets for the second simplex corner
        int i2, j2, k2, l2; // The integer offsets for the third simplex corner
        int i3, j3, k3, l3; // The integer offsets for the fourth simplex corner
        // [rankx, ranky, rankz, rankw] is a 4-vector with the numbers 0, 1, 2 and 3
        // in some order. We use a thresholding to set the coordinates in turn.
        // Rank 3 denotes the largest coordinate.
        i1 = rankx >= 3 ? 1 : 0;
        j1 = ranky >= 3 ? 1 : 0;
        k1 = rankz >= 3 ? 1 : 0;
        l1 = rankw >= 3 ? 1 : 0;
        // Rank 2 denotes the second largest coordinate.
        i2 = rankx >= 2 ? 1 : 0;
        j2 = ranky >= 2 ? 1 : 0;
        k2 = rankz >= 2 ? 1 : 0;
        l2 = rankw >= 2 ? 1 : 0;
        // Rank 1 denotes the second smallest coordinate.
        i3 = rankx >= 1 ? 1 : 0;
        j3 = ranky >= 1 ? 1 : 0;
        k3 = rankz >= 1 ? 1 : 0;
        l3 = rankw >= 1 ? 1 : 0;
        // The fifth corner has all coordinate offsets = 1, so no need to compute that.
        double x1 = x0 - i1 + SimplexNoise.G4; // Offsets for second corner in (x,y,z,w) coords
        double y1 = y0 - j1 + SimplexNoise.G4;
        double z1 = z0 - k1 + SimplexNoise.G4;
        double w1 = w0 - l1 + SimplexNoise.G4;
        double x2 = x0 - i2 + 2.0 * SimplexNoise.G4; // Offsets for third corner in (x,y,z,w) coords
        double y2 = y0 - j2 + 2.0 * SimplexNoise.G4;
        double z2 = z0 - k2 + 2.0 * SimplexNoise.G4;
        double w2 = w0 - l2 + 2.0 * SimplexNoise.G4;
        double x3 = x0 - i3 + 3.0 * SimplexNoise.G4; // Offsets for fourth corner in (x,y,z,w) coords
        double y3 = y0 - j3 + 3.0 * SimplexNoise.G4;
        double z3 = z0 - k3 + 3.0 * SimplexNoise.G4;
        double w3 = w0 - l3 + 3.0 * SimplexNoise.G4;
        double x4 = x0 - 1.0 + 4.0 * SimplexNoise.G4; // Offsets for last corner in (x,y,z,w) coords
        double y4 = y0 - 1.0 + 4.0 * SimplexNoise.G4;
        double z4 = z0 - 1.0 + 4.0 * SimplexNoise.G4;
        double w4 = w0 - 1.0 + 4.0 * SimplexNoise.G4;
        // Work out the hashed gradient indices of the five simplex corners
        int ii  = i & Noise.tableSizeMask;
        int jj  = j & Noise.tableSizeMask;
        int kk  = k & Noise.tableSizeMask;
        int ll  = l & Noise.tableSizeMask;
        int gi0 = this.perm[ii + this.perm[jj + this.perm[kk + this.perm[ll]]]] % 32;
        int gi1 = this.perm[ii + i1 + this.perm[jj + j1 + this.perm[kk + k1 + this.perm[ll + l1]]]] % 32;
        int gi2 = this.perm[ii + i2 + this.perm[jj + j2 + this.perm[kk + k2 + this.perm[ll + l2]]]] % 32;
        int gi3 = this.perm[ii + i3 + this.perm[jj + j3 + this.perm[kk + k3 + this.perm[ll + l3]]]] % 32;
        int gi4 = this.perm[ii + 1 + this.perm[jj + 1 + this.perm[kk + 1 + this.perm[ll + 1]]]] % 32;
        // Calculate the contribution from the five corners
        double t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0 - w0 * w0;
        if (t0 < 0)
        {
            n0 = 0.0;
        }
        else
        {
            t0 *= t0;
            n0 = t0 * t0 * (x0 * SimplexNoise.GRAD4[gi0][0] + y0 * SimplexNoise.GRAD4[gi0][1] + z0 * SimplexNoise.GRAD4[gi0][2] + w0 * SimplexNoise.GRAD4[gi0][3]);
        }
        double t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1 - w1 * w1;
        if (t1 < 0)
        {
            n1 = 0.0;
        }
        else
        {
            t1 *= t1;
            n1 = t1 * t1 * (x1 * SimplexNoise.GRAD4[gi1][0] + y1 * SimplexNoise.GRAD4[gi1][1] + z1 * SimplexNoise.GRAD4[gi1][2] + w1 * SimplexNoise.GRAD4[gi1][3]);
        }
        double t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2 - w2 * w2;
        if (t2 < 0)
        {
            n2 = 0.0;
        }
        else
        {
            t2 *= t2;
            n2 = t2 * t2 * (x2 * SimplexNoise.GRAD4[gi2][0] + y2 * SimplexNoise.GRAD4[gi2][1] + z2 * SimplexNoise.GRAD4[gi2][2] + w2 * SimplexNoise.GRAD4[gi2][3]);
        }
        double t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3 - w3 * w3;
        if (t3 < 0)
        {
            n3 = 0.0;
        }
        else
        {
            t3 *= t3;
            n3 = t3 * t3 * (x3 * SimplexNoise.GRAD4[gi3][0] + y3 * SimplexNoise.GRAD4[gi3][1] + z3 * SimplexNoise.GRAD4[gi3][2] + w3 * SimplexNoise.GRAD4[gi3][3]);
        }
        double t4 = 0.6 - x4 * x4 - y4 * y4 - z4 * z4 - w4 * w4;
        if (t4 < 0)
        {
            n4 = 0.0;
        }
        else
        {
            t4 *= t4;
            n4 = t4 * t4 * (x4 * SimplexNoise.GRAD4[gi4][0] + y4 * SimplexNoise.GRAD4[gi4][1] + z4 * SimplexNoise.GRAD4[gi4][2] + w4 * SimplexNoise.GRAD4[gi4][3]);
        }
        // Sum up and scale the result to cover the range [-1,1]
        return 27.0 * (n0 + n1 + n2 + n3 + n4);
    }
    
    private static final double[][] GRAD3 = {
            {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0},
            {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1},
            {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
    };
    
    private static final double[][] GRAD4 = {
            {0, 1, 1, 1}, {0, 1, 1, -1}, {0, 1, -1, 1}, {0, 1, -1, -1},
            {0, -1, 1, 1}, {0, -1, 1, -1}, {0, -1, -1, 1}, {0, -1, -1, -1},
            {1, 0, 1, 1}, {1, 0, 1, -1}, {1, 0, -1, 1}, {1, 0, -1, -1},
            {-1, 0, 1, 1}, {-1, 0, 1, -1}, {-1, 0, -1, 1}, {-1, 0, -1, -1},
            {1, 1, 0, 1}, {1, 1, 0, -1}, {1, -1, 0, 1}, {1, -1, 0, -1},
            {-1, 1, 0, 1}, {-1, 1, 0, -1}, {-1, -1, 0, 1}, {-1, -1, 0, -1},
            {1, 1, 1, 0}, {1, 1, -1, 0}, {1, -1, 1, 0}, {1, -1, -1, 0},
            {-1, 1, 1, 0}, {-1, 1, -1, 0}, {-1, -1, 1, 0}, {-1, -1, -1, 0}
    };
    
}
