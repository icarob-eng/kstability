package com.kstability

import com.kstabilty.*
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Class responsible for converting a node, and its respective properties,
 * from YAML to an object in Kotlin.
 * @property isNode property responsible for indicating whether the argument
 * passed to the class is, preliminarily, a node.
 *
 * @see Knot
 * @see Vector
 * **/
@Suppress("UNCHECKED_CAST")
class Node(private val arg:Map<String,Any> = mapOf()){

    constructor(entry: Map.Entry<String,Any>) : this(mapOf(entry.toPair()))

    val isNode:Boolean
        get() = _isNode()
    private fun _isNode(): Boolean{
        if(arg.keys.size!=1){
            return false
        }
        if(arg.values.all {it is String && (it.lowercase() =="vertical" || it.lowercase()=="horizontal")}){
            return true
        }
        else if(arg.values.all {it is Map<*,*> } && arg.values.all{ it -> (it as Map<*, *>).values.all { it is Number }
                    && (it as Map<*,*>).keys.all { it == "x" || it=="y" }}){
            return true
        }
        else if(arg.values.all { it -> (it is Array<*>) && it.all { it is Number } }){
            return true
        }
        else if(arg.values.all{ it -> (it is ArrayList<*>) && it.all { it is Number }}){
            return true
        }
        return false
    }

    fun makeNode():Knot? {
        if(this.isNode){
            if(arg.values.all {it is String && (it.lowercase() =="vertical" || it.lowercase()=="horizontal")}){
                val name:String = arg.entries.first().key
                val vector = Vector(arg.entries.first().value as String)
                return Knot(name,vector)
            }
            else if(arg.values.all {it is Map<*,*> } &&
                arg.values.all{ it -> (it as Map<String,Any>).values.all { it is Number }}){
                val name:String = arg.entries.first().key
                val coordinatesNotation:Map<String,Any> = arg.entries.first() as Map<String,Any>
                val vector = Vector(coordinatesNotation["x"] as Float,coordinatesNotation["y"] as Float)
                return Knot(name,vector)
            }
            else if(arg.values.all { it -> it is Array<*> && it.all { it is Number }}){
                val name:String = arg.entries.first().key
                val listNotation: Array<Number> = arg.entries.first().value as Array<Number>
                val vector = Vector(listNotation)
                return Knot(name,vector)

            }
            else if(arg.values.all{ it -> (it is ArrayList<*>) && it.all { it is Number }}){
                val name:String = arg.entries.first().key
                val lisNotation: ArrayList<Number> = arg.entries.first().value as ArrayList<Number>
                val vector = Vector(lisNotation)
                return Knot(name, vector)
            }
            else{
                throw IllegalArgumentException("Tipo de notação inválida.")
            }
        }
        return null
    }
}

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
@Suppress("UNCHECKED_CAST")
class Nodes(private val arg: Map<String,Any>){

    val isANodeSection:Boolean
        get() = _isANodeSection()
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
 * Class responsible for managing a support, regarding the argument passed to it.
 * @property isHolder property responsible for indicating, preliminarily, if the
 * argument passed to the class is a support.
 *
 * @see Support
 * @see Knot
 * @see Vector
 *
 * **/
@Suppress("UNCHECKED_CAST")
class Holder(private val arg:Map<String,Any>){

    constructor(entry: Map.Entry<String, Any>): this(mapOf(entry.toPair()))

    val isHolder:Boolean
        get() = _isHolder()

