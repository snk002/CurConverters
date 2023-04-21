package domain

import java.io.File

// For Filler and Mover

abstract class FinalTools(var srcDir: String, val target: String) {

    protected var broken = 0
    protected var others = 0
    protected var nocode = 0
    protected var nodate = 0

    open fun moveJson() {
        var x = 0
        File(target).mkdirs()
        File(srcDir).walk().forEach {
            if (!it.isFile) return@forEach
            if (it.extension != "json") return@forEach
            x++
            if (!readAndCopy(it)) {
                println("Unexpected error on the $x iteration")
                return
            }
        }
        println("Total $x files. Found $broken unterminated strings, $nocode haven't ownId, $nodate haven't date and $others other issues")
    }


    fun logProcessed(x: Int) {
        val i: Int = x / 100000
        if (x < 1_000_000) println("${i}00k processed...")
        else {
            println("${i / 10}.${i % 10}M processed...")
        }
    }

    abstract fun readAndCopy(srcFile: File): Boolean
}
