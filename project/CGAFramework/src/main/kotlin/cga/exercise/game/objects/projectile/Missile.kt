package cga.exercise.game.objects.projectile

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Vector3f
import kotlin.math.pow

class Missile() {
    val model: Renderable?
    var speed=100f
    val radius=10f
    init {
        model= ModelLoader.loadModel("assets/models/Missile/Missile.obj", 0f, 0f, 0f)
        setMissileSpeed(1f)

    }

    fun setMissileSpeed(speedFactor:Float){
        val modelSpeedFactorInversed=model!!.scaleFactor.pow(-1)
        speed=speed*modelSpeedFactorInversed*speedFactor
    }
    fun move(dt:Float){
        model?.translate(Vector3f(0f,0f, -speed * dt))
        model?.rotateWorld(-0.1f*dt,0f, 0f)
        model?.rotate(-0.1f*dt,0f, 0f)
    }
    fun getScaledRadius():Float{
        println("Missile radius"+radius*model!!.scaleFactor)
        return radius*model!!.scaleFactor
    }
    fun outOfMap():Boolean{
        return!(model!!.getWorldPosition().x<=50 && model!!.getWorldPosition().x>=-50 &&
                model!!.getWorldPosition().y<=50 && model!!.getWorldPosition().y>=0 &&
                model!!.getWorldPosition().z<=50 && model!!.getWorldPosition().z>=-50)
    }
    fun checkCollision(targetRadius:Float, targetWorldPosition:Vector3f):Boolean{
        val ownWorldPosition=model!!.getWorldPosition()
        val distance = Math.sqrt(
            (ownWorldPosition.x - targetWorldPosition.x) * (ownWorldPosition.x - targetWorldPosition.x) +
                    (ownWorldPosition.y - targetWorldPosition.y) * (ownWorldPosition.y - targetWorldPosition.y) +
                    (ownWorldPosition.z - targetWorldPosition.z) * (ownWorldPosition.z - targetWorldPosition.z).toDouble())
        return distance < (getScaledRadius() + targetRadius)
    }
}