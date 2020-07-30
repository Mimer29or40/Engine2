#version 460 core

layout(location = 0) in vec2 aPosition;

layout(std140, binding = 0) uniform View { mat4 view; };

out vec2 position;

void main(void)
{
    position = aPosition;
    gl_Position = view * vec4(aPosition, 0.0, 1.0);
}
