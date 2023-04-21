package legacy

/**
 * Basic lot entity interface for Legacy!
 */
interface BaseLot {

    /** Platform code based on [Sources]*/
    val platformCode: String

    /** save to file */
    fun save(file: String)

    /** to fix some possible issues before save */
    fun fix()
}