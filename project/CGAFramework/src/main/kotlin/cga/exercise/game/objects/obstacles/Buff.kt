package cga.exercise.game.objects.obstacles

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Vector3f
import kotlin.random.Random

class Buff ():Obstacles(){
    override var isIdle= true
    override var nextSpawn=Random.nextInt(0, 10).toFloat()
    override val model: Renderable?
    val speed=5f
    var buffType=BuffType.NOBUFF
    init {
        model= ModelLoader.loadModel("assets/models/Parachute/parachute.obj", 0f, 0f, 0f)
        model?.scale(Vector3f(1f))
        setRandomBuffType()
    }
    override fun spawn(t:Float):Boolean{
        if(nextSpawn<=t&&isIdle) {
            isIdle = false
            return true
        }else return false
    }

    fun setRandomBuffType(){
        val ran= Random.nextInt(0, 1)
        if(ran==0) buffType=BuffType.HEAL else buffType=BuffType.DMG
    }
    override fun move(dt:Float){
        model?.translate(Vector3f(0f,0f, speed * dt))
    }
}