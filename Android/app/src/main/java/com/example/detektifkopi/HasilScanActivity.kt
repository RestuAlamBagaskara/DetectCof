package com.example.detektifkopi

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.detektifkopi.data.DatabaseSave
import com.example.detektifkopi.data.ScanResult
import com.google.android.material.button.MaterialButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class HasilScanActivity : AppCompatActivity() {
    private lateinit var imgResult: ImageView
    private lateinit var tvPenyakit: TextView
    private lateinit var tvJamur: TextView
    private lateinit var tvDeskripsi: TextView
    private lateinit var tvPencegahan: TextView
    private lateinit var tvObat: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_scan)

        imgResult = findViewById(R.id.img_hasil)
        tvPenyakit = findViewById(R.id.tv_penyakit)
        tvJamur = findViewById(R.id.nama_jamur)
        tvDeskripsi = findViewById(R.id.deskripsi_singkat)
        tvPencegahan = findViewById(R.id.tv_pencegahan)
        tvObat = findViewById(R.id.tv_obat)


        val capturedImageBitmap = intent.getParcelableExtra<Bitmap>("capturedImage")
        val resultLabel = intent.getStringExtra("resultLabel")

        imgResult.setImageBitmap(capturedImageBitmap)
//        imgResult.scaleType = ImageView.ScaleType.FIT_XY

        // Set the text on the TextView based on the result label
        tvPenyakit.text = when (resultLabel) {
            "Cerscospora" -> getString(R.string.cerscospora)
            "Miner" -> getString(R.string.miner)
            "Sehat" -> getString(R.string.sehat)
            "Phoma" -> getString(R.string.phoma)
            "Rust" -> getString(R.string.rust)
            else -> ""
        }
        tvJamur.text = when (resultLabel) {
            "Cerscospora" -> getString(R.string.virus_cerscospora)
            "Miner" -> getString(R.string.virus_miner)
            "Sehat" -> getString(R.string.tdk_sakit)
            "Phoma" -> getString(R.string.virus_phoma)
            "Rust" -> getString(R.string.virus_rust)
            else -> ""
        }
        tvDeskripsi.text = when (resultLabel) {
            "Cerscospora" -> getString(R.string.desc_cerscospora)
            "Miner" -> getString(R.string.desc_miner)
            "Sehat" -> getString(R.string.desc_sehat)
            "Phoma" -> getString(R.string.desc_phoma)
            "Rust" -> getString(R.string.desc_rust)
            else -> ""
        }
        tvPencegahan.text = when (resultLabel) {
            "Cerscospora" -> getString(R.string.cegah_cerscospora)
            "Miner" -> getString(R.string.cegah_miner)
            "Sehat" -> getString(R.string.perawatan)
            "Phoma" -> getString(R.string.cegah_phoma)
            "Rust" -> getString(R.string.cegah_rust)
            else -> ""
        }

        tvObat.text = when (resultLabel) {
            "Cerscospora" -> getString(R.string.obat_cerscospora)
            "Miner" -> getString(R.string.obat_miner)
            "Sehat" -> getString(R.string.obat_sehat)
            "Phoma" -> getString(R.string.obat_phoma)
            "Rust" -> getString(R.string.obat_rust)
            else -> ""
        }


        //ellipsize Deskripsi
        val maxDesc = 5
        val oriDesc = tvDeskripsi.text.toString()
        tvDeskripsi.text = getEllipsizedText(oriDesc, maxDesc)
        tvDeskripsi.setOnClickListener {
            if (tvDeskripsi.maxLines == maxDesc) {
                tvDeskripsi.text = oriDesc
                tvDeskripsi.maxLines = Int.MAX_VALUE
            } else {
                tvDeskripsi.text = getEllipsizedText(oriDesc, maxDesc)
                tvDeskripsi.maxLines = maxDesc
            }
        }

//        ellipsize mitigasi
        val max2 = 2 // Tentukan jumlah maksimum baris teks sebelum elipsize
        val oriMitigasi = tvPencegahan.text.toString()
        tvPencegahan.text = getEllipsizedText(oriMitigasi, max2)
        tvPencegahan.setOnClickListener {
            if (tvPencegahan.maxLines == max2) {
                tvPencegahan.text = oriMitigasi
                tvPencegahan.maxLines = Int.MAX_VALUE
            } else {
                tvPencegahan.text = getEllipsizedText(oriMitigasi, max2)
                tvPencegahan.maxLines = max2
            }
        }

        //ellipsize obat
        val oriObat = tvObat.text.toString()
        tvObat.text = getEllipsizedText(oriObat, max2)
        tvObat.setOnClickListener {
            if (tvObat.maxLines == max2) {
                tvObat.text = oriObat
                tvObat.maxLines = Int.MAX_VALUE
            } else {
                tvObat.text = getEllipsizedText(oriObat, max2)
                tvObat.maxLines = max2
            }
        }

        val btnSave = findViewById<MaterialButton>(R.id.btn_save)

        btnSave.setOnClickListener {
            val penyakitValue = tvPenyakit.text.toString()
            val virusValue = tvJamur.text.toString()
//            val imagePath = saveImageToInternalStorage(capturedImageBitmap)

            val capturedImageBitmap = intent.getParcelableExtra<Bitmap>("capturedImage")
            val imagePath = saveImageToInternalStorage(capturedImageBitmap)

            val dbHandler = DatabaseSave(this)
//            val scanResult = ScanResult(penyakitValue, virusValue, imagePath)
            // Create a new ScanResult object with id set to 0 (assuming it will be auto-generated by the database)
            val scanResult = ScanResult(id = 0, penyakitValue, virusValue, imagePath)
            val insertedId = dbHandler.addScanResult(scanResult)

            Log.d(insertedId.toString(), "Data berhasil disimpan dengan id: ")
            Toast.makeText(this, getString(R.string.succes), Toast.LENGTH_SHORT).show()
        }

    }

    private fun saveImageToInternalStorage(bitmap: Bitmap?): String {
        val wrapper = ContextWrapper(applicationContext)
        val file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        val imageFile = File(file, "image_${System.currentTimeMillis()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(imageFile)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageFile.absolutePath
    }

    private fun getEllipsizedText(text: String, maxLines: Int): CharSequence {
        val textView = TextView(this)
        textView.textSize = 14f
        textView.maxLines = maxLines
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.text = text
        return textView.text
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

}