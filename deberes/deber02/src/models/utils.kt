package models

fun advancedCompareOfSearch(original: String, search: String): Boolean{

    return ".*${search.toLowerCase().trim()}.*".toRegex().containsMatchIn(original.toLowerCase().trim())
}




