package domain

import data.tools.ImageResizer
import domain.models.Sources
import java.io.File
import java.time.Duration
import java.time.Instant.now
import java.util.concurrent.*

/**
 * Image copy and resize
 *
 * @param src source as [Sources]
 * @param srcDir source directory
 * @param target target directory
 * @param d directory delimiter ('\' or '/')
 */
class ImageProcessor(val src: Sources, var srcDir: String, val target: String, private val d: Char) {

    private val logFile = File("$target${d}log.txt")

    private var doResize = false

    private var total = 0

    private var processed = 0

    fun moveImages(doResize: Boolean) {
        this.doResize = doResize

        val t = now()
        File(target).mkdirs()
        logFile.writeText("Begin at $t\n")
        println("Read from $srcDir, write to $target ...")

        val tNum: Int = if (doResize) {
            println("Set number of threads (1-10):")
            readln().toIntOrNull() ?: 1
        } else 1

        val executors: ExecutorService? = if (tNum > 1) {
            Executors.newFixedThreadPool(tNum)
        } else null

        val futures = mutableListOf<Future<Boolean>>()

        File(srcDir).walk().forEach {
            if (!it.isFile) return@forEach
            if (it.extension !in listOf<String>("jpg", "jpeg")) return@forEach
            total++
            if (executors != null) {
                val callable = ImageWork(it) as Callable<Boolean>
                val future: Future<Boolean> = executors.submit(callable)
                futures.add(future)
                if (futures.count() > 1000) {
                    executors.awaitTermination(1, TimeUnit.MINUTES)
                    futures.clear()
                }
            } else {
                processFile(it)
            }
        }
    }

    /**
     * Copy file or resize
     */
    fun processFile(f: File) {
        val lot = f.parent.substringAfterLast(d)
        val auc = f.parent.dropLastWhile { c -> c != d }.dropLast(1).dropLastWhile { c -> c != d }.dropLast(1)
            .substringAfterLast(d)
        val newName = "$target$d$auc$d$lot$d${f.nameWithoutExtension}.jpg"
        when (doResize) {
            true -> try {
                ImageResizer.resize(f.path, newName, 300)
                processed++
            } catch (e: Exception) {
                logFile.appendText(f.path + "\n")
                //e.printStackTrace()
            }
            false -> try {
                f.copyTo(File(newName), overwrite = true)
                processed++
            } catch (e: Exception) {
                logFile.appendText(f.path + "\n")
                //e.printStackTrace()
            }
        }
        if (processed % 100 == 0) print("Processed: $processed of $total\r")
    }

    inner class ImageWork(private val f: File) : Callable<Boolean> {

        @Throws(java.lang.Exception::class)
        override fun call(): Boolean {
            processFile(f)
            return true
        }
    }

}