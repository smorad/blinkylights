attribute vec4 a_position;
attribute vec2 a_offset;
attribute vec2 a_texCoords;

varying vec3 v_position;
varying vec2 v_offset;
varying vec2 v_texCoords;

uniform mat4 viewProjection;

mat4 rotationMatrix(vec3 axis, float angle)
{
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;
    
    return mat4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  0.0,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  0.0,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           0.0,
                0.0,                                0.0,                                0.0,                                1.0);
}

void main()
{
    
	float a = atan(a_position.x/a_position.z);

	v_offset = a_offset;
	v_texCoords = a_texCoords;
	v_position = a_position.xyz;
    gl_Position = viewProjection *(a_position + vec4(a_offset.xy, 0, 0) * rotationMatrix(vec3(0, 1, 0), a));

    //gl_Position = viewProjection * gl_position;
}