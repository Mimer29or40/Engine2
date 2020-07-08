package engine.VoxelSpace;

import engine.Engine;
import engine.color.Color;
import engine.color.Colorc;
import engine.render.Texture;
import org.joml.Vector2dc;

import java.util.ArrayList;

public class VoxelSpace extends Engine
{
    static class Camera
    {
        double x        = 512.; // x position on the map
        double y        = 800.; // y position on the map
        double height   = 78.;  // height of the camera
        double angle    = 0.;   // direction of the camera
        double horizon  = 100.; // horizon position (look up and down)
        int    distance = 800;  // distance of map
    }
    
    void loadMap(String fileNames)
    {
        String[] files = fileNames.split(";");
        this.colorMap  = Texture.loadImage("maps/" + files[0] + ".png");
        this.heightMap = Texture.loadImage("maps/" + files[1] + ".png");
        
        notification("Loaded Map: " + fileNames);
    }
    
    Camera camera = new Camera();
    
    Color background = new Color("#FFE09090");
    
    ArrayList<String> maps        = new ArrayList<>();
    int               selectedMap = 0;
    Texture           colorMap, heightMap;
    
    boolean sampleHeight = false;
    boolean sampleColor  = false;
    
    @Override
    public void setup()
    {
        size(400, 400, 2, 2);
        
        this.maps.add("C1W;D1");
        this.maps.add("C2W;D2");
        this.maps.add("C3;D3");
        this.maps.add("C4;D4");
        this.maps.add("C5W;D5");
        this.maps.add("C6W;D6");
        this.maps.add("C7W;D7");
        this.maps.add("C8;D6");
        this.maps.add("C9W;D9");
        this.maps.add("C10W;D10");
        this.maps.add("C11W;D11");
        this.maps.add("C12W;D11");
        this.maps.add("C13;D13");
        this.maps.add("C14;D14");
        this.maps.add("C14W;D14");
        this.maps.add("C15;D15");
        this.maps.add("C16W;D16");
        this.maps.add("C17W;D17");
        this.maps.add("C18W;D18");
        this.maps.add("C19W;D19");
        this.maps.add("C20W;D20");
        this.maps.add("C21;D21");
        this.maps.add("C22W;D22");
        this.maps.add("C23W;D21");
        this.maps.add("C24W;D24");
        this.maps.add("C25W;D25");
        this.maps.add("C26W;D18");
        this.maps.add("C27W;D15");
        this.maps.add("C28W;D25");
        this.maps.add("C29W;D16");
        
        loadMap(this.maps.get(this.selectedMap));
        
        // mouse().captured(true);
    }
    