    private fun _isHolder():Boolean{
        if(arg.entries.size!=1){
            return false
        }
        return arg.values.all{ it -> (it as Map<*, *>).values.all { it == 1 || it == 2 || it == 3 || it =="vertical" ||
                it =="horizontal" || (it is ArrayList<*> && it.all { it is Number })}}
    }
    fun makeSupport(knots:MutableList<Knot>): Support? {
        val holder:Map<String, Any> = (arg.entries.first().value as Map<String,Map<String, Any>>)
        if(this.isHolder){
            if(holder["direção"] is String){
                return if(holder["gênero"]==1){
                    knots.find{it.name==arg.entries.first().key}
                        ?.let { Support(knot = it, gender = Support.Gender.FIRST,
                            dir = Vector(holder["direção"] as String)) }
                } else if(holder["gênero"]==2){
                    knots.find{it.name==arg.entries.first().key}
                        ?.let { Support(knot = it, gender = Support.Gender.SECOND,
                            dir = Vector(holder["direção"] as String)) }
                } else{
                    knots.find{it.name==arg.entries.first().key}
                        ?.let { Support(knot = it, gender = Support.Gender.THIRD,
                            dir = Vector(holder["direção"] as String)) }
                }
            }
            else{
                return if(holder["gênero"]==1){
                    knots.find{it.name==arg.entries.first().key}
                        ?.let { Support(knot = it, gender = Support.Gender.FIRST,
                            dir = Vector(holder["direção"] as ArrayList<Number>)) }
                } else if(holder["gênero"]==2){
                    knots.find{it.name==arg.entries.first().key}
                        ?.let { Support(knot = it, gender = Support.Gender.SECOND,
                            dir = Vector(holder["direção"] as ArrayList<Number>)) }
                } else{
                    knots.find{it.name==arg.entries.first().key}
                        ?.let { Support(knot = it, gender = Support.Gender.THIRD,
                            dir = Vector(holder["direção"] as ArrayList<Number>)) }
                }
            }
        }
        else {
            throw IllegalArgumentException("Nó não declarado.")
        }
        return null
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

@Suppress("UNCHECKED_CAST")
class Holders(private val arg:Map<String, Any>){

    val isAHolderSection:Boolean
        get() = _isAHolderSection()

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
 * Class responsible for managing the construction and evaluation of a slash candidate argument.
 *
 * @property isBeam property responsible for preliminarily checking whether the argument is a slash.
 * @property isValidBeam Property responsible for checking if the argument passed is a valid slash
 * based on the nodes on the file.
 *
 * @see Bar
 * **/

class Beam(private val beam:ArrayList<*>){

    val isBeam:Boolean
        get() = _isBeam()

    private fun _isBeam():Boolean{
        return beam.all { it is String }
    }

    fun isValidBeam(knots:MutableList<Knot>):Boolean{
        for(name in beam){
            if(name !in knots.map { it.name }){
                return false
            }
        }
        return true
    }

    fun makeBar(knots:MutableList<Knot>): Bar? {
        if(this.isBeam && this.isValidBeam(knots)){
            return Bar(knot1 = knots.find { it.name == beam.get(0) }!!,
                knot2 = knots.find { it.name == beam.get(1) }!!)
        }
        else{
            throw IllegalArgumentException("As barras informadas são inválidas.")
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
 * Class responsible for managing the argument passed to it regarding loads.
 * @property isLoad checks if the argument passed to the class is a payload.
 *
 * @see DistributedLoad
 * @see PointLoad
 * **/
@Suppress("UNCHECKED_CAST")
class Load(private val arg: Map<String, Any>){

    constructor(arg:Map.Entry<String,Any>):this(mapOf(arg.toPair()))

    val isLoad: Boolean
        get() = _isLoad()

    private fun _isLoad():Boolean{
        if(arg.entries.size!=1){
            return false
        }
        val content:Map<String, Any> = arg.entries.first().value as Map<String,Any>
        return content.keys.containsAll(setOf("nó","direção","módulo")) || content.keys.containsAll(setOf("nó","vetor"))
    }

    fun makeLoad(knots: MutableList<Knot>): Any? {
        if(this.isLoad){
            val content = arg.entries.first().value as Map<String, Any>
            if(content.keys.containsAll(setOf("nó","direção","módulo")) || content["no"] is ArrayList<*>){
                val knotsNames = (content["no"] as ArrayList<String>)
                if(knotsNames.size!=2){
                    throw Exception("Número de nós inválidos.")
                }
                return DistributedLoad(knot1 = knots.find { it.name == knotsNames[0]}!!,
                    knot2 = knots.find { it.name == knotsNames[1]}!!,
                    vector = Vector(content["direção"] as String)*(content["módulo"] as Float))
            }
            else if(content.keys.containsAll(setOf("nó","direção","módulo")) || content["no"] is String){
                return PointLoad(knot = knots.find { it.name == content["nó"]}!!,
                    vector=Vector(content["direção"] as String)*(content["módulo"] as Float))
            }
            else if(content.keys.containsAll(setOf("nó","vetor")) || content["no"] is ArrayList<*>){
                val knotsNames = (content["no"] as ArrayList<String>)
                if(knotsNames.size!=2){
                    throw Exception("Número de nós inválidos.")
                }
                return DistributedLoad(knot1 = knots.find { it.name == knotsNames[0]}!!,
                    knot2 = knots.find { it.name == knotsNames[1]}!!, Vector(content["vetor"] as ArrayList<Number>))
            }
            else if(content.keys.containsAll(setOf("nó","vetor")) || content["no"] is String){
                return PointLoad(knot = knots.find { it.name == content["nó"]}!!,
                    vector=Vector(content["vetor"] as ArrayList<Number>))
            }
        }
        return null
    }

}

class Loads(private val arg: Map<String,Any>){

    val isALoadSection:Boolean
        get() = _isALoadSection()

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