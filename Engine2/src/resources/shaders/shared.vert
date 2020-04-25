#version 460 core

layout(location = 0) in vec2 aPosition;

uniform mat4 pv;

out vec2 position;

void main(void)
{
    position = aPosition;
    gl_Position = pv * vec4(aPosition, 0.0, 1.0);
}
