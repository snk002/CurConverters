package domain.models

/**
 * General auction entity.
 *
 * Currency & premium used to help filling lots data
 * All fields together are used for make houses or auctions lists.
**/
abstract class Auction {
    var id: String = ""
    abstract var code: String
    abstract var currency: String
    abstract var premium: String
    abstract val site: String?
    abstract val houseName: String
    abstract val country: String
    abstract val city: String

    /** Represents [Auction] as single CSV-formatted String */
    abstract fun toCSV() : String

    /** Extract data to make Houses list */
    abstract fun toHouse(src: Sources, id: Int) : House

    /** Extract house code from auction code (currently for SR only) */
    abstract fun fixCode(value: String)

    /**
     * Generate unique house key based on source and local code
     * @param src source [Sources] */
    fun getHouseKey(src: Sources) = "$src:$code"
}
