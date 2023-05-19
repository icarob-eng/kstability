package com.kstability

import com.kstabilty.*
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * Class responsible for converting a node, and its respective properties,
 * from YAML to an object in Kotlin.
 * @property isNode property responsible for indicating whether the argument passed to the class is, preliminarily, a node.
 * @property isNotNode Property that is the negation of isNode.
 *
 * @see Knot
 * @see Vector
 * **/
@Suppress("UNCHECKED_CAST")
class Node(private val arg:Map<String,Any> = mapOf()){

    constructor(entry: Map.Entry<String,Any>) : this(mapOf(entry.toPair()))

    val isNode:Boolean = run {
        if(arg.keys.size!=1){
            return@run false
        }
        if(arg.values.all {it is String && (it.lowercase() =="vertical" || it.lowercase()=="horizontal")}){
            return@run true
        }
        else if(arg.values.all {it is Map<*,*> } && arg.values.all{ it -> (it as Map<*, *>).values.all { it is Number }}){
            return@run true
        }
        else if(arg.values.all { it -> (it is Array<*>) && it.all { it is Number } }){
            return@run true
        }
        else if(arg.values.all{ it -> (it is ArrayList<*>) && it.all { it is Number }}){
            return@run true
        }
        return@run false
    }

    val isNotNode:Boolean = !isNode

