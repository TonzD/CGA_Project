package cga.exercise.game.objects.obstacles

import cga.exercise.components.geometry.Renderable

abstract class Obstacles (){
    abstract var isIdle:Boolean
    abstract var nextSpawn:Float
    abstract val model:Renderable?
    abstract fun spawn(t:Float):Boolean
    abstract fun move(dt:Float)
    fun isInvisible():Boolean{
        return !(model!!.getWorldPosition()!!.x>-60
                &&model!!.getWorldPosition()!!.x<60
                &&model!!.getWorldPosition()!!.y>-60
                &&model!!.getWorldPosition()!!.y<60
                &&model!!.getWorldPosition()!!.z>-60
                &&model!!.getWorldPosition()!!.z<=60)
    }
}