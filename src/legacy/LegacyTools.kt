import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import domain.models.Auction
import data.models.AuctionSR
import domain.models.House
import java.io.File
import java.nio.charset.Charset
import kotlin.reflect.KClass

//legacy only
val houseIndex: MutableMap<String, String> = mutableMapOf() // id, code
val auctionIndex: MutableMap<String, Auction> = mutableMapOf()
val cats = mutableMapOf<String, String>()
var prices = 0

const val DEST_FILE_PATH = "/home/serge/" //SR_houses1.txt"
const val SRC_PATH2 = "/mnt/winshare/LA/lot"
const val DEST_PATH2 = "/big/LA/lot_ready/"


fun getCat(abbr: String) : String {
    return if (!cats[abbr].isNullOrEmpty()) cats[abbr]!!
    else ""
}

class LegacyTools() {

    companion object {
        const val PREF_PATH = "/bigssd/general"
        const val BASE_PATH = "/big"
    }

    var maxHouseId = 0
    var escape = 0
    var escapeFixed = 0
    var broken = 0
    var others = 0
    var good = 0

    private fun loadHouseIndex(fn: String) {
        val lines = File(fn).readLines(Charset.defaultCharset())
        lines.forEach { s: String ->
            val a = s.split("\t")
            if (a.size>1) houseIndex[a[1]] = a[0]
            maxHouseId = a[0].toInt()
        }
    }

    private fun loadCats(fn: String) {
        val lines = File(fn).readLines(Charset.defaultCharset())
        lines.forEach { s: String ->
            val a = s.split("=")
            if (a.size>1) cats[a[0]] = a[1]
        }
    }

    fun <T: Auction> auctionsToHousesList(auctionsPath: String, destFile: String, at: KClass<T>) {
        loadAuctions(auctionsPath, at) // fills the auctionIndex
        loadHouseIndex("${BASE_PATH}/SR/sr_houses.txt") // fills the houseIndex and sets maxHouseId
        val houses: MutableMap<Int, Auction> = mutableMapOf() // here Auction act as House
        val newHouses = mutableListOf<House>() // new
        println("Start with $maxHouseId")
        var x = 0
        val i = maxHouseId
        val codes: HashSet<String> = hashSetOf() //checking unique house id (for IN)
        auctionIndex.forEach { auction ->
            val houseCode = if (at == AuctionSR::class) auction.key.dropLast(5)
            else auction.value.code
            if (codes.add(houseCode)) {
                val houseId = houseIndex.getOrPut(houseCode, { (++maxHouseId).toString() })
                houses[houseId.toInt()] = auction.value
                auction.value.id = houseId
                auction.value.code = houseCode
                //newHouses.add(House(maxHouseId,houseCode,auction.value.))
                x++
            }
        }
        println("$x, End with $maxHouseId")
        File(destFile).printWriter().use { writer ->
            houses.forEach{
                writer.println(it.value.toCSV())
            }
        }
        println("CSV file saved to $destFile")
        if (maxHouseId > i) {
            File("${PREF_PATH}/sr_houses_NEW.txt").printWriter().use { writer ->
                houseIndex.forEach{
                    writer.println(it.value+"\t"+it.key)
                }
            }
            println("New Houses list saved to ${PREF_PATH}/sr_houses_NEW.txt")
        }
    }

    private fun <T: Auction> loadAuctions(path: String, at: KClass<T>) {
        val gson = Gson()
        var x = 0
        var y = 0
        File(path).walk().forEach {
            if (!it.isFile) return@forEach
            if (it.extension != "json") return@forEach
            val str = it.readText(Charsets.UTF_8)
            try {
                val auction = gson.fromJson(str, at.java)
                auctionIndex[it.nameWithoutExtension] = auction
                x++
                //println(it.nameWithoutExtension + " " + auction)
            } catch (e: Exception) {
                y++
            }
        }
        println("$x auctions loaded, $y failed")
    }

