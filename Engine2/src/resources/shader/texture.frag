#version 460 core

uniform vec4 tint;
uniform float interpolate;
uniform sampler2D texture1;
uniform sampler2D texture2;

in vec2 position;
in vec2 texCord;

out vec4 FragColor;

void main(void)
{
    if (interpolate <= 0)
    {
        FragColor = texture(texture1, texCord) * tint;
    }
    else if (interpolate >= 1)
    {
        FragColor = texture(texture2, texCord) * tint;
    }
    else
    {
        FragColor = mix(texture(texture1, texCord), texture(texture2, texCord), interpolate) * tint;
    }
}
