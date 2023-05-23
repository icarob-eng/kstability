package com.kstability

import com.kstabilty.*

/**
 * Class responsible for converting a node, and its respective properties,
 * from YAML to an object in Kotlin.
 * @property isNode property responsible for indicating whether the argument
 * passed to the class is, preliminarily, a node.
 *
 * @see Node
 * @see Vector
 * **/
class NodeCreator(private val arg:Map<String,Any> = mapOf()){

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

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class, IllegalArgumentException::class)
    fun createNode(): Node? {
        if(this.isNode){
            if(arg.values.all {it is String && (it.lowercase() =="vertical" || it.lowercase()=="horizontal")}){
                val name:String = arg.entries.first().key
                val vector = Vector(arg.entries.first().value as String)
                return Node(name,vector)
            }
            else if(arg.values.all {it is Map<*,*> } &&
                arg.values.all{ it -> (it as Map<String,Any>).values.all { it is Number }}){
                val name:String = arg.entries.first().key
                val coordinatesNotation:Map<String,Any> = arg.entries.first() as Map<String,Any>
                val vector = Vector(coordinatesNotation["x"] as Float,coordinatesNotation["y"] as Float)
                return Node(name,vector)
            }
            else if(arg.values.all { it -> it is Array<*> && it.all { it is Number }}){
                val name:String = arg.entries.first().key
                val listNotation: Array<Number> = arg.entries.first().value as Array<Number>
                val vector = Vector(listNotation)
                return Node(name,vector)

            }
            else if(arg.values.all{ it -> (it is ArrayList<*>) && it.all { it is Number }}){
                val name:String = arg.entries.first().key
                val lisNotation: ArrayList<Number> = arg.entries.first().value as ArrayList<Number>
                val vector = Vector(lisNotation)
                return Node(name, vector)
            }
            else{
                throw IllegalArgumentException("Tipo de notação inválida.")
            }
        }
        return null
    }
}

/**
 * Class responsible for managing a support, regarding the argument passed to it.
 * @property isSupport property responsible for indicating, preliminarily, if the
 * argument passed to the class is a support.
 *
 * @see Support
 * @see Node
 * @see Vector
 *
 * **/
class SupportCreator(private val arg:Map<String,Any>){

    constructor(entry: Map.Entry<String, Any>): this(mapOf(entry.toPair()))

    val isSupport:Boolean
        get() = _isSupport()