    fun makeNode():Knot? {
        try{
            if(this.isNode){
                if(arg.values.all {it is String && (it.lowercase() =="vertical" || it.lowercase()=="horizontal")}){
                    val name:String = arg.entries.first().key
                    val vector = Vector(arg.entries.first().value as String)
                    return Knot(name,vector)
                }
                else if(arg.values.all {it is Map<*,*> } && arg.values.all{ it -> (it as Map<String,Any>).values.all { it is Number }}){
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
        }
        catch (e:Exception){
            print(e.message)
        }
        return null
    }
}

/**
 * Class responsible for managing all the nodes contained in the appropriate section: node.
 *
 * @property isANodeSection property responsible for checking whether the passed argument is a node section.
 * @property isNotANodeSection denial of ownership isANodeSection
 * @property isAllNodes property responsible for checking if the content of the section is only composed of nodes.
 * @property isAnyNode property responsible for checking whether there is at least one node in the section.
 *
 * @see Node
 * @see Knot
 * **/
@Suppress("UNCHECKED_CAST")
class Nodes(private val arg: Map<String,Any>){

    val isANodeSection = run{
        if(arg.entries.size!=1){
            return@run false
        }
        for((outerKey,outerValue) in arg as Map<String,Map<String,Any>>){
            for(innerMap in outerValue){
                if(Node(innerMap).isNotNode){
                    return@run false
                }
            }
        }
        return@run true
    }

    val isNotANodeSection = !isANodeSection

    val isAllNodes = isANodeSection

    val isAnyNode = run{
        if(arg.entries.size!=1){
            return@run false
        }
        for((outerKey,outerValue) in arg as Map<String,Map<String,Any>>){
            for(innerMap in outerValue){
                if(Node(innerMap).isNode){
                    return@run true
                }
            }
        }
        return@run false
    }
    fun makeNodes():MutableList<Knot>{
        val nodes: MutableList<Knot> = mutableListOf()
        if(this.isANodeSection){
            for((outerKey,outerValue) in arg as Map<String,Map<String,Any>>){
                for(innerNode in outerValue){
                    Node(innerNode).makeNode()?.let { nodes.add(it) }
                }
            }
        }
        else if(this.isAnyNode){
            for((outerKey,outerValue) in arg as Map<String,Map<String,Any>>){
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
 * @property isHolder property responsible for indicating, preliminarily, if the argument passed to the class is a support.
 * @property isNotHolder denial of ownership isHolder
 *
 * @see Support
 * @see Knot
 * @see Vector
 *
 * **/
@Suppress("UNCHECKED_CAST")
class Holder(private val arg:Map<String,Any>){

    constructor(entry: Map.Entry<String, Any>): this(mapOf(entry.toPair()))

    val isHolder = run {
        if(arg.entries.size!=1){
            return@run false
        }
        return@run arg.values.all{ it -> (it as Map<*, *>).values.all { it == 1 || it == 2 || it == 3 || it =="vertical" || it =="horizontal" || (it is ArrayList<*> && it.all { it is Number })}}
    }

    val isNotHolder = !isHolder
    fun makeSupport(knots:MutableList<Knot>): Support? {
        val holder:Map<String, Any> = (arg.entries.first().value as Map<String,Map<String, Any>>)
        try{
            if(this.isHolder){
                if(holder["direção"] is String){
                    return if(holder["gênero"]==1){
                        knots.find{it.name==arg.entries.first().key}
                            ?.let { Support(knot = it, gender = Support.Gender.FIRST, dir = Vector(holder["direção"] as String)) }
                    } else if(holder["gênero"]==2){
                        knots.find{it.name==arg.entries.first().key}
                            ?.let { Support(knot = it, gender = Support.Gender.SECOND, dir = Vector(holder["direção"] as String)) }
                    } else{
                        knots.find{it.name==arg.entries.first().key}
                            ?.let { Support(knot = it, gender = Support.Gender.THIRD, dir = Vector(holder["direção"] as String)) }
                    }
                }
                else{
                    return if(holder["gênero"]==1){
                        knots.find{it.name==arg.entries.first().key}
                            ?.let { Support(knot = it, gender = Support.Gender.FIRST, dir = Vector(holder["direção"] as ArrayList<Number>)) }
                    } else if(holder["gênero"]==2){
                        knots.find{it.name==arg.entries.first().key}
                            ?.let { Support(knot = it, gender = Support.Gender.SECOND, dir = Vector(holder["direção"] as ArrayList<Number>)) }
                    } else{
                        knots.find{it.name==arg.entries.first().key}
                            ?.let { Support(knot = it, gender = Support.Gender.THIRD, dir = Vector(holder["direção"] as ArrayList<Number>)) }
                    }
                }
            }
            else{
                throw IllegalArgumentException("Nó não declarado.")
            }
        }
        catch (e:IllegalArgumentException){
            print(e.message)
        }
        catch (e:Exception){
            print(e.message)
        }
        return null
    }
}

@Suppress("UNCHECKED_CAST")
class Holders(private val arg:Map<String, Any>){

    val isAHolderSection:Boolean = run{
        if(arg.entries.size!=1) {
            return@run false
        }
        for((outerKey, outerValue) in arg as Map<String,Map<String,Any>>){
            for(holder in outerValue){
                if(Holder(outerValue).isNotHolder){
                    return@run false
                }
            }
        }
        return@run true
    }

    val isNotAHolderSection:Boolean = !isAHolderSection

    val isAllHolders:Boolean = run {
        for((outerKey, outerValue) in arg as Map<String, Map<String,Any>>){
            for(holder in outerValue){
                if(Holder(outerValue).isNotHolder){
                    return@run false
                }
            }
        }
        return@run true
    }

    val isAnyHolder: Boolean = run{
        for((outerKey, outerValue) in arg as Map<String, Map<String, Any>>){
            for(holder in outerValue){
                if(Holder(outerValue).isNotHolder){
                    return@run false
                }
            }
        }
        return@run false
    }

    fun makeSupports(knots: MutableList<Knot>):MutableList<Support>?{
        val supports:MutableList<Support> = mutableListOf()
        try{
            if(this.isAHolderSection){
                for((outerKey,outerValue) in arg as Map<String,Map<String,Any>>){
                    for(holder in outerValue){
                        Holder(holder).makeSupport(knots)?.let { supports.add(it) }
                    }
                }
            }
            else{
                throw IllegalArgumentException("O argumento da classe não é uma seção de suportes.")
            }
        }
        catch (e:IllegalArgumentException){
            print(e.message)
        }
        return null
    }

}

class Beam(private val beam:ArrayList<*>){

    val isBeam = run{
        return@run beam.all { it is String }
    }

    val isNotBeam = !isBeam
    fun isValidBeam(knots:MutableList<Knot>):Boolean{
        for(name in beam){
            if(name !in knots.map { it.name }){
                return false
            }
        }
        return true
    }

    fun isNotValidBeam(knots:MutableList<Knot>):Boolean{
        return !isValidBeam(knots)
    }

    fun makeBar(knots:MutableList<Knot>): Bar? {
        try{
            if(this.isBeam && this.isValidBeam(knots)){
                return Bar(knot1 = knots.find { it.name == beam.get(0) }!!, knot2 = knots.find { it.name == beam.get(1) }!!)
            }
            else{
                throw IllegalArgumentException("As barras informadas são inválidas.")
            }
        }
        catch (e:IllegalArgumentException){
            print(e.message)
        }
        return null
    }
}
class Beams(private val arg: Map<String,Any>){

    val isBeam = run{
        if(arg.entries.size !=1 || arg.entries.first().key!="barras"){
            return@run false
        }
        return@run true
    }

    val isNotBeam = !isBeam

    fun isValidListOfBeams(knots:MutableList<Knot>): Boolean{
        for(beam in arg.entries.first().value as ArrayList<*>){
            if(Beam(beam as ArrayList<*>).isNotValidBeam(knots)){
                return false
            }
        }
        return true
    }

    fun isNotValidListOfBeams(knots:MutableList<Knot>): Boolean{
        return !this.isValidListOfBeams(knots)
    }

    fun makeBars(knots:MutableList<Knot>):List<Bar>{
        val out:MutableList<Bar> = mutableListOf()
        try{
            if(this.isValidListOfBeams(knots)){
                for(beam in arg.entries.first().value as ArrayList<*>)
                    Beam(beam as ArrayList<*>).makeBar(knots)?.let { out.add(it) }
            }
            else{
                throw IllegalArgumentException("A lista de barras é inválida.")
            }
        }
        catch (e:IllegalArgumentException){
            print(e.message)
        }
        return out
    }
}

@Suppress("UNCHECKED_CAST")
class Load(private val arg: Map<String, Any>){

    val isLoad: Boolean = run{
        if(arg.entries.size!=1){
            return@run false
        }
        val content:Map<String, Any> = arg.entries.first().value as Map<String,Any>
        return@run content.keys.containsAll(setOf("nó","direção","módulo")) || content.keys.containsAll(setOf("nó","vetor"))
    }

    val isNotLoad: Boolean = !isLoad

    fun makeLoad(knots: MutableList<Knot>): Any? {
        try{
            if(this.isLoad){
                val content = arg.entries.first().value as Map<String, Any>
                if(content.keys.containsAll(setOf("nó","direção","módulo")) || content["no"] is ArrayList<*>){
                    val knotsNames = (content["no"] as ArrayList<String>)
                    if(knotsNames.size!=2){
                        throw Exception("Número de nós inválidos.")
                    }
                    return DistributedLoad(knot1 = knots.find { it.name == knotsNames[0]}!!, knot2 = knots.find { it.name == knotsNames[1]}!!, Vector(content["direção"] as String)*(content["módulo"] as Float))
                }
                else if(content.keys.containsAll(setOf("nó","direção","módulo")) || content["no"] is String){
                    return PointLoad(knot = knots.find { it.name == content["nó"]}!!, Vector(content["direção"] as String)*(content["módulo"] as Float))
                }
                else if(content.keys.containsAll(setOf("nó","vetor")) || content["no"] is ArrayList<*>){
                    val knotsNames = (content["no"] as ArrayList<String>)
                    if(knotsNames.size!=2){
                        throw Exception("Número de nós inválidos.")
                    }
                    return DistributedLoad(knot1 = knots.find { it.name == knotsNames[0]}!!, knot2 = knots.find { it.name == knotsNames[1]}!!, Vector(content["vetor"] as ArrayList<Number>))
                }
                else if(content.keys.containsAll(setOf("nó","vetor")) || content["no"] is String){
                    return PointLoad(knot = knots.find { it.name == content["nó"]}!!, Vector(content["vetor"] as ArrayList<Number>))
                }
            }
        }
        catch (e:Exception){
            print(e.message)
        }
        return null
    }

}

class Parse(path:String){
    var data: Map<String,Any> = Yaml().load(File(path).inputStream())

    fun getProjectName(): String? {
        if(data["projeto"]!=null && data["projeto"] is String){
            return data["projeto"] as String
        }
        return null
    }
}