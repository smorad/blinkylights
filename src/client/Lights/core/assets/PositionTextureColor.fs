

uniform sampler2D texture0;


varying vec3 v_position;
varying vec2 v_texCoords;
varying vec2 v_offset;


void main()
{

	float a = ((.01 - length(v_offset)) * 1.0/.01);
    gl_FragData[0] =  texture2D(texture0, v_texCoords);
    //gl_FragData[0].a *= a;

}