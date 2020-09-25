package models

import java.util.*

fun advancedCompareOfSearch(original: String, search: String): Boolean{

    return ".*${search.toLowerCase(Locale.ROOT).trim()}.*".toRegex().containsMatchIn(original.toLowerCase(
        Locale.ROOT
    ).trim()
    )
}




