import domain.models.Sources

fun printHelp(src: Sources?) {
    val msg = "Select option to do with"
    if (src == null) println("Select source to work:\n 1 - SaleRoom\n 2 - Invaluable\n 0 to exit.")
    else when (src) {
        Sources.SR -> println("$msg $src:\n 1 - normalize JSON (old)\n 2 - make houses list\n 3 - final check only\n" +
                " 4 - final check, fill and copy\n 5 - move to final joined directory\n 6 - check for price\n" +
                " 7 - extract auctions data to csv\n" +
                " 8 - move images only\n 9 - make thumbnails from images\n" +
                " 0 to exit.")
        Sources.IN -> println("$msg $src:\n 2 - make houses list\n 3 - final check only\n" +
                " 4 - final check, fill and copy\n 5 - move to final joined directory\n" +
                " 7 - extract auctions data to csv\n" +
                " 0 to exit.")
        Sources.LA -> println("$msg $src:\n 3 - final check, fill and copy\n 5 - move to final joined directory\n" +
                " 0 to exit.")
        Sources.ALL -> println("1 - Post al to elastic")
    }
}

fun stringShorter(s: String, maxLen: Int) : String {
    if (s.length <= maxLen) return s
    return s.dropLast(s.length - maxLen).dropLastWhile { it.isLetter() }.dropLast(1).plus("...")
}