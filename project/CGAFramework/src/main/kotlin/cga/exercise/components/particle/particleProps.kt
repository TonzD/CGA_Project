//package cga.exercise.components.particle
//
//import cga.exercise.components.camera.TronCamera
//import cga.exercise.components.geometry.Renderable
//import cga.exercise.components.shader.ShaderProgram
//import cga.framework.ModelLoader.loadModel
//import org.joml.Matrix4f
//import org.joml.Vector2f
//import org.joml.Vector3f
//import org.joml.Vector4f
//import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
//import org.lwjgl.opengl.GL11.*
//import kotlin.math.pow
//
//class Particle(var position:Vector3f= Vector3f(0f,0f,0f),
//                    var velocity:Vector3f= Vector3f(1f,1f,1f), var velocityVariation:Vector2f=Vector2f(0f,0f),
//                    var color:Vector4f=Vector4f(1f,0f,0f,1f),
//                    var gravity:Float=1f,
//                    var scale:Float=1f){
//
//    var model=loadModel("assets/models/untitled.obj",0f,0f,0f)
//    var life:Float =0f
//    init {
//        model!!.scale(Vector3f(30f))
//    }
//}
//
//class ParticleManager(val targetObject: Renderable?,val cam:TronCamera) {
//    val listSize=500
//    var particlesList = mutableListOf<Particle>()
//    val sizeVariation=3
//    val offset=2f
//    var lastUsedParticle=0
//
//    init {
//        fillParticlesList(cam)
//    }
//
//    fun spawnParticles(dt:Float,targetObject: Renderable?){
//        for (i in 0..sizeVariation){
//            val unUsedIndex=firstUnusedParticle()
//            respawnParticle(particlesList[unUsedIndex],targetObject,offset)
//        }
//        for (i in particlesList){
//            i.life=i.life-dt
//            if(i.life>0f){
//                val velo=i.velocity.length()
//                i.model!!.parent=targetObject
//                i.model!!.setModelMatrixUpperLeft(Matrix4f())
//                i.model!!.rotateTowards(targetObject!!.getWorldZAxis().negate(),targetObject!!.getWorldYAxis().negate())
//                i.model!!.translate(Vector3f(0f,0f,0.5f*particlesList[0].model!!.scaleFactor!!.pow(-1f)))
//          //    i.color=Vector4f(i.color.x-(dt*2.5f),i.color.x-(dt*2.5f),i.color.x-(dt*2.5f),i.color.w-(dt*i.life))
//            }
//        }
//    }
//
//    fun firstUnusedParticle():Int{
//        for(i in lastUsedParticle..listSize){
//            if(particlesList[i].life<0f){
//                lastUsedParticle=i
//                return i
//            }
//        }
//        for(i in 0..lastUsedParticle){
//            if(particlesList[i].life<0f){
//                lastUsedParticle=i
//                return i
//            }
//        }
//        lastUsedParticle=0
//        return 0
//    }
//
//    fun respawnParticle(particle: Particle,targetObject:Renderable?,offset:Float){
//        val random =( ((Math.random() % 100) - 50) / 10.0f).toFloat()
//        val rColor = 0.5f + ((Math.random() % 100) / 100.0f).toFloat()
//        particle.model!!.resetTransformations()
//       // particle.model!!.translate(Vector3f(0f,0f,0.5f))
//       // particle.color = Vector4f(rColor,rColor,rColor, 1.0f); // RGBA Werte
//        particle.life = 1.0f
//      //  particle.velocity = targetObject!!.getCalculatedVelocity().mul(0.1f)
//    }
//
//    fun render(cam:TronCamera,shader:ShaderProgram){
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE)
//        for (i in particlesList){
//            if (i.life>0f){
//                i.model!!.setModelMatrixUpperLeft(Matrix4f())
//                i.model!!.rotateTowards(cam.getWorldZAxis().negate(),cam.getWorldYAxis().negate())
//                i.model!!.rotate(Math.toRadians(90.0).toFloat(),0f,0f)
//                i.model!!.render(shader)
//            }
//        }
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
//    }
//
//    fun fillParticlesList(cam:TronCamera){
//        for (i in 0..listSize){
//            particlesList.add(0,Particle())
//            particlesList[0]!!.model!!.translate(Vector3f(0f,10*particlesList[0].model!!.scaleFactor!!.pow(-1f),5*particlesList[0].model!!.scaleFactor!!.pow(-1f)))
//
//        }
//    }
//}