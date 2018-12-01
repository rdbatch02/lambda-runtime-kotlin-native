package runtime

fun hello(): String = "Hello, Kotlin/Native!"

fun main(args: Array<String>) {
    val client = LambdaRuntimeClient()
    println(hello())
    client.getExample()
}