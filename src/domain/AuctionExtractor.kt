import com.google.gson.Gson
import data.models.AuctionIN
import data.providers.AuctionBaseReader
import domain.models.Auction
import domain.models.Sources
import java.io.File

/**
 * Extract auctions data from files in /auction_id directory and put into the
 * CSV file for export into the database
 */
class AuctionExtractor(src: Sources, path: String, target: String) : AuctionBaseReader(src, path, target) {

    private val auctions = mutableListOf<Auction>()
    private var errors = 0
    private var count = 0

    /** Read dir and apply action to each file. Call this to start operation */
    fun extractAuctions() {
        if (AppClass.readHouses()<1) return
        readAuctions(true).also { x ->
            if (x <= 0) return
            println("$x done. Total ${auctions.count()} auctions found, with $errors errors, written $count lines")
        }
        target.also {
            if (saveFound(it))
                println("Extracted data saved to $it")
        }
    }

    override fun saveFound(destFile: String): Boolean = try {
        File(destFile).printWriter().use { writer ->
                auctions.forEach {
                    writer.println(it.toCSV())
                    //writer.println((it as AuctionIN).toShortCSV())
                    count++
                }
            }
            true
        } catch (e: java.lang.Exception) {
            println(e.message)
            false
        }

    override fun readAuction(file: File): Boolean {
        val gson = Gson()
        return try {
            val str = file.readText(Charsets.UTF_8)
            val a = gson.fromJson(str,getClassType())
            a.id = file.nameWithoutExtension
            a.fixCode(file.nameWithoutExtension) // for SR. ignored for IN
            if (a is AuctionIN) {
                if (!a.fixDate()) {
                    errors++
                    return false
                }
            }
            //val dba = AuctionDB("globalid",a.code,"houseid","title","date",true,true,1)
            auctions.add(a)
            true
        } catch (e: Exception) {
            //println(file.name +" "+e.message)
            true
        }
    }
}