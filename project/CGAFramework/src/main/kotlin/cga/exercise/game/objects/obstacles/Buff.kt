package cga.exercise.game.objects.obstacles

import cga.exercise.components.geometry.Renderable
import cga.framework.ModelLoader
import org.joml.Vector3f
import kotlin.math.pow
import kotlin.random.Random

class Buff ():Obstacles(){
    override var isIdle= true
    override var nextSpawn=Random.nextInt(0, 10).toFloat()
    override val model: Renderable?
    override var radius = 1f
    override var speed=1f
    override var speedA =0
    override var speedB= 0
    var side=Random.nextInt(0, 100)
    var altitude=30f
    var spawnX=Random.nextInt(0, 100).toFloat()
    var spawnZ=Random.nextInt(0, 100).toFloat()
    var lifeTime=0f
    var currentTime=0f
    var deSpawn=false
    var buffType=BuffType.NOBUFF

    init {
        model= ModelLoader.loadModel("assets/models/Parachute/parachute.obj", 0f, 0f, 0f)
        model?.scale(Vector3f(3f))
        setRandomBuffType()
        setRandomSpawnSide()
    }

    override fun setRandomSpawnSide(){
        side=Random.nextInt(0, 100)
        if(side<=50){
            spawnX=Random.nextInt(-50, 50).toFloat()
            spawnZ=Random.nextInt(-50, -30).toFloat()
            model?.resetTransformations()
            model?.translate(Vector3f(-spawnX,altitude,spawnZ))
            model?.rotate(0f,Math.toRadians(-90.0).toFloat(),0f)
            model?.scale(Vector3f(3f))
        }
        if(side>50){
            spawnX=Random.nextInt(-50, 50).toFloat()
            spawnZ=Random.nextInt(30, 50).toFloat()
            model?.resetTransformations()
            model?.translate(Vector3f(spawnX,altitude,spawnZ))
            model?.rotate(0f,Math.toRadians(90.0).toFloat(),0f)
            model?.scale(Vector3f(3f))
        }
    }
    override fun spawn(t:Float):Boolean{
        if(nextSpawn<=t&&isIdle) {
            setRandomSpawnSide()
            setRanSpeed()
            lifeTime=t+15f
            isIdle = false
            return true
        }else return false
    }
    fun setRandomBuffType(){
        val ran= Random.nextInt(0, 100)
        if(ran<=50) buffType=BuffType.HEAL else buffType=BuffType.DMG
    }
    fun falling():Boolean{
        return model!!.getWorldPosition().y in 6.0..50.0
    }
    fun move(dt:Float,t:Float){
        if(lifeTime<t)deSpawn=true
        if (falling()) model?.translate(Vector3f(0f,-speed * dt,0f))
    }

    override fun isInvisible():Boolean{
        if(deSpawn){
            model!!.resetTransformations()
            setRandomBuffType()
            deSpawn=false
            return true
        }
        else return false
    }

    override fun setRanSpeed(){}
}