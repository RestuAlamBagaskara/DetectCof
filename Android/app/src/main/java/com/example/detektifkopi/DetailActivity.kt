package com.example.detektifkopi

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.baseline_arrow_back_24, null)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Detail"
            setDisplayHomeAsUpEnabled(true)
        }

        val namaPenyakit = intent.getStringExtra("resultLabel")
        // Get the extras from the intent
        val capturedImage: Bitmap? = intent.getParcelableExtra("capturedImage")
        val imageView: ImageView = findViewById(R.id.img_save)
        imageView.setImageBitmap(capturedImage)

        // Atur teks pada text view berdasarkan nama penyakit
        val penyakit: TextView = findViewById(R.id.tv_penyakit_save)
        val des: TextView = findViewById(R.id.desc_save)
        val cegah: TextView = findViewById(R.id.tv_pencegahan_save)
        val obat: TextView = findViewById(R.id.tv_obat_save)
        when (namaPenyakit) {
            "Miner" -> {
                penyakit.text = getString(R.string.miner)
                des.text = getString(R.string.desc_miner)
                cegah.text = getString(R.string.cegah_miner)
                obat.text = getString(R.string.obat_miner)
            }
            "Rust" -> {
                penyakit.text = getString(R.string.rust)
                des.text = getString(R.string.desc_rust)
                cegah.text = getString(R.string.cegah_rust)
                obat.text = getString(R.string.obat_rust)
            }
            "Phoma"-> {
                penyakit.text = getString(R.string.phoma)
                des.text = getString(R.string.desc_phoma)
                cegah.text = getString(R.string.cegah_phoma)
                obat.text = getString(R.string.obat_phoma)
            }
            "Tanamanmu Sehat" -> {
                penyakit.text = getString(R.string.sehat)
                des.text = getString(R.string.desc_sehat)
                cegah.text = getString(R.string.perawatan)
                obat.text = getString(R.string.obat_sehat)
            }
        }

        //ellipsize Deskripsi
        val maxDesc = 5
        val oriDesc = des.text.toString()
        des.text = getEllipsizedText(oriDesc, maxDesc)
        des.setOnClickListener {
            if (des.maxLines == maxDesc) {
                des.text = oriDesc
                des.maxLines = Int.MAX_VALUE
            } else {
                des.text = getEllipsizedText(oriDesc, maxDesc)
                des.maxLines = maxDesc
            }
        }

//        ellipsize mitigasi
        val max2 = 2 // Tentukan jumlah maksimum baris teks sebelum elipsize
        val oriMitigasi = cegah.text.toString()
        cegah.text = getEllipsizedText(oriMitigasi, max2)
        cegah.setOnClickListener {
            if (cegah.maxLines == max2) {
                cegah.text = oriMitigasi
                cegah.maxLines = Int.MAX_VALUE
            } else {
                cegah.text = getEllipsizedText(oriMitigasi, max2)
                cegah.maxLines = max2
            }
        }

        //ellipsize obat
        val oriObat = obat.text.toString()
        obat.text = getEllipsizedText(oriObat, max2)
        obat.setOnClickListener {
            if (obat.maxLines == max2) {
                obat.text = oriObat
                obat.maxLines = Int.MAX_VALUE
            } else {
                obat.text = getEllipsizedText(oriObat, max2)
                obat.maxLines = max2
            }
        }
    }
    private fun getEllipsizedText(text: String, maxLines: Int): CharSequence {
        val textView = TextView(this)
        textView.textSize = 14f
        textView.maxLines = maxLines
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.text = text
        return textView.text
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}