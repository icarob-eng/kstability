package com.moon.kstability

import com.moon.kstability.*
import com.moon.kstability.parsing.StringsPtBr
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Class responsible for managing all the nodes contained in the appropriate section: node.
 *
 * @property isANodeSection property responsible for checking whether the passed argument is a node section.
 * @property isAllNodes property responsible for checking if the content of the section is only composed of nodes.
 * @property isAnyNode property responsible for checking whether there is at least one node in the section.
 *
 * @suppress UNCHECKED_CAST
 * @throws ClassCastException
 *
 * @see NodeCreator
 * @see Node
 * **/
class NodesManager(private val arg: Map<String,Any>){

    constructor(entry:Map.Entry<String,Any>):this(mapOf(entry.toPair()))

    val isANodeSection:Boolean
        get() = _isANodeSection()

    @Suppress("UNCHECKED_CAST")
    private fun _isANodeSection():Boolean {
        if(arg.entries.size!=1 || arg.entries.first().value !is Map<*, *>){
            return false
        }
        if(!(arg.entries.first().value as Map<*,*>).keys.all { it is String }){
            return false
        }
        for((outerKey,outerValue) in arg as Map<String,Map<String,Any>>){
            for(innerMap in outerValue){
                if(!NodeCreator(innerMap).isNode){
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
        if(arg.entries.size!=1 || arg.entries.first().value !is Map<*, *>){
            return false
        }
        if(!(arg.entries.first().value as Map<*,*>).all { it.key is String }){
            return false
        }
        for((outerKey,outerValue) in arg as Map<String,Map<String,Any>>){
            for(innerMap in outerValue){
                if(NodeCreator(innerMap).isNode){
                    return true
                }
            }
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    fun createAllNodes():MutableList<Node>{
        val nodes: MutableList<Node> = mutableListOf()
        if(this.isANodeSection){
            for((_,outerValue) in arg as Map<String,Map<String,Any>>){
                for(innerNode in outerValue){
                    NodeCreator(innerNode).createNode()?.let { nodes.add(it) }
                }
            }
        }
        else if(this.isAnyNode){
            for((_,outerValue) in arg as Map<String,Map<String,Any>>){
                for(innerNode in outerValue){
                    if(NodeCreator(innerNode).isNode)
                    NodeCreator(innerNode).createNode()?.let { nodes.add(it) }
                }
            }
        }
        return nodes
    }
}
/**
 * Class responsible for managing the supports contained in the appropriate section: supports
 * @property isASupportSection property responsible for checking whether the argument passed to
 * the class is a support section.
 * @property isAllSupports property responsible for checking whether all arguments in the section are brackets.
 * @property isAnySupport property responsible for checking if any arguments of the section are supports.
 *
 * @suppress UNCHECKED_CAST
 * @throws ClassCastException
 * @throws IllegalArgumentException
 *
 * @see SupportCreator
 * **/

class SupportsManager(private val arg:Map<String, Any>){

    val isASupportSection:Boolean
        get() = _isASupportSection()


    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isASupportSection():Boolean{
        if(arg.entries.size!=1 || arg.entries.first().value !is Map<*,*>) {
            return false
        }
        if(!(arg.entries.first().value as Map<*,*>).keys.all{it is String}){
            return false
        }
        if(!(arg.entries.first().value as Map<*,*>).values.all{it is Map<*,*>}){
            return false
        }
        for((_, outerValue) in arg as Map<String,Map<String,Any>>){
            for(support in outerValue){
                if(!SupportCreator(outerValue).isSupport){
                    return false
                }
            }
        }
        return true
    }

    val isAllSupports:Boolean
        get() = _isAllSupports()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isAllSupports():Boolean{
        if(arg.entries.size!=1 || arg.entries.first().value !is Map<*,*>) {
            return false
        }
        if(!(arg.entries.first().value as Map<*,*>).keys.all { it is String } || !(arg.entries.first().value as Map<*,*>).values.all { it is Map<*,*> }){
            return false
        }
        for((_, outerValue) in arg as Map<String, Map<String,Any>>){
            for(support in outerValue){
                if(!SupportCreator(support).isSupport){
                    return false
                }
            }
        }
        return true
    }

    val isAnySupport: Boolean
        get() = _isAnySupport()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isAnySupport():Boolean {
        if(arg.entries.size!=1 || arg.entries.first().value !is Map<*,*>) {
            return false
        }
        for((_, outerValue) in arg){
            if(outerValue is Map<*,*> && outerValue.keys.all { it is String } && outerValue.values.all { it is Map<*,*> }){
                for(support in outerValue as Map<String, Map<String, Any>>){
                    if(SupportCreator(outerValue).isSupport){
                        return true
                    }
                }
            }
            else{
                continue
            }
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class, IllegalArgumentException::class)
    fun createAllSupports(nodes: MutableList<Node>):MutableList<Support>?{
        val supports:MutableList<Support> = mutableListOf()
        if(this.isASupportSection){
            for((_,outerValue) in arg as Map<String,Map<String,Any>>){
                for(support in outerValue){
                    SupportCreator(support).createSupport(nodes)?.let { supports.add(it) }
                }
            }
        }
        else{
            throw IllegalArgumentException(StringsPtBr.invalidSupportList)
        }
        return null
    }

}
/**
 * Class responsible for managing the construction and verification of beams according to the argument passed to it.
 * @property isABeamSection property responsible for checking whether the argument passed to the class
 * constitutes a section of slashes.
 *
 * @throws ClassCastException
 *
 * @see BeamCreator
 * **/
class BeamsManager(private val arg: Map<String,Any>){

    val isABeamSection:Boolean
        get() = _isABeamSection()

    private fun _isABeamSection():Boolean{
        return !(arg.entries.size !=1 || arg.entries.first().key!= StringsPtBr.beamsSection)
    }

    fun isValidListOfBeams(nodes:MutableList<Node>): Boolean {
        if(!(arg.entries.first().value is ArrayList<*> && (arg.entries.first().value as ArrayList<*>).all { it is String })){
            return false
        }
        for (beam in arg.entries.first().value as ArrayList<*>) {
            if (!BeamCreator(beam as ArrayList<*>).isValidBeam(nodes)) {
                return false
            }
        }
        return true
    }

    @Throws(IllegalArgumentException::class)
    fun createAllBeams(nodes:MutableList<Node>):List<Beam>{
        val out:MutableList<Beam> = mutableListOf()
        if(this.isValidListOfBeams(nodes)){
            for(beam in arg.entries.first().value as ArrayList<*>)
                BeamCreator(beam as ArrayList<*>).createBeam(nodes).let { out.add(it) }
        }
        else{
            throw IllegalArgumentException(StringsPtBr.invalidBeamList)
        }
        return out
    }
}
/**
 * Class responsible for managing the construction and verification of Loads according to the argument passed to it.
 *
 * @suppress UNCHECKED_CAST
 * @throws ClassCastException
 * 
 * @property isALoadSection property responsible for checking whether the argument passed to the class
 * constitutes a section of loads.
 *  **/
class PointLoadsManager(private val arg: Map<String,Any>){

    val isALoadSection:Boolean
        get() = _isALoadSection()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isALoadSection():Boolean{
        if(arg.entries.size!=1 || arg[StringsPtBr.loadsSection]==null){
            return false
        }
        for((_, outerValue) in arg as Map<String,Map<String, Any>>){
            for(load in outerValue){
                if(!PointLoadCreator(load).isLoad){
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
        if(arg.entries.size!=1 || arg[StringsPtBr.loadsSection]==null){
            return false
        }
        if(arg.entries.first().value !is Map<*,*> || !(arg.entries.first().value as Map<*,*>).keys.all { it is String }  || !(arg.entries.first().value as Map<*,*>).values.all { it is Map<*,*> }){
            return false
        }
        for((_, outerValue) in arg as Map<String,Map<String, Any>>){
            for(load in outerValue){
                if(!PointLoadCreator(load).isLoad){
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
        if(arg.entries.size!=1){
            return false
        }
        for((_, outerValue) in arg){
            if(outerValue is Map<*,*> && outerValue.keys.all { it is String } && outerValue.values.all { it is Any }){
                for(load in outerValue as Map<String, Any>){
                    try {
                        if (PointLoadCreator(load).isLoad) {
                            return true
                        }
                    }
                    catch (_:Exception){
                        continue
                    }
                }
            }
        }
        return false
    }

}
/**
 * General class responsible for doing the whole process and handling the other classes.
 * **/
class Parser(path:String){
    val data: Map<String,Any> = Yaml().load(File(path).inputStream())

    fun getProjectName(): String? {
        if(data[StringsPtBr.structureSection]!=null && data[StringsPtBr.structureSection] is String){
            return data[StringsPtBr.structureSection] as String
        }
        return null
    }
}