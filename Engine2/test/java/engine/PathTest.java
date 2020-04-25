package engine;

import java.nio.file.Path;

import static engine.util.Util.getPath;

public class PathTest
{
    public static void main(String[] args)
    {
        Path path = getPath("shaders/pixel.vert");
    }
}
