package cga.exercise.components.camera
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Matrix4f

class TronCamera(): ICamera, Transformable() {

    override fun getCalculateViewMatrix(): Matrix4f {

        val newMat = Matrix4f()
        val eye = getWorldPosition()
        val center = getWorldPosition().sub(getWorldZAxis())
        val up = getWorldYAxis()
        return newMat.lookAt(eye, center, up)
    }

    override fun getCalculateProjectionMatrix(): Matrix4f {
        val fov = Math.toRadians(90.0).toFloat()
        val aspect = 16f / 9f
        val near = 0.1f
        val far = 300f
        val newMat = Matrix4f()

        return newMat.perspective(fov, aspect, near, far)
    }

    override fun bind(shader: ShaderProgram) {
        shader.setUniform("view_matrix", getCalculateViewMatrix(), false)
        shader.setUniform("projection_matrix", getCalculateProjectionMatrix(), false)
    }
}