    private fun _isSupport():Boolean{
        if(arg.entries.size!=1){
            return false
        }
        return arg.values.all{ it -> (it as Map<*, *>).values.all { it == 1 || it == 2 || it == 3 || it =="vertical" ||
                it =="horizontal" || (it is ArrayList<*> && it.all { it is Number })}}
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class, IllegalArgumentException::class)
    fun createSupport(nodes:MutableList<Node>): Support? {
        val holder:Map<String, Any> = (arg.entries.first().value as Map<String,Map<String, Any>>)
        if(this.isSupport){
            if(holder["direção"] is String){
                return if(holder["gênero"]==1){
                    nodes.find{it.name==arg.entries.first().key}
                        ?.let { Support(node = it, gender = Support.Gender.FIRST,
                            dir = Vector(holder["direção"] as String)) }
                } else if(holder["gênero"]==2){
                    nodes.find{it.name==arg.entries.first().key}
                        ?.let { Support(node = it, gender = Support.Gender.SECOND,
                            dir = Vector(holder["direção"] as String)) }
                } else{
                    nodes.find{it.name==arg.entries.first().key}
                        ?.let { Support(node = it, gender = Support.Gender.THIRD,
                            dir = Vector(holder["direção"] as String)) }
                }
            }
            else{
                return if(holder["gênero"]==1){
                    nodes.find{it.name==arg.entries.first().key}
                        ?.let { Support(node = it, gender = Support.Gender.FIRST,
                            dir = Vector(holder["direção"] as ArrayList<Number>)) }
                } else if(holder["gênero"]==2){
                    nodes.find{it.name==arg.entries.first().key}
                        ?.let { Support(node = it, gender = Support.Gender.SECOND,
                            dir = Vector(holder["direção"] as ArrayList<Number>)) }
                } else{
                    nodes.find{it.name==arg.entries.first().key}
                        ?.let { Support(node = it, gender = Support.Gender.THIRD,
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
 * Class responsible for managing the construction and evaluation of a slash candidate argument.
 *
 * @property isBeam property responsible for preliminarily checking whether the argument is a slash.
 * @property isValidBeam Property responsible for checking if the argument passed is a valid slash
 * based on the nodes on the file.
 *
 * @see Beam
 * **/

class BeamCreator(private val beams:ArrayList<*>){

    val isBeam:Boolean
        get() = _isBeam()

    private fun _isBeam():Boolean{
        return beams.all { it is String }
    }

    fun isValidBeam(nodes:MutableList<Node>):Boolean{
        for(name in beams){
            if(name !in nodes.map { it.name }){
                return false
            }
        }
        return true
    }

    @Throws(IllegalArgumentException::class)
    fun createBeam(nodes:MutableList<Node>): Beam? {
        if(this.isBeam && this.isValidBeam(nodes)){
            return Beam(node1 = nodes.find { it.name == beams[0] }!!,
                node2 = nodes.find { it.name == beams[1] }!!)
        }
        else{
            throw IllegalArgumentException("As barras informadas são inválidas.")
        }
        return null
    }
}

/**
 * Class responsible for managing the argument passed to it regarding loads.
 * @property isLoad checks if the argument passed to the class is a payload.
 *
 * @see DistributedLoad
 * @see PointLoad
 * **/
class PointLoadCreator(private val arg: Map<String, Any>){

    constructor(arg:Map.Entry<String,Any>):this(mapOf(arg.toPair()))

    val isLoad: Boolean
        get() = _isLoad()

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    private fun _isLoad():Boolean{
        if(arg.entries.size!=1){
            return false
        }
        val content:Map<String, Any> = arg.entries.first().value as Map<String,Any>
        return content.keys.containsAll(setOf("nó","direção","módulo")) || content.keys.containsAll(setOf("nó","vetor"))
    }

    @Suppress("UNCHECKED_CAST")
    @Throws(ClassCastException::class)
    fun createLoad(nodes: MutableList<Node>): Any? {
        if(this.isLoad){
            val content = arg.entries.first().value as Map<String, Any>
            if(content.keys.containsAll(setOf("nó","direção","módulo")) || content["no"] is ArrayList<*>){
                val nodesNames = (content["no"] as ArrayList<String>)
                if(nodesNames.size!=2){
                    throw Exception("Número de nós inválidos.")
                }
                return DistributedLoad(node1 = nodes.find { it.name == nodesNames[0]}!!,
                    node2 = nodes.find { it.name == nodesNames[1]}!!,
                    vector = Vector(content["direção"] as String)*(content["módulo"] as Float))
            }
            else if(content.keys.containsAll(setOf("nó","direção","módulo")) || content["no"] is String){
                return PointLoad(node = nodes.find { it.name == content["nó"]}!!,
                    vector=Vector(content["direção"] as String)*(content["módulo"] as Float))
            }
            else if(content.keys.containsAll(setOf("nó","vetor")) || content["no"] is ArrayList<*>){
                val nodesNames = (content["no"] as ArrayList<String>)
                if(nodesNames.size!=2){
                    throw Exception("Número de nós inválidos.")
                }
                return DistributedLoad(node1 = nodes.find { it.name == nodesNames[0]}!!,
                    node2 = nodes.find { it.name == nodesNames[1]}!!, Vector(content["vetor"] as ArrayList<Number>))
            }
            else if(content.keys.containsAll(setOf("nó","vetor")) || content["no"] is String){
                return PointLoad(node = nodes.find { it.name == content["nó"]}!!,
                    vector=Vector(content["vetor"] as ArrayList<Number>))
            }
        }
        return null
    }

}