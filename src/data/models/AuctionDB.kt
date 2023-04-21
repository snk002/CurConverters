package data.models

/**
 * Database auction entity
 */
data class AuctionDB (
    var globalid: String = "",
    var localid: String = "",
    var houseid: String = "",
    var houselocal: String = "",
    var title: String = "",
    var date: String = "",
    var hasprices: Boolean = false,
    var hasimages: Boolean = false,
    var lotcount: Int = 0,
    var source: String = "",
    var premium: Double = 0.0,
    ) {

    fun toCSV() : String {
        val prc = if (hasprices) 1 else 0
        val img = if (hasimages) 1 else 0
        return "$globalid\t$source\t$localid\t$houseid\t$houselocal\t$date\t$prc\t$img\t$lotcount\t$premium\t$title"
    }
}
