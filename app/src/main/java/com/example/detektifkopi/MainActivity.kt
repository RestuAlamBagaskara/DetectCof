package com.example.detektifkopi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.detektifkopi.fargment.AboutFragment
import com.example.detektifkopi.fargment.FragmentArtikel
import com.example.detektifkopi.fargment.Home
import com.example.detektifkopi.fargment.Tersimpan
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = Home()
        val artikel = FragmentArtikel()
        val tersimpan = Tersimpan()
        val about = AboutFragment()

        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    setCurrentFragment(homeFragment)
                    bottomNavigationView.menu.findItem(R.id.navigation_home).isChecked = true
                    Log.i(TAG, "Home")
                }
                R.id.navigation_artikel -> {
                    setCurrentFragment(artikel)
                    bottomNavigationView.menu.findItem(R.id.navigation_artikel).isChecked = true
                    Log.i(TAG, "Artikel")
                }
                R.id.navigation_simpan -> {
                    setCurrentFragment(tersimpan)
                    Log.i(TAG, "Tersimpan")
                }
                R.id.navigation_About -> {
                    setCurrentFragment(about)
                    Log.i(TAG, "About")
                }

            }
            val colorStateList = ContextCompat.getColorStateList(this, R.color.color_icon)
            bottomNavigationView.itemIconTintList = colorStateList
            bottomNavigationView.itemTextColor = colorStateList

            true
        }
        setCurrentFragment(homeFragment)
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finishAffinity()

    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.f1_wrapper, fragment)
            commit()
        }
        when (fragment) {
            is Home -> bottomNavigationView.menu.findItem(R.id.navigation_home).isChecked = true
            is FragmentArtikel -> bottomNavigationView.menu.findItem(R.id.navigation_artikel).isChecked = true
            is Tersimpan -> bottomNavigationView.menu.findItem(R.id.navigation_simpan).isChecked = true
        }
    }



}

