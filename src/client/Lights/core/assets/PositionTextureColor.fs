

uniform sampler2D texture0;


varying vec3 v_position;
varying vec2 v_texCoords;
varying vec2 v_offset;


void main()
{

    vec4 color = texture2D(texture0, v_texCoords);
    vec4 outline = vec4(0.0, 0.0, 0.0, 1.0);



    float length = length(v_offset);
    float inside = .15;
    


    // Outline width threshold.
    float outside = 1.0 - inside;
    
    // Fill/outline color.
    float fo_step = smoothstep(max(outside - inside, 0.0), outside, length);
    float alpha = 1.0 - smoothstep(1.0 - inside, 1.0, length);


    vec4 fo_color = mix(color, outline, fo_step);
    

    gl_FragColor = vec4(fo_color.rgb, alpha * fo_color.a + .5);





    //gl_FragData[0] =  vec4(v_offset.x, v_offset.y, 1.0, 1.0);

    //gl_FragData[0].a *= a;

}