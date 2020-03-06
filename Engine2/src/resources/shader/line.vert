#version 330

layout(location = 0) in vec3 aPosition;

uniform mat4 pv;

out vec3 position;

void main(void)
{
    position = aPosition;
    //    gl_Position = pv * vec4(aPosition, 1.0);
    gl_Position = vec4(aPosition, 1.0);
}
