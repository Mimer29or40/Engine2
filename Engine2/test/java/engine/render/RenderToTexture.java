package engine.render;

import engine.color.Color;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL43.*;

public class RenderToTexture extends OpenGLDemo
{
    static String vertexShader =
            "#version 120\n" +
            "\n" +
            "attribute vec3 vertexPosition;\n" +
            "attribute vec3 vertexNormal;\n" +
            "attribute vec3 vertexColor;\n" +
            "attribute vec2 texCoord2d;\n" +
            "\n" +
            "uniform mat4 modelViewMatrix;\n" +
            "uniform mat3 normalMatrix;\n" +
            "uniform mat4 projectionMatrix;\n" +
            "\n" +
            "struct LightSource\n" +
            "{\n" +
            "    vec3 ambient;\n" +
            "    vec3 diffuse;\n" +
            "    vec3 specular;\n" +
            "    vec3 position;\n" +
            "};\n" +
            "uniform LightSource lightSource;\n" +
            "\n" +
            "struct LightModel\n" +
            "{\n" +
            "    vec3 ambient;\n" +
            "};\n" +
            "uniform LightModel lightModel;\n" +
            "\n" +
            "struct Material {\n" +
            "    vec3  emission;\n" +
            "    vec3  specular;\n" +
            "    highp float shininess;\n" +
            "};\n" +
            "uniform Material material;\n" +
            "\n" +
            "varying vec3 v_color;\n" +
            "varying vec2 v_texCoord2d;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    vec3 normal     = normalize(normalMatrix * vertexNormal);                     // normal vector              \n" +
            "    vec3 position   = vec3(modelViewMatrix * vec4(vertexPosition, 1));            // vertex pos in eye coords   \n" +
            "    vec3 halfVector = normalize(lightSource.position + vec3(0,0,1));              // light half vector          \n" +
            "    float nDotVP    = dot(normal, normalize(lightSource.position));               // normal . light direction   \n" +
            "    float nDotHV    = max(0., dot(normal,  halfVector));                          // normal . light half vector \n" +
            "    float pf        = mix(0., pow(nDotHV, material.shininess), step(0., nDotVP)); // power factor               \n" +
            "\n" +
            "    vec3 ambient    = lightSource.ambient;\n" +
            "    vec3 diffuse    = lightSource.diffuse * nDotVP;\n" +
            "    vec3 specular   = lightSource.specular * pf;\n" +
            "    vec3 sceneColor = material.emission + vertexColor * lightModel.ambient;\n" +
            "\n" +
            "    v_color = clamp(sceneColor +                           \n" +
            "                    ambient  * vertexColor +               \n" +
            "                    diffuse  * vertexColor +               \n" +
            "                    specular * material.specular, 0., 1. );\n" +
            "\n" +
            "    v_texCoord2d = texCoord2d;\n" +
            "\n" +
            "    gl_Position = projectionMatrix * modelViewMatrix * vec4(vertexPosition, 1);\n" +
            "}\n";
    
    static String fragmentShader =
            "#version 120\n" +
            "\n" +
            "uniform sampler2D texUnit;\n" +
            "\n" +
            "varying vec3 v_color;\n" +
            "varying vec2 v_texCoord2d;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = vec4(v_color, 1) * texture2D(texUnit, v_texCoord2d);\n" +
            "}\n";
    
    static String[] circles = new String[] {
            "................",
            "................",
            "......xxxx......",
            "....xxxxxxxx....",
            "...xxxxxxxxxx...",
            "...xxx....xxx...",
            "..xxx......xxx..",
            "..xxx......xxx..",
            "..xxx......xxx..",
            "..xxx......xxx..",
            "...xxx....xxx...",
            "...xxxxxxxxxx...",
            "....xxxxxxxx....",
            "......xxxx......",
            "................",
            "................"
    };
    
    boolean running = true;
    
    Shader      shader;
    VertexArray vertexArray;
    
    Texture texture;
    
