package engine.vs;

import engine.Engine;
import engine.color.Color;
import engine.color.Colorc;
import engine.render.Texture;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Arrays;

public class VoxelSpace extends Engine
{
    static class Camera
    {
        public boolean flying   = true;
        public boolean onGround = false;
        
        final Vector3d pos = new Vector3d(512, 78, 512);
        final Vector3d vel = new Vector3d(0);
        final Vector3d acc = new Vector3d(0);
        
        final Vector3d x = new Vector3d(1, 0, 0);
        final Vector3d y = new Vector3d(0, 1, 0);
        final Vector3d z = new Vector3d(0, 0, 1);
        
        final Vector3d right = new Vector3d(1, 0, 0);
        final Vector3d up    = new Vector3d(0, 1, 0);
        final Vector3d front = new Vector3d(0, 0, 1);
        
        final Vector3d look = new Vector3d(0, 0, -1);
        
        final double fov = 75;
        
        // double x        = 512.; // x position on the map
        // double y        = 800.; // y position on the map
        // double height   = 78.;  // height of the camera
        // double angle    = 0.;   // direction of the camera
        double horizon = 100.; // horizon position (look up and down)
        final int distance = 800;  // distance of map
    }
    
    static class HeightMap
    {
        final Color temp = new Color();
        
        final int size = 1024;
        
        @SuppressWarnings("SameReturnValue")
        double maxHeight()
        {
            return 255;
        }
        
        double getHeight(double x, double z)
        {
            return 0;
        }
        
        double getHeightFast(double x, double z)
        {
            return 0;
        }
        
        Colorc getColor(double x, double z)
        {
            return this.temp.set(getHeight(x, z) / maxHeight(), 255);
        }
        
        Colorc getColorFast(double x, double z)
        {
            return this.temp.set(getHeightFast(x, z) / maxHeight(), 255);
        }
    }
    
    static class TextureHeightMap extends HeightMap
    {
        Texture colorTexture;
        Texture heightTexture;
        
        double getHeight(double x, double z)
        {
            double u = x / size;
            double v = z / size;
            return heightTexture.sampleBL(u - Math.floor(u), v - Math.floor(v)).r();
        }
        
        double getHeightFast(double x, double z)
        {
            int u = (int) Math.floor(x) % size;
            int v = (int) Math.floor(z) % size;
            if (u < 0) u += size;
            if (v < 0) v += size;
            return heightTexture.getPixel(u, v).r();
        }
        
        Colorc getColor(double x, double z)
        {
            double u = x / size;
            double v = z / size;
            return colorTexture.sampleBL(u - Math.floor(u), v - Math.floor(v));
        }
        
        Colorc getColorFast(double x, double z)
        {
            int u = ((int) Math.floor(x)) % size;
            int v = ((int) Math.floor(z)) % size;
            if (u < 0) u += size;
            if (v < 0) v += size;
            return colorTexture.getPixel(u, v);
        }
    }
    
    void loadMap(String fileNames)
    {
        String[] files = fileNames.split(";");
        
        map.colorTexture  = Texture.loadImage("maps/" + files[0] + ".png");
        map.heightTexture = Texture.loadImage("maps/" + files[1] + ".png");
        
        notification("Loaded Map: " + fileNames);
    }
    
    final Camera camera = new Camera();
    
    final Color background = new Color("#FFE09090");
    
    TextureHeightMap map;
    final ArrayList<String> maps = new ArrayList<>();
    
    int selectedMap = 0;
    
    boolean sampleHeight = false;
    boolean sampleColor  = false;
    
    @Override
    public void setup()
    {
        size(400, 400, 2, 2);
        
        map = new TextureHeightMap();
        
        maps.add("C1W;D1");
        maps.add("C2W;D2");
        maps.add("C3;D3");
        maps.add("C4;D4");
        maps.add("C5W;D5");
        maps.add("C6W;D6");
        maps.add("C7W;D7");
        maps.add("C8;D6");
        maps.add("C9W;D9");
        maps.add("C10W;D10");
        maps.add("C11W;D11");
        maps.add("C12W;D11");
        maps.add("C13;D13");
        maps.add("C14;D14");
        maps.add("C14W;D14");
        maps.add("C15;D15");
        maps.add("C16W;D16");
        maps.add("C17W;D17");
        maps.add("C18W;D18");
        maps.add("C19W;D19");
        maps.add("C20W;D20");
        maps.add("C21;D21");
        maps.add("C22W;D22");
        maps.add("C23W;D21");
        maps.add("C24W;D24");
        maps.add("C25W;D25");
        maps.add("C26W;D18");
        maps.add("C27W;D15");
        maps.add("C28W;D25");
        maps.add("C29W;D16");
        
        loadMap(maps.get(selectedMap));
    }
    
