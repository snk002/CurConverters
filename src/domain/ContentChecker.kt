import com.google.gson.Gson
import data.models.FinalLotSafeData
import domain.models.Sources
import java.io.File

/**
 * Checks for prices. If at least one found per dir then ok.
 * @param src source type [Sources]
 * @param baseDir source directory
 */
//TODO: add checks for other fields
class ContentChecker(val src: Sources, baseDir: String) {

    private val lotDir = "$baseDir/lot"
    private val target = "$baseDir/out"
    private val aucDir = "$baseDir/auction_id"

    fun checkFor() {
        var x = 0
        var y = 0
        File(target).mkdirs()
        File(lotDir).list().forEach { currentPath ->
            val cFile = File("$lotDir/$currentPath")
            if (cFile.isDirectory) {
                x++
                if (!readLotsDirectory(cFile)) {
                    //println("Not Found in $currentPath")
                    try {
                        File("$aucDir/$currentPath.json").copyTo(File("$target/$currentPath.json"), true)
                    } catch (e: NoSuchFileException) {
                        println("Not Found in $currentPath but $target/$currentPath.json not found")
                    }
                    y++
                }
            }
        }
        println("Total $x auctions checked. Not found in $y ")
    }

    /** returns true if first found  */
    private fun readLotsDirectory(srcDir: File): Boolean {
        var x = 0 // number of checked files, control for empty dirs
        srcDir.listFiles().forEach {
            if (it.extension != "json") return@forEach
            x++
            if (readAndCheckFile(it)) {
                //println("Total $x lots checked in "+srcDir.name)
                return true
            }
        }
        //println("Total $x lots checked in "+srcDir.name)
        return x == 0 //false only if really checked
    }

    /** returns true if found */
    private fun readAndCheckFile(srcFile: File): Boolean {
        val gson = Gson()
        return try {
            val str = srcFile.readText(Charsets.UTF_8)
            val lotLoaded: FinalLotSafeData = gson.fromJson(str, FinalLotSafeData::class.java)
            val price = lotLoaded.priceRealised.replace(",","").toDoubleOrNull() ?: 0.0
            price != 0.0
        } catch (e: Exception) {
            true //ignore bad files
        }
    }
}