    final Matrix4f view       = new Matrix4f();
    final Matrix4f projection = new Matrix4f();
    
    @Override
    protected void init(String title)
    {
        super.init(title);
        
        this.shader = new Shader().loadVertex(vertexShader).loadFragment(fragmentShader).validate().bind();
        this.shader.setVec3("lightSource.ambient", 0.0f, 0.0f, 0.0f);
        this.shader.setVec3("lightSource.diffuse", 1.0f, 1.0f, 1.0f);
        this.shader.setVec3("lightSource.specular", 1.0f, 1.0f, 1.0f);
        this.shader.setVec3("lightSource.position", 0.0f, 1.0f, 1.0f);
        this.shader.setVec3("lightModel.ambient", 0.2f, 0.2f, 0.2f);
        this.shader.setVec3("material.emission", 0.0f, 0.0f, 0.0f);
        this.shader.setVec3("material.specular", 1.0f, 1.0f, 1.0f);
        this.shader.setUniform("material.shininess", 10.0f);
        
        this.vertexArray = new VertexArray();
        this.vertexArray.add(new float[] {
                1.0f, 1.0f, -1.0f,  // Green
                -1.0f, 1.0f, -1.0f, // Green
                -1.0f, 1.0f, 1.0f,  // Green
                1.0f, 1.0f, 1.0f,   // Green
                1.0f, -1.0f, 1.0f,  // Orange
                -1.0f, -1.0f, 1.0f, // Orange
                -1.0f, -1.0f, -1.0f,// Orange
                1.0f, -1.0f, -1.0f, // Orange
                1.0f, 1.0f, 1.0f,   // Red
                -1.0f, 1.0f, 1.0f,  // Red
                -1.0f, -1.0f, 1.0f, // Red
                1.0f, -1.0f, 1.0f,  // Red
                1.0f, -1.0f, -1.0f, // Yellow
                -1.0f, -1.0f, -1.0f,// Yellow
                -1.0f, 1.0f, -1.0f, // Yellow
                1.0f, 1.0f, -1.0f,  // Yellow
                -1.0f, 1.0f, 1.0f,  // Blue
                -1.0f, 1.0f, -1.0f, // Blue
                -1.0f, -1.0f, -1.0f,// Blue
                -1.0f, -1.0f, 1.0f, // Blue
                1.0f, 1.0f, -1.0f,  // Magenta
                1.0f, 1.0f, 1.0f,   // Magenta
                1.0f, -1.0f, 1.0f,  // Magenta
                1.0f, -1.0f, -1.0f  // Magenta
        }, 24);
        this.vertexArray.add(new float[] {
                0.0f, 1.0f, 0.0f,  // Green
                0.0f, 1.0f, 0.0f,  // Green
                0.0f, 1.0f, 0.0f,  // Green
                0.0f, 1.0f, 0.0f,  // Green
                0.0f, -1.0f, 0.0f, // Orange
                0.0f, -1.0f, 0.0f, // Orange
                0.0f, -1.0f, 0.0f, // Orange
                0.0f, -1.0f, 0.0f, // Orange
                0.0f, 0.0f, 1.0f,  // Red
                0.0f, 0.0f, 1.0f,  // Red
                0.0f, 0.0f, 1.0f,  // Red
                0.0f, 0.0f, 1.0f,  // Red
                0.0f, 0.0f, -1.0f, // Yellow
                0.0f, 0.0f, -1.0f, // Yellow
                0.0f, 0.0f, -1.0f, // Yellow
                0.0f, 0.0f, -1.0f, // Yellow
                -1.0f, 0.0f, 0.0f, // Blue
                -1.0f, 0.0f, 0.0f, // Blue
                -1.0f, 0.0f, 0.0f, // Blue
                -1.0f, 0.0f, 0.0f, // Blue
                1.0f, 0.0f, 0.0f,  // Magenta
                1.0f, 0.0f, 0.0f,  // Magenta
                1.0f, 0.0f, 0.0f,  // Magenta
                1.0f, 0.0f, 0.0f,  // Magenta
        }, 24);
        this.vertexArray.add(new float[] {
                0.0f, 1.0f, 0.0f, // Green
                0.0f, 1.0f, 0.0f, // Green
                0.0f, 1.0f, 0.0f, // Green
                0.0f, 1.0f, 0.0f, // Green
                1.0f, 0.5f, 0.0f, // Orange
                1.0f, 0.5f, 0.0f, // Orange
                1.0f, 0.5f, 0.0f, // Orange
                1.0f, 0.5f, 0.0f, // Orange
                1.0f, 0.0f, 0.0f, // Red
                1.0f, 0.0f, 0.0f, // Red
                1.0f, 0.0f, 0.0f, // Red
                1.0f, 0.0f, 0.0f, // Red
                1.0f, 1.0f, 0.0f, // Yellow
                1.0f, 1.0f, 0.0f, // Yellow
                1.0f, 1.0f, 0.0f, // Yellow
                1.0f, 1.0f, 0.0f, // Yellow
                0.0f, 0.0f, 1.0f, // Blue
                0.0f, 0.0f, 1.0f, // Blue
                0.0f, 0.0f, 1.0f, // Blue
                0.0f, 0.0f, 1.0f, // Blue
                1.0f, 0.0f, 1.0f, // Magenta
                1.0f, 0.0f, 1.0f, // Magenta
                1.0f, 0.0f, 1.0f, // Magenta
                1.0f, 0.0f, 1.0f, // Magenta
        }, 24);
        this.vertexArray.add(new float[] {
                0.0f, 0.0f, // Green
                0.0f, 1.0f, // Green
                1.0f, 1.0f, // Green
                1.0f, 0.0f, // Green
                0.0f, 0.0f, // Orange
                0.0f, 1.0f, // Orange
                1.0f, 1.0f, // Orange
                1.0f, 0.0f, // Orange
                0.0f, 0.0f, // Red
                0.0f, 1.0f, // Red
                1.0f, 1.0f, // Red
                1.0f, 0.0f, // Red
                0.0f, 0.0f, // Yellow
                0.0f, 1.0f, // Yellow
                1.0f, 1.0f, // Yellow
                1.0f, 0.0f, // Yellow
                0.0f, 0.0f, // Blue
                0.0f, 1.0f, // Blue
                1.0f, 1.0f, // Blue
                1.0f, 0.0f, // Blue
                0.0f, 0.0f, // Magenta
                0.0f, 1.0f, // Magenta
                1.0f, 1.0f, // Magenta
                1.0f, 0.0f, // Magenta
        }, 24);
        
        int[] indices = new int[36];
        for (int i = 0, v = 0, n = 6 * 4; v < n; v += 4)
        {
            indices[i++] = v;
            indices[i++] = v + 1;
            indices[i++] = v + 2;
            
            // second triangle (ccw winding)
            indices[i++] = v;
            indices[i++] = v + 2;
            indices[i++] = v + 3;
        }
        this.vertexArray.addEBO(indices, GL_STATIC_DRAW);
        
        this.view.identity();
        
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        
        glActiveTexture(GL_TEXTURE0);
        this.shader.setUniform("texUnit", 0);
        
        this.texture = new Texture(16, 16, 3, Color.GREY);
        for (int j = 0; j < 16; j++)
        {
            for (int i = 0; i < 16; i++)
            {
                if (circles[j].charAt(i) == 'x')
                {
                    this.texture.setPixel(i, j, Color.GREEN);
                }
            }
        }
        this.texture.bindTexture().upload();
    
        glClearColor(.5f, .5f, .5f, 1.f);
    }
    
    @Override
    protected void loop()
    {
        while (this.running)
        {
            glfwPollEvents();
            
            glfwSwapBuffers(this.window);
        }
    }
    
    @Override
    protected void windowCloseCallback(long window)
    {
        this.running = false;
    }
    
    public static void main(String[] args)
    {
        new RenderToTexture().run("Render to Texture");
    }
}
