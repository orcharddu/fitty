import java.io.*

fun main(args: Array<String>) {
    val list = mutableListOf<String>()
    try {
        val file = File("dictionary.txt")
        val inputStream = FileInputStream(file)
        val inputStreamReader = InputStreamReader(inputStream, "utf-8")
        val bufferedReader = BufferedReader(inputStreamReader)
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            if (line!!.length == 5 && line != "pussy" && line != "shits" && line != "fucks") {
                list.add(line!!)
            }
        }
        bufferedReader.close()
        inputStreamReader.close()
        inputStream.close()
    } catch (e: IOException) {
        print(e.message)
    }

    println("loaded dictionary size ${list.size}")

    val map = Array(list.size) { IntArray(list.size) }

    list.forEachIndexed{ i, wordA ->
        list.forEachIndexed { j, wordB ->
            map[i][j] = wordDistance(wordA, wordB)
        }
    }

    for (i in 0 until list.size) {
        if (i % 2 == 0) generate(list, map, i)
    }
}

fun generate(list: List<String>, map: Array<IntArray>, startNode: Int) {

    val size = map.size
    // Dijkstra
    val distance = IntArray(size)
    val path = IntArray(size)
    val visited = BooleanArray(size)

    for (i in 0 until size) {
        distance[i] = map[i][startNode]
        path[i] = startNode
    }

    visited[startNode] = true
    // relaxing
    for (i in 1 until size) {
        var minDis = Int.MAX_VALUE
        var minNode = 0
        // choose an unvisited nearest node
        for (j in 0 until size) {
            if (!visited[j] && distance[j] < minDis) {
                minDis = distance[j]
                minNode = j
            }
        }
        visited[minNode] = true
        for (j in 0 until size) {
            if (!visited[j] && isConnected(map, minNode, j) && minDis + map[minNode][j] < distance[j]) {
                distance[j] = minDis + map[minNode][j]
                path[j] = minNode
            }
        }
    }

    val endNodes = mutableListOf<Int>()
    distance.forEachIndexed { i, dis ->
        if (dis == 5) {
            endNodes.add(i)
        }
    }

    val puzzles = mutableListOf<List<String>>()
    endNodes.forEach { endNode ->
        val puzzle = mutableListOf<String>()
        var currentNode = endNode
        puzzle.add(list[currentNode])
        for (i in 0 until distance[endNode]) {
            currentNode = path[currentNode]
            puzzle.add(list[currentNode])
        }
        puzzles.add(puzzle.reversed())
    }

//    puzzles.forEach { puzzle ->
//        print("listOf(")
//        for (i in 0 until puzzle.size - 1) {
//            print("\"${puzzle[i]}\", ")
//        }
//        println("\"${puzzle.last()}\"),")
//    }
    if (puzzles.size == 0) return
    val puzzle = puzzles.random()
    print("listOf(")
    for (i in 0 until puzzle.size - 1) {
        print("\"${puzzle[i]}\", ")
    }
    println("\"${puzzle.last()}\"),")

}

fun isConnected(map: Array<IntArray>, a: Int, b: Int): Boolean = map[a][b] == 1

fun wordDistance(wordA: String, wordB: String): Int {
    var distance = 0
    val minLength = wordA.length.coerceAtMost(wordB.length)
    for (i in 0 until minLength) {
        if (wordA[i] != wordB[i]) {
            distance++
        }
    }
    return if (distance == 1) 1 else Int.MAX_VALUE
}


