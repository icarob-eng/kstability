package com.kstability

import com.kstabilty.*
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Class responsible for managing all the nodes contained in the appropriate section: node.
 *
 * @property isANodeSection property responsible for checking whether the passed argument is a node section.
 * @property isAllNodes property responsible for checking if the content of the section is only composed of nodes.
 * @property isAnyNode property responsible for checking whether there is at least one node in the section.
 *
 * @see Node
 * @see Knot
 * **/
class Nodes(private val arg: Map<String,Any>){

    val isANodeSection:Boolean
        get() = _isANodeSection()

    @Suppress("UNCHECKED_CAST")
    private fun _isANodeSection():Boolean {
        if(arg.entries.size!=1){
            return false
        }
        for((outerKey,outerValue) in arg as Map<String,Map<String,Any>>){
            for(innerMap in outerValue){
                if(!Node(innerMap).isNode){
                    return false
                }
            }
        }
        return true
    }

    val isAllNodes:Boolean = _isANodeSection()

    val isAnyNode:Boolean
        get() = _isAnyNode()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isAnyNode():Boolean {
        if(arg.entries.size!=1){
            return false
        }
        for((outerKey,outerValue) in arg as Map<String,Map<String,Any>>){
            for(innerMap in outerValue){
                if(Node(innerMap).isNode){
                    return true
                }
            }
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    fun makeNodes():MutableList<Knot>{
        val nodes: MutableList<Knot> = mutableListOf()
        if(this.isANodeSection){
            for((_,outerValue) in arg as Map<String,Map<String,Any>>){
                for(innerNode in outerValue){
                    Node(innerNode).makeNode()?.let { nodes.add(it) }
                }
            }
        }
        else if(this.isAnyNode){
            for((_,outerValue) in arg as Map<String,Map<String,Any>>){
                for(innerNode in outerValue){
                    if(Node(innerNode).isNode)
                    Node(innerNode).makeNode()?.let { nodes.add(it) }
                }
            }
        }
        return nodes
    }
}
/**
 * Class responsible for managing the supports contained in the appropriate section: supports
 * @property isAHolderSection property responsible for checking whether the argument passed to
 * the class is a support section.
 * @property isAllHolders property responsible for checking whether all arguments in the section are brackets.
 * @property isAnyHolder property responsible for checking if any arguments of the section are supports.
 *
 * @see Holder
 * **/

class Holders(private val arg:Map<String, Any>){

    val isAHolderSection:Boolean
        get() = _isAHolderSection()


    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isAHolderSection():Boolean{
        if(arg.entries.size!=1) {
            return false
        }
        for((_, outerValue) in arg as Map<String,Map<String,Any>>){
            for(holder in outerValue){
                if(!Holder(outerValue).isHolder){
                    return false
                }
            }
        }
        return true
    }

    val isAllHolders:Boolean
        get() = _isAllHolders()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isAllHolders():Boolean{
        for((_, outerValue) in arg as Map<String, Map<String,Any>>){
            for(holder in outerValue){
                if(!Holder(outerValue).isHolder){
                    return false
                }
            }
        }
        return true
    }

    val isAnyHolder: Boolean
        get() = _isAnyHolder()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isAnyHolder():Boolean {
        for((_, outerValue) in arg as Map<String, Map<String, Any>>){
            for(holder in outerValue){
                if(!Holder(outerValue).isHolder){
                    return false
                }
            }
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class, IllegalArgumentException::class)
    fun makeSupports(knots: MutableList<Knot>):MutableList<Support>?{
        val supports:MutableList<Support> = mutableListOf()
        if(this.isAHolderSection){
            for((_,outerValue) in arg as Map<String,Map<String,Any>>){
                for(holder in outerValue){
                    Holder(holder).makeSupport(knots)?.let { supports.add(it) }
                }
            }
        }
        else{
            throw IllegalArgumentException("O argumento da classe não é uma seção de suportes.")
        }
        return null
    }

}
/**
 * Class responsible for managing the construction and verification of bars according to the argument passed to it.
 * @property isABeamSection property responsible for checking whether the argument passed to the class
 * constitutes a section of slashes.
 *
 * @see Beam
 * **/
class Beams(private val arg: Map<String,Any>){

    val isABeamSection:Boolean
        get() = _isABeamSection()

    private fun _isABeamSection():Boolean{
        return !(arg.entries.size !=1 || arg.entries.first().key!="barras")
    }

    fun isValidListOfBeams(knots:MutableList<Knot>): Boolean {
        for (beam in arg.entries.first().value as ArrayList<*>) {
            if (!Beam(beam as ArrayList<*>).isValidBeam(knots)) {
                return false
            }
        }
        return true
    }

    @Throws(IllegalArgumentException::class)
    fun makeBars(knots:MutableList<Knot>):List<Bar>{
        val out:MutableList<Bar> = mutableListOf()
        if(this.isValidListOfBeams(knots)){
            for(beam in arg.entries.first().value as ArrayList<*>)
                Beam(beam as ArrayList<*>).makeBar(knots)?.let { out.add(it) }
        }
        else{
            throw IllegalArgumentException("A lista de barras é inválida.")
        }
        return out
    }
}
/**
 * Class responsible for managing the construction and verification of Loads according to the argument passed to it.
 * @property isALoadSection property responsible for checking whether the argument passed to the class
 * constitutes a section of loads.
 *  **/
class Loads(private val arg: Map<String,Any>){

    val isALoadSection:Boolean
        get() = _isALoadSection()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isALoadSection():Boolean{
        if(arg.entries.size!=1 && arg["cargas"]==null){
            return false
        }
        for((_, outerValue) in arg as Map<String,Map<String, Any>>){
            for(load in outerValue){
                if(!Load(load).isLoad){
                    return false
                }
            }
        }
        return true
    }

    val isNotALoadSection: Boolean
        get() = !_isALoadSection()


    val isAllLoads: Boolean
        get() = _isAllLoads()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isAllLoads():Boolean{
        for((_, outerValue) in arg as Map<String,Map<String, Any>>){
            for(load in outerValue){
                if(Load(load).isLoad){
                    return false
                }
            }
        }
        return true
    }

    val isAnyALoad:Boolean
        get() = _isAnyLoad()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isAnyLoad():Boolean{
        for((_, outerValue) in arg as Map<String,Map<String, Any>>){
            for(load in outerValue){
                if(Load(load).isLoad){
                    return true
                }
            }
        }
        return false
    }

}
/**
 * General class responsible for doing the whole process and handling the other classes.
 * **/
class Parse(path:String){
    val data: Map<String,Any> = Yaml().load(File(path).inputStream())

    fun getProjectName(): String? {
        if(data["projeto"]!=null && data["projeto"] is String){
            return data["projeto"] as String
        }
        return null
    }
}