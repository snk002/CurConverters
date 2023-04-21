import AppClass.AppData
import data.api.ElasticUploader
import data.models.*
import domain.ImageProcessor
import domain.models.Auction
import domain.models.House
import domain.models.Sources
import java.io.File
import java.nio.charset.Charset

/***
 * Class to manage operations.
 * Contains [AppData] singleton to provide paths and houses data.
 * @param src Source (SR, IN, etc. defined in [Sources])
 */
class AppClass(private val src: Sources) {

    private var path: String = "$BASE_PATH1/$src"
    lateinit var auctions: HashMap<String, Auction> // Auctions from current source
    private var d: Char = '/' //dir path break

    fun processCommand(cmdId: Int) {
        var fpath: String =
        if (!isWindows()) {
            println("Begin processing command $cmdId for $src...\nSelect /big as alternate root (ignored in Windows)? [y/N]")
            readlnOrNull()?.let {
                if (it.lowercase() == "y") path = "$BASE_PATH2/$src"
            }
            PATH_FINAL2
        } else {
            path = "$BASE_PATHW\\$src"
            d = '\\'
            PATH_FINALW
        }
        when(src) {
            Sources.SR -> {
                when (cmdId) {
                    1 -> LegacyTools().processJsonSR("/mnt/winshare/SR/lot", "/bigssd/SR/auction", "/big/SR/lot_final/", AuctionSR::class)
                    2 -> HouseExtractor(src, "$path/auction", "$PATH_FINAL/").extractHouses()
                    3 -> FinalFiller(src, "$path/lot", "$PATH_FINAL/$src").testJson()
                    4 -> FinalFiller(src, "$path/lot", "$PATH_FINAL/$src").moveJson()
                    //5 -> FinalMover(src,"$PATH_FINAL/$src", "$PATH_FINAL/all").moveJson()
                    5 -> FinalMover(src,"$path", "$PATH_FINAL/all").moveJson()
                    6 -> ContentChecker(src, "$BASE_PATH2/SR/check3").checkFor()
                    //7 -> AuctionExtractor(src, "$path/auction", "$PATH_FINAL/${src}_auctions.csv").extractAuctions()
                    7 -> LotsToAuctions(src,"$BASE_PATH1/final/SR","$PATH_FINAL/sr_auc.csv").makeAll()
                    8 -> ImageProcessor(src, "$path$d"+"pic", "$fpath$d$src$d"+"pics", d).moveImages(false)
                    9 -> ImageProcessor(src, "$path$d"+"pic", "$fpath$d$src$d"+"tumbs", d).moveImages(true)
                }
            }
            Sources.IN -> {
                when (cmdId) {
                    1 -> println("Not yet released :(")
                    2 -> HouseExtractor(src,  "$path/auction", "$PATH_FINAL/").extractHouses()
                    //2 -> LegacyTools().auctionsToHousesList("$path/auction", DEST_FILE_PATH + "IN_houses1.txt", AuctionIN::class)
                    3 -> FinalFiller(src, "$path/lot", "$PATH_FINAL/$src").testJson()
                    4 -> FinalFiller(src, "$path/lot", "$PATH_FINAL/$src").moveJson()
                    //5 -> FinalMover(src,"$PATH_FINAL/$src", "$PATH_FINAL/all").moveJson()
                    5 -> FinalMover(src,"$PATH_FINAL/$src", "$PATH_FINAL/all").moveJson()
                    6 -> TODO("Checker not yet released for IN")
                    7 -> AuctionExtractor(src, "$path/auction_id", "$PATH_FINAL/auctions_$src.csv").extractAuctions()
                    //7 -> LotsToAuctions(src,"/big/IN/IN_sec/","/bigssd/final/${src}_auc_sec.csv").makeAll()
                }
            }
            Sources.LA -> {
                when (cmdId) {
                    1 -> LegacyTools().checkJsonLA(SRC_PATH2, DEST_PATH2)
                    2 -> println("Not yet released :(")
                    5 -> FinalMover(src,"$PATH_FINAL/$src","$PATH_FINAL/all").moveJson()
                }
            }
            Sources.ALL -> {//http://5.9.23.168:9200/3owls-auction/
                ElasticUploader("$PATH_FINAL/all", "http://65.108.213.27:9200/3owls-auction/").run()
            }
        }

    }

    companion object AppData {

        const val PREF_PATH  = "/bigssd/general"
        private const val BASE_PATH1 = "/bigssd"
        private const val BASE_PATH2 = "/big"
        private const val BASE_PATHW = "H:\\"
        private const val PATH_FINAL = "/bigssd/final"
        private const val PATH_FINAL2 = "/big/final"
        private const val PATH_FINALW = "H:\\"
        private const val HOUSE_DIRECTORY = "$PREF_PATH/houses_full.csv"

        lateinit var maxHouseNumbers: HashMap<Sources, Int> // Max id based on src
        lateinit var houses: HashMap<String, House> // All houses

        /** Load houses data from SCV file */
        fun readHouses() : Int {
            val lines = File(HOUSE_DIRECTORY).readLines(Charset.defaultCharset())
            var x = 0
            houses = hashMapOf()
            maxHouseNumbers = hashMapOf()
            lines.forEach { line: String ->
                val item = line.split("\t")
                if (item.size>2) {
                    val ownId = try { item[0].toInt() } catch (E: Exception) { 0 }
                    val srcId = item[1].trim()
                    val ignored = try { item[2].toInt() } catch (E: Exception) { 0 }
                    val name = item[3].trim()
                    val country = item[4].trim()
                    val city = item[5].trim()
                    val currency = item[6].trim()
                    val premium: Double = item[7].trim().removeSuffix("%").toDoubleOrNull() ?: 0.0
                    val site = item[8].trim()
                    val hSrc = item[9].trim()
                    if (ownId == 0 || srcId == "") return@forEach
                    val h = House(ownId, srcId, ignored, name, country, city, currency, premium, site, hSrc)
                    houses["$hSrc:$srcId"] = h
                    //if (ownId > maxHouseId) maxHouseId = ownId
                    if (ownId > (maxHouseNumbers.getOrDefault(Sources.valueOf(hSrc), 0 )) )
                        maxHouseNumbers[Sources.valueOf(hSrc)] = ownId
                    x++
                }
            }
            if (x>0) {
                println("Directory for $x houses was loaded successful. Max ID's:")
                maxHouseNumbers.forEach {
                    println(it.key.toString()+"="+it.value)
                }
            }
            else {
                println("Error: Directory houses was not loaded successful.")
            }
            return x
        }
    }

    fun isWindows() : Boolean {
        return System.getProperty("os.name").startsWith("Windows")
    }
}