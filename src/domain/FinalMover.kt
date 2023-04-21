import com.google.gson.Gson
import data.models.FinalLotData
import domain.FinalTools
import domain.models.Sources
import java.io.File

/***
 * Read json from the final format (FinalFiller output).
 * Checks for global (own) House ID and auction date.
 * Files saved to final/all directory, directory tree build by globalId/date
 ***/

class FinalMover(private val src: Sources, srcDir: String, target: String) : FinalTools(srcDir, target) {

    private var testlot = 0
    private var testlotLog = File(target+"_testlot.txt")
    private var nocodeLog = File(target+"_nocode.txt")
    private var nodateLog = File(target+"_nodate.txt")

    override fun moveJson()  {
        var x = 0
        File(target).mkdirs()
        println("Reading from $srcDir, writing to $target")
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
        println("Total $x files. Found $broken unterminated strings, $nocode haven't ownId, $nodate haven't date, $testlot tests and $others other issues")
    }

    override fun readAndCopy(srcFile: File) : Boolean {
        val gson = Gson()
        return try {
            val str = srcFile.readText(Charsets.UTF_8)
            val lot: FinalLotData = gson.fromJson(str, FinalLotData::class.java)
            val dirName = lot.globalHouseCode.toString() //100003
            val subDirName = lot.saleDate //2018-12-10
            if (dirName == "") {
                nocodeLog.appendText(srcFile.path+"\n")
                nocode++
                return false
            }
            if (subDirName == "") {
                nodateLog.appendText(srcFile.path+"\n")
                nodate++
                return false
            }
            if (lot.lotDescription.contentEquals("test", true) || lot.lotTitle.contentEquals("test", true)) {
                testlot++
                testlotLog.appendText(srcFile.path+"\n")
                return true
            }
            File("$target/$dirName/$subDirName").mkdirs()
            srcFile.copyTo(File("$target/$dirName/$subDirName/"+srcFile.name), overwrite = true)
            true
        } catch (e: Exception) {
            val msg = e.message
            if (msg != null) {
                if (msg.contains("Unterminated string")) {
                    //println(src.path + " Failed, Unterminated string")
                    broken++
                } else {
                    println(srcFile.path + " Failed: " + msg)
                    others++
                }
            }
            true
        }
    }

}