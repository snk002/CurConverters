package data.providers

import data.models.AuctionIN
import data.models.AuctionSR
import domain.models.Auction
import domain.models.Sources
import java.io.File

/**
 * Base class to read auctions.
 * @param src Source as [Sources]
 * @param srcDir path to source auctions folder
 * @param target path to saved file
 */
abstract class AuctionBaseReader(val src: Sources, private val srcDir: String, val target: String) {

    /** Recursively read directory with auction files */
    fun readAuctions(verbose: Boolean = false): Int {
        var x = 0
        println("Reading from $srcDir, results will save to $target")
        print("Continue? [Y/n]")
        if (!readlnOrNull().toString().contentEquals("y",true)) return -1
        File(srcDir).walk().forEach { file ->
            if (!file.isFile) return@forEach
            if (file.extension != "json") return@forEach
            x++
            if (verbose && (x % 1000 == 0)) print("\r${x/1000}k processed...   ")
            if (!readAuction(file)) {
                val target = target.dropLastWhile { it != '/' } +file.path.dropWhile { it != '/' }
                file.copyTo(File(target), true)
                //println("\nUnexpected error on the $x iteration")
                //return -1
            }
        }
        return x
    }

    /** Save to CSV file */
    abstract fun saveFound(destFile: String) : Boolean

    /**
     * Should read auction and try to make small fixes.
     * @param file source [File] to read
     * @return true if read and/or fixed ok
     */
    abstract fun readAuction(file: File): Boolean

    /** Return actual auction class based on source */
    fun getClassType() : Class<out Auction> = when (src) {
        Sources.SR -> AuctionSR::class.java
        Sources.IN -> AuctionIN::class.java
        else -> Auction::class.java
    }
}
