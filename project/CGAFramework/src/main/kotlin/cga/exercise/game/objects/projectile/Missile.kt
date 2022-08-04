package cga.exercise.game.objects.projectile

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.pow

class Missile() {
    val model: Renderable?
    var speed=100f
    val radius=10f
    var lastPosition: Vector3f
    var chargeStart=0f
    var chargeEnd=0f
    var charge=0f
    init {
        model= ModelLoader.loadModel("assets/models/Missile/Missile.obj", 0f, 0f, 0f)
        setMissileSpeed(1f)
        lastPosition=model!!.getWorldPosition()

    }
    fun calculateCharge(){
        charge=(chargeEnd-chargeStart).coerceIn(0f,1f)
    }
    fun setMissileSpeed(speedFactor:Float){
        val modelSpeedFactorInversed=model!!.scaleFactor.pow(-1)
        speed=speed*modelSpeedFactorInversed*speedFactor
    }
    fun move(dt:Float){
        model?.translate(Vector3f(0f,0f, -speed*charge* dt))
        model?.rotateWorld(-0.1f*charge.pow(-1)*dt,0f, 0f)
        model?.rotate(-0.1f*charge.pow(-1)*dt,0f, 0f)
    }
    fun getScaledRadius():Float{
        return radius*model!!.scaleFactor
    }
    fun outOfMap():Boolean{
        return!(model!!.getWorldPosition().x<=50 && model!!.getWorldPosition().x>=-50 &&
                model!!.getWorldPosition().y<=50 && model!!.getWorldPosition().y>=5 &&
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