    @Override
    public void draw(double elapsedTime)
    {
        // DEBUG
        if (keyboard().F1.down())
        {
            sampleHeight = !sampleHeight;
            notification("Sample Height: " + (sampleHeight ? "On" : "Off"));
        }
        if (keyboard().F2.down())
        {
            sampleColor = !sampleColor;
            notification("Sample Color: " + (sampleColor ? "On" : "Off"));
        }
        
        if (keyboard().ESCAPE.down()) mouse().toggleCaptured();
        
        // Change Map
        if (keyboard().PAGE_UP.down())
        {
            selectedMap++;
            if (selectedMap >= maps.size()) selectedMap = 0;
            loadMap(maps.get(selectedMap));
        }
        if (keyboard().PAGE_DN.down())
        {
            selectedMap--;
            if (selectedMap < 0) selectedMap = maps.size() - 1;
            loadMap(maps.get(selectedMap));
        }
        
        double cameraSpeed         = 50;
        double cameraRotationSpeed = 3;
        double sensitivity = 4;
        
        int mapWidthPeriod  = colorMap.width() - 1;
        int mapHeightPeriod = colorMap.height() - 1;
        
        // Movement
        if (keyboard().L_CTRL.held()) cameraSpeed *= 5;
        if (mouse().captured())
        {
            Vector2dc rel = mouse().rel();
            if (rel.x() != 0)
            {
                camera.angle -= rel.x() * 0.1 * sensitivity * elapsedTime;
            }
            if (rel.y() != 0)
            {
                camera.horizon -= rel.y() * 20 * sensitivity * elapsedTime;
            }
            if (keyboard().LEFT.held() || keyboard().A.held())
            {
                camera.x -= cameraSpeed * Math.cos(camera.angle) * elapsedTime;
                camera.y += cameraSpeed * Math.sin(camera.angle) * elapsedTime;
            }
            if (keyboard().RIGHT.held() || keyboard().D.held())
            {
                camera.x += cameraSpeed * Math.cos(camera.angle) * elapsedTime;
                camera.y -= cameraSpeed * Math.sin(camera.angle) * elapsedTime;
            }
        }
        else
        {
            if (keyboard().LEFT.held() || keyboard().A.held())
            {
                camera.angle += cameraRotationSpeed * elapsedTime;
            }
            if (keyboard().RIGHT.held() || keyboard().D.held())
            {
                camera.angle -= cameraRotationSpeed * elapsedTime;
            }
            if (keyboard().R.held())
            {
                camera.horizon += 100 * elapsedTime;
            }
            if (keyboard().F.held())
            {
                camera.horizon -= 100 * elapsedTime;
            }
        }
        if (keyboard().SPACE.held())
        {
            camera.height += cameraSpeed * elapsedTime;
        }
        if (keyboard().L_SHIFT.held())
        {
            camera.height -= cameraSpeed * elapsedTime;
        }
        if (keyboard().UP.held() || keyboard().W.held())
        {
            camera.x -= cameraSpeed * Math.sin(camera.angle) * elapsedTime;
            camera.y -= cameraSpeed * Math.cos(camera.angle) * elapsedTime;
        }
        if (keyboard().DOWN.held() || keyboard().S.held())
        {
            camera.x += cameraSpeed * Math.sin(camera.angle) * elapsedTime;
            camera.y += cameraSpeed * Math.cos(camera.angle) * elapsedTime;
        }
        
        if (camera.horizon < -screenHeight()) camera.horizon = -screenHeight();
        if (camera.horizon > screenHeight()) camera.horizon = screenHeight();
        
        // Collision detection. Don't fly below the surface.
        int    cameraX = (int) Math.floor(camera.x) & mapWidthPeriod;
        int    cameraY = (int) Math.floor(camera.y) & mapHeightPeriod;
        double cameraU = camera.x / mapWidthPeriod;
        double cameraV = camera.y / mapHeightPeriod;
        
        int height = 10 + (sampleHeight ? heightMap.sampleBL(cameraU, cameraV).r() : heightMap.getPixel(cameraX, cameraY).r());
        if (height > camera.height) camera.height = height;
        
        clear(background);
        
        double sin = Math.sin(camera.angle);
        double cos = Math.cos(camera.angle);
        
        int[] hiddenY = new int[screenWidth()];
        for (int i = 0; i < screenWidth(); i++)
        {
            hiddenY[i] = screenHeight();
        }
        
        double deltaZ = 1.0;
        int[]  pixels = loadPixels();
        
        // Draw from front to back
        for (int z = 1; z < camera.distance; z += deltaZ)
        {
            // 90 degree field of view
            double plx = -cos * z - sin * z;
            double ply = sin * z - cos * z;
            double prx = cos * z - sin * z;
            double pry = -sin * z - cos * z;
            
            double dx = (prx - plx) / screenWidth();
            double dy = (pry - ply) / screenWidth();
            plx += camera.x;
            ply += camera.y;
            double invZ = 1. / z * 240.;
            for (int i = 0; i < screenWidth(); i++)
            {
                int    textureX = (int) Math.floor(plx) & mapWidthPeriod;
                int    textureY = (int) Math.floor(ply) & mapHeightPeriod;
                double u        = plx / mapWidthPeriod;
                double v        = ply / mapHeightPeriod;
                u -= Math.floor(u);
                v -= Math.floor(v);
                
                int altitude     = sampleHeight ? heightMap.sampleBL(u, v).r() : heightMap.getPixel(textureX, textureY).r();
                int screenHeight = (int) ((camera.height - altitude) * invZ + camera.horizon);
                
                DrawVerticalLine(i, screenHeight, hiddenY[i], sampleColor ? colorMap.sampleBL(u, v) : colorMap.getPixel(textureX, textureY), pixels);
                if (screenHeight < hiddenY[i]) hiddenY[i] = screenHeight;
                plx += dx;
                ply += dy;
            }
            deltaZ += 0.005;
        }
        updatePixels();
    }
    
    void DrawVerticalLine(int x, int yTop, int yBottom, Colorc col, int[] data)
    {
        if (yTop < 0) yTop = 0;
        if (yTop > yBottom) return;
        
        // get offset on screen for the vertical line
        int offset = ((yTop * screenWidth()) + x) * 4;
        for (int k = yTop; k < yBottom; k++)
        {
            data[offset]     = col.r();
            data[offset + 1] = col.g();
            data[offset + 2] = col.b();
            offset           = offset + screenWidth() * 4;
        }
    }
    
    public static void main(String[] args)
    {
        start(new VoxelSpace());
    }
}