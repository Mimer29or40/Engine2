#version 460 core

uniform sampler2D tex;

in vec2 position;
in vec2 texCord;

out vec4 FragColor;

void main(void)
{
    FragColor = texture(tex, texCord);
}
