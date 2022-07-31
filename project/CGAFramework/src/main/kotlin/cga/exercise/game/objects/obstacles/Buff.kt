package cga.exercise.game.objects.obstacles

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Vector3f
import kotlin.random.Random

class Buff ():Obstacles(){
    override var isIdle= true
    override var nextSpawn=Random.nextInt(0, 10).toFloat()
    override val model: Renderable?
    override var radius = 1f
    override var speed=1f
    var side=Random.nextInt(0, 100)
    val altitude=20f
    var spawnX=Random.nextInt(0, 100).toFloat()
    var spawnZ=Random.nextInt(0, 100).toFloat()


    var buffType=BuffType.NOBUFF

    init {
        model= ModelLoader.loadModel("assets/models/Parachute/parachute.obj", 0f, 0f, 0f)
        model?.scale(Vector3f(3f))
        setRandomBuffType()
    }

    override fun setRandomSpawnSide(){
        side=Random.nextInt(0, 100)
        if(side<=50){
            model?.resetTransformations()
            model?.rotate(0f,Math.toRadians(-90.0).toFloat(),0f)
            model?.translate(Vector3f(-spawnX,altitude,spawnZ))
            model?.scale(Vector3f(1f))
        }
        if(side>50){
            model?.resetTransformations()
            model?.rotate(0f,Math.toRadians(90.0).toFloat(),0f)
            model?.translate(Vector3f(spawnX,altitude,spawnZ))
            model?.scale(Vector3f(1f))
        }
    }

    fun setRandomBuffType(){
        val ran= Random.nextInt(0, 1)
        if(ran==0) buffType=BuffType.HEAL else buffType=BuffType.DMG
    }

    override fun move(dt:Float){
        model?.translate(Vector3f(0f,-speed * dt,0f))
    }

    override fun setRanSpeed(){}
}