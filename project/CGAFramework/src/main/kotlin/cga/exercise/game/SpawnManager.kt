package cga.exercise.game

import Plane
import Ship
import cga.exercise.components.geometry.Renderable
import cga.exercise.game.objects.obstacles.Buff
import cga.exercise.game.objects.obstacles.Obstacles
import cga.exercise.game.objects.obstacles.ShipSmall
import org.joml.Vector3f
import kotlin.random.Random

class SpawnManager() {
    val obstacleList= mutableListOf<Obstacles>()

    val shipS1= ShipSmall(-10f)
    val shipS2=ShipSmall(10f)
    val shipL=Ship(0f)
    val plane1=Plane(-10f)
    val plane2=Plane(10f)
    val buff=Buff()


    fun spawn(t:Float){
        if(shipS1.spawn(t))obstacleList.add(shipS1)
        if(shipS2.spawn(t))obstacleList.add(shipS2)
        if(shipL.spawn(t))obstacleList.add(shipL)
        if(plane1.spawn(t))obstacleList.add(plane1)
        if(plane2.spawn(t))obstacleList.add(plane2)
        if(buff.spawn(t))obstacleList.add(buff)
    }

    fun move(dt:Float,t:Float){
        shipS1.move(dt)
        shipS2.move(dt)
        shipL.move(dt)
        plane1.move(dt)
        plane2.move(dt)
        buff.move(dt,t)
    }

    fun removeInvisible(t:Float){
        if(obstacleList.isNotEmpty()) {
            for (i in obstacleList) {
                if (i.isInvisible()) {
                    i.isIdle = true
                    i.nextSpawn = t + Random.nextInt(3, 5).toFloat()
                    obstacleList.remove(i)
                    removeInvisible(t)
                    break
                }
            }
        }
    }

    fun checkMissileCollision(missileRadius:Float,missileWorldPosition:Vector3f):Boolean{
        for (i in obstacleList){
           if(i.checkCollision(missileRadius,missileWorldPosition)) return true
        }
        return false
    }

    fun getRenderList():MutableList<Renderable?>{
        val renderlist=mutableListOf<Renderable?>()
        for (i in obstacleList){
            renderlist.add(i.model)
        }
        return renderlist
    }

}