    @SuppressWarnings("StatementWithEmptyBody")
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
        
        if (mouse().LEFT.down()) mouse().toggleCaptured();
        if (keyboard().ESCAPE.down()) mouse().captured(false);
        
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
        
        double sensitivity = 4;
        
        // Movement
        double moveAcc  = 300;
        double maxSpeed = 200;
        
        boolean  moved = false;
        Vector3d temp  = new Vector3d();
        
        if (mouse().captured())
        {
            if (mouse().relX() != 0)
            {
                double angle = mouse().relX() * -0.1 * sensitivity * elapsedTime;
                camera.x.rotateAxis(angle, camera.up.x(), camera.up.y(), camera.up.z());
                camera.y.rotateAxis(angle, camera.up.x(), camera.up.y(), camera.up.z());
                camera.z.rotateAxis(angle, camera.up.x(), camera.up.y(), camera.up.z());
                camera.right.rotateAxis(angle, camera.up.x(), camera.up.y(), camera.up.z());
                // camera.up.rotateAxis(angle, camera.up.x(), camera.up.y(), camera.up.z());
                camera.front.rotateAxis(angle, camera.up.x(), camera.up.y(), camera.up.z());
                camera.look.rotateAxis(-angle, camera.up.x(), camera.up.y(), camera.up.z());
            }
            if (mouse().relY() != 0)
            {
                double angle = mouse().relY() * -0.1 * sensitivity * elapsedTime;
                camera.y.rotateAxis(angle, camera.x.x(), camera.x.y(), camera.x.z());
                camera.z.rotateAxis(angle, camera.x.x(), camera.x.y(), camera.x.z());
                // camera.up.rotateAxis(angle, camera.x.x(), camera.x.y(), camera.x.z());
                // camera.front.rotateAxis(angle, camera.x.x(), camera.x.y(), camera.x.z());
                camera.look.rotateAxis(-angle, camera.x.x(), camera.x.y(), camera.x.z());
            }
        }
        else
        {
            if (keyboard().UP.held())
            {
            
            }
        }
        
        if (keyboard().W.held())
        {
            camera.acc.add(temp.set(camera.front).mul(-moveAcc));
            moved = true;
        }
        else
        {
            camera.acc.add(temp.set(camera.front).mul(moveAcc));
        }
        if (keyboard().S.held())
        {
            camera.acc.add(temp.set(camera.front).mul(moveAcc));
            moved = true;
        }
        else
        {
            camera.acc.add(temp.set(camera.front).mul(-moveAcc));
        }
        if (keyboard().A.held())
        {
            camera.acc.add(temp.set(camera.right).mul(-moveAcc));
            moved = true;
        }
        else
        {
            camera.acc.add(temp.set(camera.right).mul(moveAcc));
        }
        if (keyboard().D.held())
        {
            camera.acc.add(temp.set(camera.right).mul(moveAcc));
            moved = true;
        }
        else
        {
            camera.acc.add(temp.set(camera.right).mul(-moveAcc));
        }
        
        if (keyboard().F.down()) camera.flying = !camera.flying;
        if (!camera.flying)
        {
            if (camera.onGround)
            {
                if (keyboard().SPACE.down())
                {
                    camera.acc.add(temp.set(camera.up).mul(1000000));
                    moved = true;
                }
            }
            else
            {
                camera.acc.add(temp.set(camera.up).mul(-9.81 * 50));
                moved = true;
            }
        }
        else
        {
            if (keyboard().SPACE.held())
            {
                camera.acc.add(temp.set(camera.up).mul(moveAcc));
                moved = true;
            }
            else
            {
                camera.acc.add(temp.set(camera.up).mul(-moveAcc));
            }
            if (keyboard().L_SHIFT.held())
            {
                camera.acc.add(temp.set(camera.up).mul(-moveAcc));
                moved = true;
            }
            else
            {
                camera.acc.add(temp.set(camera.up).mul(moveAcc));
            }
        }
        
        // Camera Update
        camera.vel.x += camera.acc.x * elapsedTime;
        camera.vel.y += camera.acc.y * elapsedTime;
        camera.vel.z += camera.acc.z * elapsedTime;
        
        camera.acc.set(0);
        
        if (camera.vel.lengthSquared() > maxSpeed * maxSpeed) camera.vel.normalize().mul(maxSpeed);
        
        if (!moved) camera.vel.mul(camera.onGround ? 0.7 : 0.92);
        
        if (Math.abs(camera.vel.x) < 0.0001) camera.vel.x = 0;
        if (Math.abs(camera.vel.y) < 0.0001) camera.vel.y = 0;
        if (Math.abs(camera.vel.z) < 0.0001) camera.vel.z = 0;
        
