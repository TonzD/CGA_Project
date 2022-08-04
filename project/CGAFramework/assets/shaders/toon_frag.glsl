#version 330 core

//input from vertex shader

struct PointLight{
    vec3 color;
    vec3 attenuationValue;
};

struct SpotLight{
    vec3 color;
    vec3 spotLightDirection;
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
uniform PointLight pointLights;
uniform SpotLight spotLights;
const float levels = 3.0;
                                                    //uniform vec3 pointLightColor;
                                                    //uniform vec3 pointLight2Color;
                                                    //uniform vec3 spotLightColor
                                                    //uniform vec3 spotLightDirection;
                                                    //uniform float spotLightOuterCone;
                                                    //uniform float spotLightInnerCone;
                                                    //uniform vec3 pointLightquelle;
                                                    //uniform vec3 pointLight2quelle;
                                                    //uniform vec3 spotLightquelle;



in struct VertexData
{
vec3 position;
vec3 normal;
vec2 tc;
vec3 viewDir;
} vertexData;

in vec3 pointLightDirections;
in vec3 spotLightDirections;
                                                         //in struct PointLightData
                                                         //{
                                                         //    vec3 lightDir;
                                                         //    vec3 viewDir;
                                                         //}pointLightData;
                                                         //
                                                         //in struct SpotLightData
                                                         //{
                                                         //    vec3 lightDir;
                                                         //}spotLightData;

                                                         //fragment shader output
out vec4 color;



vec4 diffus(vec3 lightDir, vec3 lightColor){
    vec3 N = normalize(vertexData.normal);
    vec3 L = normalize(lightDir);
    vec3 diffTex = texture(diffuse, vertexData.tc).xyz;
    float cosa = max(0.0, dot(N,L)); //Skalarprodukt von N und L um Winkel zu berechnen. Nur positive Werte!
    float level = floor(cosa * levels);
    cosa = level/levels;
    vec3 DiffuseTerm = (diffTex * lightColor);
    return vec4(DiffuseTerm * cosa, 1.0);
     }

float toon(vec3 lightDir, vec3 lightColor){
vec3 N = normalize(vertexData.normal);
vec3 L = normalize(lightDir);
vec3 diffTex = texture(diffuse, vertexData.tc).xyz;
float cosa = max(0.0, dot(N,L));
return cosa;
}
vec4 spec(vec3 lightDir, vec3 lightColor){ //matShinisess, lightDir, lightColor übergeben?
vec3 N = normalize(vertexData.normal);
vec3 L = normalize(lightDir);
vec3 specTex = texture(specular, vertexData.tc).xyz;
 vec3 V = normalize(vertexData.viewDir);
                                                                                                 //    vec3 R = normalize(reflect(-L, N));
vec3 halfvector = normalize(V + L);
float cosBeta = max(0.0, dot(halfvector,N));
float cosBetak = pow(cosBeta, matShininess);

float level = floor(cosBetak * levels);
cosBetak = level/levels;

vec3 result = specTex * lightColor;
return vec4(result * cosBetak, 0.0);
}

float intensity(SpotLight spotLight, vec3 calcLightDir){
                                                           float theta = dot(normalize(spotLight.spotLightDirection), normalize(-calcLightDir));
                                                           float epsilon = spotLight.innerCone - spotLight.outerCone;
                                                           float intensity = clamp((theta - spotLight.outerCone) / epsilon, 0.0, 1.0);
                                                           if (intensity > 0.75){
                                                           intensity = 0.75;
                                                           } else if (intensity > 0.5){
                                                           intensity = 0.5;
                                                           } else if (intensity > 0.25){
                                                           intensity = 0.25;
                                                           } else {
                                                           intensity = 0;
                                                           }
                                                           return intensity;
                                                       }

void main(){

               //    for(int i = 0; i < NR_SPOT_LIGHTS; i++){
               //        float intensity = toon(spotLightDirections[i], spotLights[i].color);
               //        if (intensity > 0.95)
               //        color += vec4(1.0,1.0,1.0,1.0);
               //        else if (intensity > 0.5)
               //        color += vec4(0.6,0.6,0.6,1.0);
               //        else if (intensity > 0.25)
               //        color += vec4(0.3,0.3,0.3,1.0);
               //        else
               //        color += vec4(0.0,0.0,0.0,1.0);
               //    }
               //
               //    for(int i = 0; i < NR_POINT_LIGHTS; i++){
               //        float intensity = toon(pointLightDirections[i], pointLights[i].color);
               //        if (intensity > 0.95)
               //        color += vec4(1.0,1.0,1.0,1.0);
               //        else if (intensity > 0.5)
               //        color += vec4(0.6,0.6,0.6,1.0);
               //        else if (intensity > 0.25)
               //        color += vec4(0.3,0.3,0.3,1.0);
               //        else
               //        color += vec4(0.0,0.0,0.0,1.0);
               //    }
               color = texture(emit, vertexData.tc) * vec4(emitColor,0f);
float distance = length(pointLightDirections);
float attenuation = 1.0 / (pointLights.attenuationValue.x + pointLights.attenuationValue.y * distance + pointLights.attenuationValue.z * (distance * distance));
float spotDistance = length(spotLightDirections);
color += diffus(pointLightDirections, pointLights.color)*attenuation + spec(pointLightDirections, pointLights.color)*attenuation;
float spotAttenuation = 1.0 / (spotLights.attenuationValue.x + spotLights.attenuationValue.y * spotDistance + spotLights.attenuationValue.z * (spotDistance * spotDistance));
                                                                                                                                                                                                                                                                                                color += diffus(spotLightDirections, spotLights.color) * spotAttenuation+ spec(spotLightDirections, spotLights.color) *spotAttenuation * intensity(spotLights, spotLightDirections);
                                                          //        color = diffus(spotLightDirections[i], spotLights[i].color);





               //Gammakorrektur
               //    float theta = dot(spotLightDirection, normalize(-spotLightData.lightDir));
               //    float epsilon = spotLightInnerCone - spotLightOuterCone;
               //    float intensity = clamp((theta - spotLightOuterCone) / epsilon, 0.0, 1.0);




               //    color = vec4();
               //    color += spec(spotLightData.lightDir, spotLightColor) *spotAttenuation * intensity();


           }