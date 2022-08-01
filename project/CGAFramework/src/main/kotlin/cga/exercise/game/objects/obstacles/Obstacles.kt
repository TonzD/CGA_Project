package cga.exercise.game.objects.obstacles

import cga.exercise.components.geometry.Renderable
import org.joml.Vector3f
import kotlin.math.pow
import kotlin.random.Random

abstract class Obstacles (){
    abstract var isIdle:Boolean
    abstract var nextSpawn:Float
    abstract val model:Renderable?
    abstract val radius:Float
    abstract var speed:Float
    abstract val speedA:Int
    abstract val speedB:Int

    fun spawn(t:Float):Boolean{
        if(nextSpawn<=t&&isIdle) {
            setRandomSpawnSide()
            setRanSpeed()
            isIdle = false
            return true
        }else return false
    }
    abstract fun setRandomSpawnSide()

    open fun move(dt:Float){
        model?.translate(Vector3f(0f, 0f, -speed * dt))
    }
    fun isInvisible():Boolean{
        return !(model!!.getWorldPosition()!!.x>-60
                &&model!!.getWorldPosition()!!.x<60
                &&model!!.getWorldPosition()!!.y>-60
                &&model!!.getWorldPosition()!!.y<60
                &&model!!.getWorldPosition()!!.z>-60
                &&model!!.getWorldPosition()!!.z<=60)
    }
    fun getScaledRadius():Float{
        println("radius"+radius*model!!.scaleFactor)
        return radius*model!!.scaleFactor
    }
    fun checkCollision(targetRadius:Float, targetWorldPosition:Vector3f):Boolean{
        val ownWorldPosition=model!!.getWorldPosition()
        val distance = Math.sqrt(
                    (ownWorldPosition.x - targetWorldPosition.x) * (ownWorldPosition.x - targetWorldPosition.x) +
                    (ownWorldPosition.y - targetWorldPosition.y) * (ownWorldPosition.y - targetWorldPosition.y) +
                    (ownWorldPosition.z - targetWorldPosition.z) * (ownWorldPosition.z - targetWorldPosition.z).toDouble())
        return distance < (getScaledRadius() + targetRadius)
    }
    open fun setRanSpeed() {
        speed=Random.nextInt(speedA,speedB).toFloat()*model!!.scaleFactor.pow(-1)
    }
}