package com.example.detektifkopi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.example.detektifkopi.adapter.OnBoardingAdapter
import com.example.detektifkopi.data.OnBoardingData

class OnBoarding : AppCompatActivity() {
    //inisialisasi
    private lateinit var judul1: String
    private lateinit var subJudul1: String
    private lateinit var judul2: String
    private lateinit var subJudul2: String
    private lateinit var judul3: String
    private lateinit var subJudul3: String

    private lateinit var onBoardingAdapter: OnBoardingAdapter
    var next: TextView? = null
    var skip: TextView? = null
    var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        next = findViewById(R.id.btn_next)
//        skip = findViewById(R.id.skip)
        //inisialisasi
        judul1 = getString(R.string.selamat_dat)
        subJudul1 = getString(R.string.kami_senang)
        judul2 = getString(R.string.slide2)
        subJudul2 = getString(R.string.subTitleSlide2)
        judul3 = getString(R.string.title_slide3)
        subJudul3 = getString(R.string.sub_title_Slide3)

        checkFirstRun()

        onBoardingAdapter = OnBoardingAdapter(
            listOf(
                OnBoardingData(judul1, subJudul1, R.drawable.onboard1),
                OnBoardingData(judul2, subJudul2, R.drawable.onboard3),
                OnBoardingData(judul3, subJudul3, R.drawable.onboard2),
            )
        )


        val screenPager = findViewById<ViewPager2>(R.id.screenPager)
        screenPager.adapter = onBoardingAdapter
        setupIndicator()
        setCurrentIndicator(0)
        screenPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        if (restorePrefData()) {
            val i = Intent(applicationContext, MainActivity::class.java)
            startActivity(i)
            finish()
        }
        val skip = findViewById<TextView>(R.id.skip)
        skip.setOnClickListener {
            screenPager.setCurrentItem(onBoardingAdapter.itemCount - 1, true)
        }
        next?.setOnClickListener {
            val currentPosition = screenPager.currentItem
            if (currentPosition < onBoardingAdapter.itemCount - 1) {
                screenPager.setCurrentItem(currentPosition + 1, true)
            } else {
                savePrefData()
                val i = Intent(applicationContext, MainActivity::class.java)
                startActivity(i)
            }
        }

        screenPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)

                if (position == onBoardingAdapter.itemCount - 1) {
                    next?.text =  getString(R.string.mulai)
                    skip?.text =  getString(R.string.empty)
                } else {
                    next?.text =  getString(R.string.next)
                    skip?.text =  getString(R.string.skip)
                }
            }
        })
    }


    private fun setupIndicator() {
        val indicators = arrayOfNulls<ImageView>(onBoardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(10, 0, 10, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.unselected
                    )
                )
                this?.layoutParams = layoutParams
            }

            val indikator = findViewById<LinearLayout>(R.id.indikator)
            indikator.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val indikator = findViewById<LinearLayout>(R.id.indikator)
        val childCount = indikator.childCount
        for (i in 0 until childCount) {
            val imageView = indikator[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.selected)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.unselected)
                )
            }
        }
    }

    private fun savePrefData() {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putBoolean("Pertama", true)
        editor.apply()
    }

    private fun restorePrefData(): Boolean {
        sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        return sharedPreferences!!.getBoolean("pertama", false)
    }

    private fun checkFirstRun() {
        val sharedPreferences = applicationContext.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("first_run", true)
        if (isFirstRun) {
            // Pertama kali dijalankan, jalankan onboarding
            val editor = sharedPreferences.edit()
            editor.putBoolean("first_run", false)
            editor.apply()
        } else {
            // Bukan pertama kali dijalankan, langsung ke MainActivity
            val intent = Intent(applicationContext, SplashScreen::class.java)
            startActivity(intent)
            finish()
        }
    }

}
