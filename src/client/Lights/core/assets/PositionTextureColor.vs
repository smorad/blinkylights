attribute vec4 a_position;
attribute vec2 a_texCoords;
attribute vec2 a_offset;

varying vec3 v_position;
varying vec2 v_texCoords;
varying vec2 v_offset;

uniform mat4 viewProjection;


void main()
{
    
	v_texCoords = a_texCoords;
	v_position = a_position.xyz;
    v_offset = a_offset;




    gl_Position = viewProjection * a_position;



}