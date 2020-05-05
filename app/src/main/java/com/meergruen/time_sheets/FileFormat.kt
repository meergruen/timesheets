package com.meergruen.time_sheets

enum class FileFormat {
    CSV,
    ORG
}


fun formatRow(row: ArrayList<String>, format: FileFormat): String {
    return when(format) {
        FileFormat.CSV -> row.joinToString(",")
        FileFormat.ORG -> row.joinToString(" | ", " | ", " | ")
    }
}


fun formatTable(columnNames: ArrayList<String>, table: ArrayList<ArrayList<String>>, fileFormat: FileFormat): String {
    var formatted = formatRow(columnNames, fileFormat)
    for (row in table) {
        formatted += formatRow(row, fileFormat)
    }
    return formatted
}
