import com.google.gson.Gson
import data.models.AuctionDB
import data.models.FinalLotData
import domain.models.Sources
import java.io.File

/**
 * Collect date from lots to fill auctions DB table.
 * @param src source type [Sources]
 * @param baseDir source directory
 * @param target destination CSV file
 */
class LotsToAuctions(val src: Sources, private val baseDir: String, private val target: String) {

    private val auctions = mutableListOf<AuctionDB>()

    fun makeAll() {
        var x = 0
        var y = 0
        File(baseDir).list()?.forEach { currentPath ->
            val cFile = File("$baseDir/$currentPath")
            if (cFile.isDirectory) {
                x++
                if (x % 1000 == 0) print("\r${x/1000}k dirs processed...   ")
                val res = readLotsDirectory(cFile)
                if (res.lotcount<=0) {
                    y++
                    return@forEach
                }
                auctions.add(res)
            }
        }
        println("Total $x auctions checked. Not found in $y ")
        if (saveFound())  println("Results saved to $target") else
            println("An error on save results to $target")
    }

    /**
     * Read auction directory to check up to each lot it for price and images.
     * @return [Pair] of [Boolean] priceFound & imageFound
     */
    private fun readLotsDirectory(srcDir: File): AuctionDB {
        var rez = AuctionDB(lotcount = -1) // result
        var priceFound = false
        var imageFound = false
        var lotsFound = 0
        var skipRead = false
        srcDir.listFiles()?.forEach {
            //if (it.extension != "json") return@forEach
            if (!it.isFile) return@forEach
            if (skipRead) {
                lotsFound++
                return@forEach
            }
            val ta = processLotData(it)
            if (ta.lotcount > -1) {
                lotsFound++
                rez = AuctionDB(globalid = "0", localid = ta.localid, houseid = ta.houseid, houselocal = ta.houselocal,
                    title = ta.title, date = ta.date, source = src.toString(), premium = ta.premium)
            }
            if (ta.hasprices) priceFound = true
            if (ta.hasimages) imageFound = true
            skipRead = (priceFound && imageFound)
        }
        rez.lotcount = lotsFound
        rez.hasimages = imageFound
        rez.hasprices = priceFound
        return rez
    }

    /**
     * Read lot json and check it for price and images.
     * @return [AuctionDB] with filled properties
     */
    private fun processLotData(srcFile: File): AuctionDB {
        val gson = Gson()
        return try {
            val str = srcFile.readText(Charsets.UTF_8)
            val lotLoaded: FinalLotData = gson.fromJson(str, FinalLotData::class.java)
            AuctionDB(lotcount = 0, hasimages = (lotLoaded.imageCount > 0), hasprices = (lotLoaded.priceRealised != 0.0),
                globalid = "0", localid = lotLoaded.saleId, houseid = lotLoaded.globalHouseCode.toString(),
                title = lotLoaded.saleName, date = lotLoaded.saleDate, houselocal = lotLoaded.houseCode, premium = lotLoaded.premium)
        } catch (e: Exception) {
            AuctionDB(lotcount = -1, hasimages = false, hasprices = false)
        }
    }

    /** Save all found auctions to CSV file */
    private fun saveFound() : Boolean = try {
        File(target).printWriter().use { writer ->
            auctions.forEach {
                writer.println(it.toCSV())
            }
        }
        true
    } catch (e: java.lang.Exception) {
        println(e.message)
        false
    }
}
