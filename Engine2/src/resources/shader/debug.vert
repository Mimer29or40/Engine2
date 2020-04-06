#version 330

uniform mat4 pv;

layout(location = 0) in vec2 aPosition;

void main(void) {
    gl_Position = pv * vec4(aPosition, 0.0, 1.0);
}
