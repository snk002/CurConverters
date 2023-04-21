import AppClass.AppData.houses
import AppClass.AppData.PREF_PATH
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import data.mappers.asFinalLotData
import data.models.FinalLotSafeData
import domain.FinalTools
import domain.models.Sources
import java.io.File
import java.nio.charset.Charset

/***
 * Read json from the latest, fixed parser (in actual format),
 * check/set local house ID and global (own) House ID,
 * also checks for a lot name, description and date.
 * Errors output to logs in the final dir with name SRC_CODE+errorName.txt
 * Files saved to final/SRC_CODE directory, directory tree stay as is.
 * @param src Source (SR, IN, etc)
 * @param srcDir path to source lots folder
 * @param target path to target lots folder
 ***/

class FinalFiller(private val src: Sources, srcDir: String, target: String) : FinalTools(srcDir, target) {

    private var brokenLog = File(target+"_broken.txt")
    private var othersLog = File(target+"_others.txt")
    private var nocodeLog = File(target+"_nocode.txt")
    private var nodateLog = File(target+"_nodate.txt")
    private var noglobal = 0
    private var noglobalLog = File(target+"_noglobal.txt")
    private var notitle = 0
    private var notitleLog = File(target+"_notitle.txt")
    private var nodescr = 0
    private var nodescrLog = File(target+"_nodescr.txt")
    private var testlot = 0
    private var testlotLog = File(target+"_testlot.txt")
    private var fixNoTitle = 0
    private var fixNoDescr = 0

    private val gsonPretty: Gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    private val secondTarget = target+"_sec" //=lot_final_sec

    private var testMode = false
    private var ignored = 0
    private var okay = 0

    // Collection to fix date in Invaluable lots based on auction data
    private val auctionDates: HashMap<String, String> = hashMapOf()

    fun testJson() {
        testMode = true
        moveJson()
    }

    override fun moveJson()  {
        var x = 0
        if (AppClass.readHouses()<1) return
        File(target).mkdirs()
        File(secondTarget).mkdirs()
        val act = if (testMode) "test only" else "writing to $target & $secondTarget"
        println("Reading from $srcDir, $act")
        if (src == Sources.IN) loadAuctionDates()
        print("Continue? [Y/n]")
        if (readLine().toString().contentEquals("n",true)) return
        File(srcDir).walk().forEach {
            if (!it.isFile) return@forEach
            if ((src!=Sources.IN && it.extension != "json") || (src==Sources.IN && it.extension.isNotBlank())) return@forEach
            x++
            if ( x % 100000 == 0) logProcessed(x)
            if (!readAndCopy(it)) {
                println("Unexpected error on the $x iteration")
                return
            }
        }
        println("Total $x files, $okay is ok. Found $broken unterminated strings, $nocode haven't ownId, $noglobal - no global id, $nodate - date," +
                " $notitle - title, $nodescr - description, $testlot tests and $others other issues. Ignored $ignored.")
        if (!testMode && (fixNoDescr>0 || fixNoTitle>0)) println("Fixed $fixNoTitle titles and $fixNoDescr descriptions.")
    }

    override fun readAndCopy(srcFile: File) : Boolean {
        val gson = Gson()
        return try {
            val str = srcFile.readText(Charsets.UTF_8)
            val lotLoaded: FinalLotSafeData = gson.fromJson(str, FinalLotSafeData::class.java)
            val lot = lotLoaded.asFinalLotData()
            // Fix possible error with "-" suffix for SR only
            if (src == Sources.SR) {
                lot.houseCode = lot.saleId.dropLast(5)
            }
            if (lot.houseCode == "") {
                nocode++
                nocodeLog.appendText(srcFile.path+"\n")
                return true
            }
            val house = houses[src.toString()+":"+lot.houseCode]
            if (house == null) {
                noglobal++
                noglobalLog.appendText(srcFile.path+"\n")
                return true
            } else {
                lot.globalHouseCode = house.ownId
            }
            if (house.ignored == 2 || lot.lotCategory.contentEquals("test", true)) {
                testlot++
                testlotLog.appendText(srcFile.path+"\n")
                return true
            }
            if (lot.saleDate == "") {
                nodate++
                nodateLog.appendText(srcFile.path+"\n")
                return true
            }
            if (lot.lotTitle == "") {
                notitle++
                notitleLog.appendText(srcFile.path+"\n")
                if (testMode) return true
                //not test, try to fix
                if (lot.lotDescription.isNotBlank()) {
                    lot.lotTitle = stringShorter(lot.lotDescription, 80)
                    fixNoTitle++
                }
                else return true
            }
            if (lot.lotDescription == "") {
                nodescr++
                nodescrLog.appendText(srcFile.path + "\n")
                if (testMode) return true
                //not test, try to fix
                if (lot.lotTitle.isNotBlank()) {
                    lot.lotDescription = lot.lotTitle
                    fixNoDescr++
                }
                else return true
            }
            if (lot.lotDescription.contentEquals("test", true) || lot.lotTitle.contentEquals("test", true)) {
                testlot++
                testlotLog.appendText(srcFile.path+"\n")
                return true
            }
            //fix date for IN
            if (src == Sources.IN) lot.saleDate = auctionDates[lot.saleId] ?: lot.saleDate
            if (!testMode) {
                if (lot.premium == 0.0) lot.premium = house.premium
                val s = gsonPretty.toJson(lot)
                val path = if (house.ignored == 1) {ignored++; secondTarget} else target
                File("$path/${lot.saleId}/${srcFile.name}").also {
                    it.parentFile.mkdirs()
                    it.writeText(s)
                    okay++ //calc only if copy success
                }
            } else okay++ //if no copy then calc here
            true
        } catch (e: Exception) {
            val msg = e.message
            if (msg != null) {
                if (msg.contains("Unterminated string")) {
                    broken++
                    brokenLog.appendText(srcFile.path+"\n")
                } else {
                    others++
                    othersLog.appendText(srcFile.path+"\n")
                }
            }
            true //false
        }
    }

    private fun loadAuctionDates() {
        val lines = File("$PREF_PATH/IN_dates.csv").readLines(Charset.defaultCharset())
        var x = 0
        var y = 0
        lines.forEach { line: String ->
            x++
            val item = line.split("\t")
            if (item.size>2) {
                auctionDates[item[0]] = item[2]
                y++
            }
        }
        println("$y of $x auction dates loaded.")
    }
}
