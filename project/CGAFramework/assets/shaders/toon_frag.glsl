#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 tc;
    vec3 normal;

} vertexData;


// Spotlight Uniforms

uniform vec3 spotColor;
uniform vec3 spotPos;
uniform vec3 preSpotDir;
uniform float outerAngle;
uniform float innerAngle;


//Pointlight0 Uniforms
uniform vec3 pointPos0;
uniform vec3 lightColor0;
//Pointlight1 Uniforms
uniform vec3 pointPos1;
uniform vec3 lightColor1;
uniform float shininess;

// Texture Uniforms
uniform sampler2D diff;
uniform sampler2D emit;
uniform sampler2D spec;
uniform sampler2D normalMap;
uniform int renderNormalMap;

uniform vec3 staticColor;
uniform vec3 camPos;

//fragment shader output
out vec4 color;

const float levels =3.0f;

float g = 2.2;

vec3 gamma( vec3 C_linear){
     return pow(C_linear.rgb, vec3(1.0/g));
}

vec3 invgamma( vec3 C_gamma){
    return pow(C_gamma.rgb, vec3(g));
}

void alphaMapping() {
        vec4 texColor = texture(emit, vertexData.tc);
        if(texColor.a < 0.1)
        discard;
}

vec3 normalMapping() {
    float xval = vertexData.position.x;
    float zval = vertexData.position.z;
    vec3 xvec = vec3(xval,0.0,0.0);
    vec3 zvec = vec3(0.0,0.0,zval);
    vec3 newVertexNormal = cross(xvec,zvec);
    if(newVertexNormal.y < 0){
        newVertexNormal.y *-1.0;
    }
    newVertexNormal = normalize(newVertexNormal);
    vec3 rgb_normal = newVertexNormal * 0.5 + 0.5;
    rgb_normal = texture(normalMap, vertexData.tc).rgb;
    rgb_normal = normalize(rgb_normal * 2.0 - 1.0);
    return rgb_normal;
}

vec4 ambientTerm(float ambientStrength, vec3 lightColor){
    vec3 ambient = ambientStrength * lightColor;
    vec3 objectColor=invgamma(texture(emit,vertexData.tc).rgb);
    vec3 result = objectColor*ambient;
    return vec4(result,1.0f);
}

vec4 diffterm(vec3 norm,vec3 lightDir,vec3 lightColor){
  //   vec3 diffTex=invgamma(texture(diff,vertexData.tc).xyz);
     vec3 diffColor=invgamma(texture(diff,vertexData.tc).xyz);
     float diff = max(0.0, dot(norm,lightDir));
     float level=floor(diff*levels);
     diff= level/levels;
     vec3 result= vec3(diffColor*lightColor);
     return vec4(result*diff,1.0f);
}

vec4 specterm(float specularStrength,vec3 norm,vec3 lightDir,vec3 viewDir,vec3 lightColor){
  //   vec3 diffTex=invgamma(texture(diff,vertexData.tc).xyz);
     vec3 diffColor=invgamma(texture(diff,vertexData.tc).xyz);
     vec3 reflectDir=reflect(-lightDir,norm);
     float specu = pow(max(dot(viewDir, reflectDir), 0.0f),shininess);
     vec3 specular = specularStrength * specu * lightColor;
     vec3 specColor=invgamma(texture(spec,vertexData.tc).xyz);
     vec3 result = specular * specColor;
     return vec4(result,1.0f);
}
void spotlight(vec3 norm,vec3 lightDir,vec3 lightColor,vec3 spotDir){
    float distance=length(lightDir);
    float attenuation = 1.0 / (distance * distance);
    //float attenuation = 1.0 / (1+(0.45*distance)+(0.0075*distance * distance));

    float theta = dot(lightDir,-spotDir);
    float epsilon= innerAngle-outerAngle;
    float intensity=clamp((theta-outerAngle)/epsilon,0.0,1.0);



    color+= diffterm(norm,lightDir,lightColor)*intensity;//*attenuation;

    float specularStrength = 0.2f;
    vec3 viewDir= normalize(camPos-vertexData.position);
    color+= specterm(specularStrength,norm,lightDir,viewDir,lightColor)*intensity;//*attenuation;
 //   color+=(diffterm(norm,sl,col)+specterm(norm,sl,col))*intensity*attenuation;
}
void pointlight(vec3 norm,vec3 lightDir,vec3 lightColor){
    float distance=length(lightDir);
  //  float attenuation = 1.0 / (distance * distance);
   // float attenuation = 1.0 / (1+(0.09*distance)+(0.032*distance * distance));
   float attenuation = 1.0 / (1.0f + 0.07f * distance + 0.017f * (distance * distance));

    color+= diffterm(norm,lightDir,lightColor)*attenuation;

    float specularStrength = 0.2f;
    vec3 viewDir= normalize(camPos-vertexData.position);
    color+= specterm(specularStrength,norm,lightDir,viewDir,lightColor)*attenuation;
}

void main(){
    // Ambiente Licht
    float ambientStrength = 0.1f;
    vec3 ambientLightColor= vec3(1.0f,1.0f,1.0f);
    color += ambientTerm(ambientStrength,ambientLightColor);

    // diffuse
    vec3 norm;
    if (renderNormalMap == 1){
        norm = normalMapping();
    } else {
        norm = normalize(vertexData.normal);
    }
    vec3 lightDir0=normalize(pointPos0-vertexData.position);
    vec3 lightDir1=normalize(pointPos1-vertexData.position);
  //  color+= diffterm(norm,lightDir,lightColor);

    //specular
//    float specularStrength = 0.2f;
  //  vec3 viewDir= normalize(camPos-vertexData.position);
 //   color+= specterm(specularStrength,norm,lightDir,viewDir,lightColor);

    //Spotlight
    alphaMapping();
    vec3 spotDir=normalize(preSpotDir);
    pointlight(norm, lightDir0, lightColor0);
    pointlight(norm, lightDir1, lightColor1);


 //  color += vec4(texture(emit,vertexData.tc).rgb,1.0f);//*staticColor

 //   spotlight(norm,spotColor,lightDir);
//    spotlight2(n,sl2,spotColor2,sd2);
//    pointlight(n,l,lightColor);

    color=vec4(gamma(color.rgb),0.0f);


}


