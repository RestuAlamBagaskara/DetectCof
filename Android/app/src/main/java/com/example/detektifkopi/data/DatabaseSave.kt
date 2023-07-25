package com.example.detektifkopi.data


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseSave (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "SaveResultDB"
        private const val TABLE_SCAN_RESULTS = "ScanResults"
        private const val KEY_ID = "id"
        private const val KEY_PENYAKIT = "penyakit"
        private const val KEY_VIRUS = "virus"
        private const val KEY_IMAGE_PATH = "image_path"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_SCAN_RESULTS " +
                "($KEY_ID INTEGER PRIMARY KEY, $KEY_PENYAKIT TEXT, $KEY_VIRUS TEXT, $KEY_IMAGE_PATH TEXT)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVer: Int, newVer: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SCAN_RESULTS")
        onCreate(db)
    }

    fun addScanResult(scanResult: ScanResult): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_PENYAKIT, scanResult.penyakit)
        values.put(KEY_VIRUS, scanResult.virus)
        values.put(KEY_IMAGE_PATH, scanResult.imagePath)

//        return db.insert(TABLE_SCAN_RESULTS, null, values)
        val id = db.insert(TABLE_SCAN_RESULTS, null, values) // Get the inserted id
        db.close() // Close the database connection
        return id // Return the inserted id
    }

    fun getAllScanResults(): List<ScanResult> {
        val scanResultsList = mutableListOf<ScanResult>()
        val selectQuery = "SELECT * FROM $TABLE_SCAN_RESULTS"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor != null && cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(KEY_ID)
            val penyakitIndex = cursor.getColumnIndex(KEY_PENYAKIT)
            val virusIndex = cursor.getColumnIndex(KEY_VIRUS)
            val imagePathIndex = cursor.getColumnIndex(KEY_IMAGE_PATH)

            do {
                val id = cursor.getLong(idIndex)
                val penyakit = cursor.getString(penyakitIndex)
                val virus = cursor.getString(virusIndex)
                val imagePath = cursor.getString(imagePathIndex)
                val scanResult = ScanResult(id, penyakit, virus, imagePath)
                scanResultsList.add(scanResult)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return scanResultsList
    }

    fun deleteScanResult(id: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_SCAN_RESULTS, "$KEY_ID=?", arrayOf(id.toString()))
    }
}