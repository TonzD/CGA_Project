import cga.exercise.components.geometry.Renderable
import cga.exercise.game.objects.obstacles.Obstacles
import cga.exercise.game.objects.obstacles.ShipType
import cga.framework.ModelLoader.loadModel
import org.joml.Vector3f
import kotlin.random.Random

class Plane(val spawnX:Float) : Obstacles() {
    override var isIdle= true
    override val model:Renderable?
    override var nextSpawn= Random.nextInt(0, 5).toFloat()

    var speed=Random.nextInt(20,25).toFloat()
    var side=Random.nextInt(0, 100)

    init {
        model=loadModel("assets/models/Plane/WW2-Plane-LowPoly.obj",0f,0f,0f)

    }
    override fun spawn(t:Float):Boolean{
        if(nextSpawn>=t&&isIdle) {
            setRandomSpawnSide()
            setRandomSpeed()
            isIdle = false
            return true
        }else return false
    }

    fun setRandomSpawnSide(){
        side=Random.nextInt(0, 100)
        println(side)
        if(side<=50){
            model?.resetTransformations()
            model?.translate(Vector3f(spawnX,0f,50f))
            model?.rotate(0f,Math.toRadians(-90.0).toFloat(),0f)
            model?.translate(Vector3f(0f,0f,50f))
            model?.scale(Vector3f(1f))
        }
        if(side>50){
            model?.resetTransformations()
            model?.translate(Vector3f(spawnX,0f,50f))
            model?.rotate(0f,Math.toRadians(90.0).toFloat(),0f)
            model?.translate(Vector3f(0f,0f,50f))
            model?.scale(Vector3f(1f))
        }
    }

    override fun move(dt:Float){
        model?.translate(Vector3f(0f,0f, -speed * dt))
    }

    fun setRandomSpeed(){
        speed=Random.nextInt(20,25).toFloat()
    }

}