    fun <T: Auction> processJsonSR(path: String, auctPath: String, target: String, at: KClass<T>) {
        var x = 0
        File(target).mkdirs()
        loadCats("${PREF_PATH}/sr_cats.txt")
        loadHouseIndex("${BASE_PATH}/SR/sr_houses.txt")
        loadAuctions(auctPath, at)
        File(path).walk().forEach {
            //println(it.name+" begin")
            if (!it.isFile) return@forEach
            if (it.extension != "json") return@forEach
            x++
            if (!readSR(it, target, it.name)) return
        }
        println("$x processed with $good okay, $broken broken files, $escape bad escapes ($escapeFixed fixed) and $others over errors; wrong prices: $prices")
    }

    fun checkJsonLA(path: String, target: String) {
        var x = 0
        File(target).mkdirs()
        File(path).walk().forEach {
            //println(it.name+" begin")
            if (!it.isFile) return@forEach
            if (it.extension != "json") return@forEach
            x++
            if (!readLA(it, target, it.name)) return
        }
        println("$x processed with $good okay, $broken broken files, $escape bad escapes ($escapeFixed fixed) and $others over errors; wrong prices: $prices")
    }

    private fun readLA(src: File, target: String, fileName: String) : Boolean {
        val gson = Gson()
        return try {
            var str = src.readText(Charsets.UTF_8)
            var a : LALotData?
            try {
                try {
                    a = gson.fromJson(str, LALotData::class.java)
                } catch (e: JsonSyntaxException) {
                    val msg = e.message
                    if (msg != null && msg.contains("Invalid escape sequence")) {
                        escape++
                        //str = str.replace("\\&", "&").replace("\'", "'")
                        //println(src.path + " Fixing: Invalid escape sequence")
                        //a = gson.fromJson(str, LALotData::class.java)
                        //escapeFixed++
                    } else throw e
                }
                good++
            } catch (e: Exception) {
                val msg = e.message
                if (msg != null) {
                    if (msg.contains("Unterminated string")) {
                        //println(src.path + " Failed, Unterminated string")
                        broken++
                    } else {
                        println(src.path + " Failed: " + msg)
                        others++
                    }
                }
            }
            if ((good%1000)==0) println("${good/1000}k files processed")
            true
        } catch (e: Exception) {
            println(src.path + " " + src.name + " FAILS:")
            print(e.message)
            false
        }
    }

    private fun readSR(src: File, target: String, fileName: String) : Boolean {
        val gson = Gson()
        return try {
            var str = src.readText(Charsets.UTF_8)
            var a : SRLotData?
            try {
                try {
                    a = gson.fromJson(str, SRLotData::class.java)
                } catch (e: JsonSyntaxException) {
                    val msg = e.message
                    if (msg != null && msg.contains("Invalid escape sequence")) {
                        escape++
                        str = str.replace("\\&", "&").replace("\'", "'")
                        //println(src.path + " Fixing: Invalid escape sequence")
                        a = gson.fromJson(str, SRLotData::class.java)
                        escapeFixed++
                    } else throw e
                }
                if (a == null) return true
                val gsonPretty = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
                //gsonPretty.toJson(a, FileWriter(target))
                a.fix()
                val fixed = StandardLotData("SR")
                fixed.assignSR(a)
                val s = gsonPretty.toJson(fixed)
                val file=File(target+a.catalogueId+"/"+fileName).also {
                    it.parentFile.mkdirs()
                }
                file.writeText(s)
                //println(gsonPretty.toJson(fixed))
                //return false
                good++
            } catch (e: Exception) {
                val msg = e.message
                if (msg != null) {
                    if (msg.contains("Unterminated string")) {
                        //println(src.path + " Failed, Unterminated string")
                        broken++
                    } else {
                        println(src.path + " Failed: " + msg)
                        others++
                    }
                }
            }
            if ((good%1000)==0) println("${good/1000}k files processed")
            true
        } catch (e: Exception) {
            println(src.path + " " + src.name + " FAILS:")
            print(e.message)
            false
        }
    }

}