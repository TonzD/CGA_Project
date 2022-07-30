import cga.exercise.components.geometry.Renderable
import cga.exercise.game.objects.obstacles.Obstacles
import cga.exercise.game.objects.obstacles.ShipType
import cga.framework.ModelLoader.loadModel
import org.joml.Vector3f
import kotlin.random.Random

class Ship(val type: ShipType,val spawnX: Float) : Obstacles () {
    override var isIdle= true
    override var nextSpawn= Random.nextInt(0, 5).toFloat()
    override val model:Renderable?
    var speed=Random.nextInt(5,10).toFloat()
    var side=Random.nextInt(0, 100)
    init {
        if(type==ShipType.SAIL) {
            model=loadModel(type.toString(),0f,Math.toRadians(90.0).toFloat(),0f)
        }else{
            model=loadModel(type.toString(),0f,Math.toRadians(180.0).toFloat(),0f)
        }
    }
    override fun spawn(t:Float):Boolean{
       if((nextSpawn<=t)&&isIdle) {
           isIdle = false
           setRandomSpawnSide()
           setRandomSpeed()
           return true
       }else return false
    }

    override fun move(dt:Float){
        if(type==ShipType.SAIL&&side<=50){
            model?.translate(Vector3f(0f,0f, speed * dt))
        }else{
            model?.translate(Vector3f(0f, 0f, -speed * dt))
        }
    }

    fun setRandomSpawnSide(){
        side=Random.nextInt(0, 100)
        if(side<=50&&type==ShipType.CARGO){
            model?.resetTransformations()
            model?.rotate(0f,Math.toRadians(-90.0).toFloat(),0f)
            model?.translate(Vector3f(spawnX,0f,50f))
            model?.scale(Vector3f(4f))
        }
        if(side>50&&type==ShipType.CARGO){
            model?.resetTransformations()
            model?.rotate(0f,Math.toRadians(90.0).toFloat(),0f)
            model?.translate(Vector3f(spawnX,0f,50f))
            model?.scale(Vector3f(4f))
        }
        if(side<=50&&type==ShipType.SAIL) {
            model?.resetTransformations()
            model?.rotate(0f, Math.toRadians(270.0).toFloat(), 0f)
            model?.translate(Vector3f(spawnX,0f,-50f))
            model?.scale(Vector3f(2f))
        }
        if(side>50&&type==ShipType.SAIL){
            model?.resetTransformations()
            model?.rotate(0f, Math.toRadians(270.0).toFloat(), 0f)
            model?.translate(Vector3f(spawnX,0f,50f))
            model?.scale(Vector3f(2f))
        }
    }
    fun setRandomSpeed(){
        speed=Random.nextInt(5,10).toFloat()
    }
}