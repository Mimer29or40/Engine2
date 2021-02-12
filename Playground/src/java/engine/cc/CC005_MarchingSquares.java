package engine.cc;

import engine.Engine;
import org.joml.Vector2d;

import static rutils.NumUtil.map;

public class CC005_MarchingSquares extends Engine
{
    double[][] field;
    
    final int rez = 20;
    int cols, rows;
    
    @Override
    public void setup()
    {
        size(600, 400, 2, 2);
        
        cols = 1 + screenWidth() / rez;
        rows = 1 + screenHeight() / rez;
        
        field = new double[cols][rows];
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        double t = seconds() * map(mouse().x(), 0, screenWidth() - 1, 0, 1);
        
        for (int i = 0; i < cols; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                field[i][j] = noise(i * 0.1, j * 0.1, t);
            }
        }
        
        clear();
        weight(rez * 0.4);
        for (int i = 0; i < cols; i++)
        {
            for (int j = 0; j < rows; j++)
            {
                stroke(field[i][j]);
                point(i * rez, j * rez);
            }
        }
        
        stroke(255);
        weight(1);
        for (int i = 0; i < cols - 1; i++)
        {
            for (int j = 0; j < rows - 1; j++)
            {
                double x = i * rez;
                double y = j * rez;
                
                Vector2d a = new Vector2d(x + rez * 0.5, y);
                Vector2d b = new Vector2d(x + rez, y + rez * 0.5);
                Vector2d c = new Vector2d(x + rez * 0.5, y + rez);
                Vector2d d = new Vector2d(x, y + rez * 0.5);
                
                int state = getState((int) Math.ceil(field[i][j]), (int) Math.ceil(field[i + 1][j]), (int) Math.ceil(field[i + 1][j + 1]), (int) Math.ceil(field[i][j + 1]));
                
                switch (state)
                {
                    case 0:
                    case 15:
                    default:
                        break;
                    case 1:
                    case 14:
                        line(c, d);
                        break;
                    case 2:
                    case 13:
                        line(b, c);
                        break;
                    case 3:
                    case 12:
                        line(b, d);
                        break;
                    case 4:
                    case 11:
                        line(a, b);
                        break;
                    case 5:
                        line(a, d);
                        line(b, c);
                        break;
                    case 6:
                    case 9:
                        line(a, c);
                        break;
                    case 7:
                    case 8:
                        line(a, d);
                        break;
                    case 10:
                        line(a, b);
                        line(c, d);
                        break;
                }
            }
        }
    }
    
    void line(Vector2d v1, Vector2d v2)
    {
        line(v1.x, v1.y, v2.x, v2.y);
    }
    
    int getState(int a, int b, int c, int d)
    {
        return a * 8 + b * 4 + c * 2 + d;
    }
    
    public static void main(String[] args)
    {
        start(new CC005_MarchingSquares());
    }
}
