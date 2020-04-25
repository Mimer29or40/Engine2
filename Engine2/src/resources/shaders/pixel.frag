#version 460 core

uniform sampler2D tex;

in vec2 cord;

out vec4 FragColor;

void main(void)
{
    FragColor = texture(tex, cord);
}
