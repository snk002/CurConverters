package domain.models

/**
 * House entity
 */
data class House(
    val ownId: Int,
    val localId: String,
    val ignored: Int, // 0 = ok, 1 = double, 2 = test/skip
    val name: String,
    val country: String,
    val city: String,
    val currency: String,
    val premium: Double,
    val site: String,
    val src: String
) {
    /** Represents [House] as single CSV-formatted String */
    fun toCSV() : String {
        return "$ownId\t$localId\t0\t$name\t$country\t$city\t$currency\t$premium\t$site\t$src"
    }
}
