import AppClass.AppData.houses
import AppClass.AppData.maxHouseNumbers
import com.google.gson.Gson
import data.providers.AuctionBaseReader
import domain.models.House
import domain.models.Sources
import java.io.File

/***
 * Read auctions, extract and collect houses data, put new houses to file, if found.
 */
class HouseExtractor(src: Sources, srcDir: String, target: String) : AuctionBaseReader(src, srcDir, target) {

    /** Collection to store new houses */
    private val newHouses: HashMap<String, House> = hashMapOf()

    /** Read dir and apply action to each file. Call this to start operation */
    fun extractHouses() {
        if (AppClass.readHouses()<1) return
        File(target).mkdirs()
        readAuctions().also { x ->
            if (x<=0) return
            println("$x done. ${newHouses.count()} found / ${houses.count()}")
        }
        "$target${src}_New_Houses.csv".also {
            if (saveFound(it))
                println("Extracted data saved to $it")
        }
    }

    /**
     * Read auction and extract data required for Houses list.
     * Puts new founded records into the newHouses collection.
     * @param file auction json file
     * */
    override fun readAuction(file: File): Boolean {
        val gson = Gson()
        return try {
            val str = file.readText(Charsets.UTF_8)
            val a = gson.fromJson(str,getClassType())
            a.fixCode(file.nameWithoutExtension) // for SR. ignored for IN
            val key = a.getHouseKey(src)
            houses.getOrElse(key) {
                a.toHouse(src, setAndGetMaxId()).also {
                    houses[key] = it
                    newHouses[key] = it
                }
                println(key + " added from " + file.name)
            }
            true
        } catch (e: Exception) {
            //println(file.name +" "+e.message)
            true
        }
    }

    /** Save all _new_ found houses into a CSV file */
    override fun saveFound(destFile: String) : Boolean = try {
        File(destFile).printWriter().use { writer ->
            newHouses.forEach {
                writer.println(it.value.toCSV())
            }
        }
        true
    } catch (e: java.lang.Exception) {
        false
    }

    /** Set a new id for current source and return it */
    private fun setAndGetMaxId() : Int {
        var x = maxHouseNumbers[src] ?: 0
        maxHouseNumbers[src] = ++x
        println("Max id set to $x")
        return x
    }
}