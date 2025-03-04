package cga.exercise.game.objects.obstacles

import cga.exercise.components.geometry.Renderable
import cga.exercise.game.objects.obstacles.Obstacles
import cga.framework.ModelLoader.loadModel
import org.joml.Vector3f
import kotlin.random.Random

class ShipSmall(val spawnX: Float) : Obstacles () {
    override var isIdle= true
    override var nextSpawn= Random.nextInt(0, 5).toFloat()
    override val model:Renderable?
    override var radius = 4f
    override var speed=0f
    override var speedA = 5
    override var speedB = 10
    var side=0
    init {
        model=loadModel("assets/models/LowPolyWoodSail/LowPolyWoodSail.obj",0f,Math.toRadians(90.0).toFloat(),0f)
        setRanSpeed()
        setRandomSpawnSide()
    }

    override fun move(dt:Float){
            model?.translate(Vector3f(0f, 0f, -speed * dt))
    }

    override fun setRandomSpawnSide(){
        side=Random.nextInt(0, 100)
        if(side<=50) {
            model?.resetTransformations()
            model?.rotate(0f, Math.toRadians(90.0).toFloat(), 0f)
            model?.translate(Vector3f(-spawnX,1.5f,50f))
            model?.scale(Vector3f(2f))
        }
        if(side>50){
            model?.resetTransformations()
            model?.rotate(0f, Math.toRadians(-90.0).toFloat(), 0f)
            model?.translate(Vector3f(spawnX, 1.5f,50f))
            model?.scale(Vector3f(2f))
        }
    }

}