        camera.pos.x += camera.vel.x * elapsedTime;
        camera.pos.y += camera.vel.y * elapsedTime;
        camera.pos.z += camera.vel.z * elapsedTime;
        
        camera.onGround = false;
        int height = 10 + (int) (sampleHeight ? map.getHeight(camera.pos.x, camera.pos.z) : map.getHeightFast(camera.pos.x, camera.pos.z));
        if (height > camera.pos.y)
        {
            camera.pos.y    = height;
            camera.onGround = true;
        }
        
        clear(background);
        
        int[] pixels = loadPixels();
        
        // ApacheDraw(pixels);
        SteepParallax(pixels);
        
        updatePixels();
        
        drawDebugText(0, 0, "camera.pos: " + camera.pos.toString());
        drawDebugText(0, 12, "camera.vel: " + camera.vel.toString());
        drawDebugText(0, 24, "camera.look: " + camera.look.toString());
    }
    
    void ApacheDraw(int[] pixels)
    {
        double angle   = -Math.atan2(camera.right.z, camera.right.x);
        double horizon = screenHeight() * (1 - camera.up.dot(camera.look)); // TODO - I need to think for about this math
        
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        
        int[] hiddenY = new int[screenWidth()];
        Arrays.fill(hiddenY, screenHeight());
        
        double deltaZ = 1.0;
        
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
            plx += camera.pos.x;
            ply += camera.pos.z;
            double invZ = 1. / z * 240.;
            for (int i = 0; i < screenWidth(); i++)
            {
                int altitude     = (int) (sampleHeight ? map.getHeight(plx, ply) : map.getHeightFast(plx, ply));
                int screenHeight = (int) ((camera.pos.y - altitude) * invZ + horizon);
                
                DrawVerticalLine(i, screenHeight, hiddenY[i], sampleColor ? map.getColor(plx, ply) : map.getColorFast(plx, ply), pixels);
                if (screenHeight < hiddenY[i]) hiddenY[i] = screenHeight;
                plx += dx;
                ply += dy;
            }
            deltaZ += 0.005;
        }
    }
    
    void SteepParallax(int[] pixels)
    {
        Vector3d temp1 = new Vector3d();
        Vector3d temp2 = new Vector3d();
        Vector3d temp3 = new Vector3d();
        
        Vector3d cameraRay  = new Vector3d();
        Vector2d offsetCord = new Vector2d();
        Vector2d textCord   = new Vector2d();
    
        Vector2d dTex = new Vector2d();
    
        int min = Math.min(screenWidth(), screenHeight());
        
        double sinFOV = Math.sin(camera.fov);
        double cosFOV = Math.cos(camera.fov);
        
        for (int j = 0; j < screenHeight(); j++)
        {
            int offset = j * screenWidth();
            
            for (int i = 0; i < screenWidth(); i++)
            {
                int index = (offset + i) * 4;
                
                double px = (i * 2.0 - screenWidth()) / min;
                double py = (j * 2.0 - screenHeight()) / min;
                
                temp1.set(camera.x).mul(-px * sinFOV);
                temp2.set(camera.y).mul(py * sinFOV);
                temp3.set(camera.z).mul(cosFOV);
                
                cameraRay.set(temp1).add(temp2).sub(temp3).normalize();
                
                if (cameraRay.y >= 0) continue;
                
                double signedDist = camera.up.dot(camera.pos);
                
                double d = -signedDist / cameraRay.dot(camera.up);
                
                if (d * 0.25 > camera.distance) continue;
                
                temp1.set(cameraRay).mul(d);
                temp2.set(camera.pos).add(temp1);
                
                offsetCord.set(temp2.x, temp2.z);
                //
                // final double minLayers = 5;
                // final double maxLayers = 15;
                // double numLayers = lerp(minLayers, maxLayers, Math.abs(cameraRay.dot(0, 1, 0)));
                //
                // double layerHeight = 1 / numLayers;
                // double currentLayerHeight = 0;
                //
                double initialHeight = (sampleHeight ? map.getHeight(offsetCord.x, offsetCord.y) : map.getHeightFast(offsetCord.x, offsetCord.y)) / 255;
    
                dTex.set(cameraRay.x, cameraRay.z).mul(100 * initialHeight);
    
                textCord.set(offsetCord).sub(dTex);
                
                Colorc color = sampleColor ? map.getColor(textCord.x, textCord.y) : map.getColorFast(textCord.x, textCord.y);
                
                pixels[index]     = color.r();
                pixels[index + 1] = color.g();
                pixels[index + 2] = color.b();
                pixels[index + 3] = 255;
                
                // pixels[index]     = 255 * i / screenWidth();
                // pixels[index + 1] = 255 * j / screenHeight();
                // pixels[index + 2] = 0;
                // pixels[index + 3] = 255;
            }
        }
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