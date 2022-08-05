package cga.exercise.components.light

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.shader.ShaderProgram
import org.joml.Math.cos
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

class SpotLight(
    lPosI: Vector3f,
    rgb: Vector3f,
    private val outerAngle:Float= Math.toRadians(25.5).toFloat(),//cutoff
    private val innerAngle:Float= Math.toRadians(15.0).toFloat()
):PointLight(lPosI,rgb),ISpotLight{

    override fun bind(shaderProgram: ShaderProgram, cam:TronCamera) {
        shaderProgram.setUniform("camPos",cam.getWorldPosition())
        shaderProgram.setUniform("spotPos",getWorldPosition())
        shaderProgram.setUniform("lightColor",rgb)
        shaderProgram.setUniform("preSpotDir",getWorldZAxis())
        shaderProgram.setUniform("outerAngle",cos(outerAngle))
        shaderProgram.setUniform("innerAngle",cos(innerAngle))
    }
}