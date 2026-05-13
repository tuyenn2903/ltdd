package exerciselab1.bai2

import kotlin.math.PI

fun main() {
    // Danh sách chỉ đọc & danh sách mutable
    val numbers = listOf(1, 2, 3, 4, 5, 6)
    println("numbers.size = ${numbers.size}")
    println("numbers[0] = ${numbers[0]}")
    println("reversed = ${listOf("red", "blue", "green").reversed()}")

    val entrees = mutableListOf<String>()
    entrees.add("spaghetti")
    println("entrees = $entrees")
    entrees[0] = "lasagna"
    println("entrees after update = $entrees")
    entrees.remove("lasagna")
    println("entrees after remove = $entrees")

    // for loop
    val myList = listOf("A", "B", "C")
    for (element in myList) {
        println("for: $element")
    }

    // while loop
    var index = 0
    while (index < myList.size) {
        println("while: ${myList[index]}")
        index++
    }

    // String length + template
    val name = "Android"
    println("name.length = ${name.length}")
    val number = 10
    val groups = 5
    println("$number people")
    println("${number * groups} people")

    // OOP: Dwelling / RoundHut / SquareCabin
    val roundHut = RoundHut(residents = 3, radius = 2.5)
    val squareCabin = SquareCabin(residents = 4, sideLength = 3.0)

    // with(...)
    with(squareCabin) {
        println("Capacity: $capacity")
        println("Material: $buildingMaterial")
        println("Has room? ${hasRoom()}")
        println("Floor area: ${floorArea()}")
    }

    println("RoundHut floor area: ${roundHut.floorArea()}")

    // PI dùng trực tiếp hoặc fully-qualified
    val radius = 2.0
    println("Circle area by PI import: ${PI * radius * radius}")
    println("Circle area by fully-qualified: ${kotlin.math.PI * radius * radius}")

    // vararg
    addToppings("cheese", "pepperoni", "olives")
}

abstract class Dwelling(private var residents: Int) {
    abstract val buildingMaterial: String
    abstract val capacity: Int
    abstract fun floorArea(): Double

    fun hasRoom(): Boolean = residents < capacity
    fun getResidentCount(): Int = residents
}

open class RoundHut(residents: Int, private val radius: Double) : Dwelling(residents) {
    override val buildingMaterial: String = "Straw"
    override val capacity: Int = 4

    override fun floorArea(): Double = PI * radius * radius
}

class SquareCabin(residents: Int, private val sideLength: Double) : Dwelling(residents) {
    override val buildingMaterial: String = "Wood"
    override val capacity: Int = 6

    override fun floorArea(): Double = sideLength * sideLength
}

fun addToppings(vararg toppings: String) {
    println("Toppings: ${toppings.joinToString()}")
}