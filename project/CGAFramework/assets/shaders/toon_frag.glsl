#version 330 core

//input from vertex shader

struct PointLight{
    vec3 color;
    vec3 attenuationValue;
};

struct SpotLight{
    vec3 color;
    vec3 spotLightDir;
    vec3 attenuationValue;
    float outerCone;
    float innerCone;
};

#define NR_POINT_LIGHTS 8
#define NR_SPOT_LIGHTS 18

uniform sampler2D emit;
uniform sampler2D diffuse;
uniform sampler2D specular;
uniform vec3 emitColor;
uniform float matShininess;
uniform PointLight pointLights[NR_POINT_LIGHTS];
uniform SpotLight spotLights[NR_SPOT_LIGHTS];

in struct VertexData
{
    vec3 pos;
    vec3 nor;
    vec2 tc;
} vertexData;

in vec3 toCamera;
in vec3 pointLightDirections[NR_POINT_LIGHTS];
in vec3 spotLightDirections[NR_SPOT_LIGHTS];

//fragment shader output
out vec4 color;



vec4 diffus(vec3 lightDir, vec3 lightColor){
    vec3 N = normalize(vertexData.nor);
    vec3 L = normalize(lightDir);
    vec3 diffTex = texture(diffuse, vertexData.tc).xyz;
    float cosa = max(0.0, dot(N,L)); //Skalarprodukt von N und L um Winkel zu berechnen. Nur positive Werte!
    vec3 DiffuseTerm = (diffTex * lightColor);
    return vec4(DiffuseTerm * cosa, 1.0);
}

vec4 spec(vec3 lightDir, vec3 lightColor){ //matShinisess, lightDir, lightColor übergeben?
    vec3 N = normalize(vertexData.nor);
    vec3 L = normalize(lightDir);
    vec3 specTex = texture(specular, vertexData.tc).xyz;
    vec3 V = normalize(toCamera);
    //    vec3 R = normalize(reflect(-L, N));

    vec3 halfvector = normalize(V + L);

    float cosBeta = max(0.0, dot(halfvector,N));
    float cosBetak = pow(cosBeta, matShininess);
    vec3 result = specTex * lightColor;
    return vec4(result * cosBetak, 0.0);
}

float intensity(SpotLight spotLight, vec3 calcLightDir){
    float theta = dot(normalize(spotLight.spotLightDir), normalize(-calcLightDir));
    float epsilon = spotLight.innerCone - spotLight.outerCone;
    float intensity = clamp((theta - spotLight.outerCone) / epsilon, 0.0, 1.0);
    return intensity;
}

void main(){
    color = texture(emit, vertexData.tc) * vec4(emitColor,0f);

    for(int i = 0; i < NR_POINT_LIGHTS; i++){
        float distance = length(pointLightDirections[i]);
        float attenuation = 1.0 / (pointLights[i].attenuationValue.x + pointLights[i].attenuationValue.y * distance + pointLights[i].attenuationValue.z * (distance * distance));
        color += diffus(pointLightDirections[i], pointLights[i].color)*attenuation + spec(pointLightDirections[i], pointLights[i].color)*attenuation;
    }

    for(int i = 0; i < NR_SPOT_LIGHTS; i++){
        float spotDistance = length(spotLightDirections[i]);
        float spotAttenuation = 1.0 / (spotLights[i].attenuationValue.x + spotLights[i].attenuationValue.y * spotDistance + spotLights[i].attenuationValue.z * (spotDistance * spotDistance));
        color += diffus(spotLightDirections[i], spotLights[i].color) * spotAttenuation+ spec(spotLightDirections[i], spotLights[i].color) *spotAttenuation * intensity(spotLights[i], spotLightDirections[i]);
//        color = diffus(spotLightDirections[i], spotLights[i].color);
    }




    //Gammakorrektur
    //    float theta = dot(spotLightDirection, normalize(-spotLightData.lightDir));
    //    float epsilon = spotLightInnerCone - spotLightOuterCone;
    //    float intensity = clamp((theta - spotLightOuterCone) / epsilon, 0.0, 1.0);




    //    color = vec4();
    //    color += spec(spotLightData.lightDir, spotLightColor) *spotAttenuation * intensity();


}