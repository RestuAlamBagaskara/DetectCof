package com.example.detektifkopi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar

class tutorial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        val toolbar: Toolbar = findViewById(R.id.tback)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.baseline_arrow_back_24, null)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Tutorial"
            setDisplayHomeAsUpEnabled(true)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}