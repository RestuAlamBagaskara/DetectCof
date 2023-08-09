package com.example.detektifkopi.fargment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.detektifkopi.*
import com.example.detektifkopi.adapter.ListArtikelAdapter
import com.example.detektifkopi.data.Artikel
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.*
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class Home : Fragment(), ListArtikelAdapter.OnItemClickCallback, BottomSheet.ImagePickerListener {
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var tfliteInterpreter: Interpreter
    private lateinit var tfliteUnquantInterpreter: Interpreter
    private lateinit var btnScan: MaterialButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        initializeTFLiteInterpreter()
        initializeUnquantTFLiteInterpreter()
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        val cardTutorial: CardView = view.findViewById(R.id.card_tutorial)

        //untuk CardArtikel Horizontal Scroll
        val dataJudul = resources.getStringArray(R.array.data_judul)
        val dataSubJudul = resources.getStringArray(R.array.data_sub)
        val dataLink = resources.getStringArray(R.array.data_link)
        val dataGambar = resources.obtainTypedArray(R.array.data_photo)
        val fragmentArtikel = FragmentArtikel()
        val itemList = mutableListOf<Artikel>()
        for (i in 0 until minOf(3, dataJudul.size)) {
            val judul = dataJudul[i]
            val subJudul = dataSubJudul[i]
            val link = dataLink[i]
            val gambar = dataGambar.getResourceId(i, 0)
            val item = Artikel(judul, subJudul, gambar, link)
            itemList.add(item)
        }
        val viewAll: TextView = view.findViewById(R.id.viewall)
        val padding = resources.getDimensionPixelSize(R.dimen.item_margin)
        val recyclerView: RecyclerView? = view?.findViewById(R.id.artikel_terkait)
        recyclerView?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.right = padding // Set right padding
            }
        })
        dataGambar.recycle()

        viewAll.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction()
                .replace(R.id.f1_wrapper, fragmentArtikel)
                .addToBackStack(null)
                .commit()
            bottomNavigationView.menu.findItem(R.id.navigation_artikel).isChecked = true
        }
        val adapter = ListArtikelAdapter(itemList)
        adapter.setOnItemClickCallback(this)
        recyclerView?.adapter = adapter

        //Card Cara Penggunaan
//        val youtubeLink = "https://youtu.be/TXUPx7P4ttI"
        cardTutorial.setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            val intent = Intent(requireContext(), tutorial::class.java)
            startActivity(intent)

        }
        //BottomNavigation
        val colorStateList = ContextCompat.getColorStateList(requireContext(), R.color.color_icon)
        bottomNavigationView.itemIconTintList = colorStateList
        bottomNavigationView.itemTextColor = colorStateList

        return view
    }

    //Action saat cardArtikel diklik
    override fun onItemClicked(data: Artikel) {
        val articleLink = data.link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleLink))
        startActivity(intent)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnScan = view.findViewById(R.id.btn_scan)
        //Saat tombol Scan diKlik tampil bottom sheet
        btnScan.setOnClickListener {
            val imagePickerBottomSheet = BottomSheet()
            imagePickerBottomSheet.setImagePickerListener(this@Home)
            imagePickerBottomSheet.show(parentFragmentManager, "image_picker_bottom_sheet")
        }
    }


    // Gambar dan Pemrosesannya
    override fun onGallerySelected() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onCameraSelected() {
        if (checkCameraPermission()) {
            openCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    private fun imageProcessing(bitmap: Bitmap) {
        val resizedImage = Bitmap.createScaledBitmap(bitmap, 300, 150, true)
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(resizedImage)

        val resizedUnquantImage = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val tensorUnquantImage = TensorImage(DataType.FLOAT32)
        tensorUnquantImage.load(resizedUnquantImage)
        val predictedUnquantLabel = runInferenceUnquant(tensorUnquantImage.tensorBuffer)
        Log.d("ini duan?", predictedUnquantLabel.toString())
        if (predictedUnquantLabel == 0) {
            val predictedLabel = runInference(tensorImage.tensorBuffer)
            Log.d("Predic tanpa trashold:", predictedLabel.toString())
            if (predictedLabel in 0..3) {
                val intent = Intent(requireContext(), HasilScanActivity::class.java)
                intent.putExtra("capturedImage", resizedImage)
                when (predictedLabel) {
                    0 -> intent.putExtra("resultLabel", "Miner")
                    1 -> intent.putExtra("resultLabel", "Sehat")
                    2 -> intent.putExtra("resultLabel", "Phoma")
                    3 -> intent.putExtra("resultLabel", "Rust")
                }
                startActivity(intent)
                // ... (kode sebelumnya untuk tampilan berdasarkan hasil prediksi)
            } else {
                val intent = Intent(requireContext(), ErrorActivity::class.java)
                startActivity(intent)
            }
        } else if (predictedUnquantLabel == 1) {
            val intent = Intent(requireContext(), ErrorActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(requireContext(), ErrorActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                //gambar dari kamera iso diproses tidak ng model ML
                CAMERA_REQUEST_CODE -> {
                    val capturedImage = data?.extras?.get("data") as Bitmap?
                    capturedImage?.let {
                        try {
                            imageProcessing(it)
                        } catch (e: IOException) {
                            Toast.makeText(
                                requireContext(),
                                "Error loading model",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                //gambar dari Galeri iso diproses tidak ng model ML
                GALLERY_REQUEST_CODE -> {
                    val imageUri = data?.data
                    imageUri?.let {
                        try {
                            val inputStream = requireContext().contentResolver.openInputStream(it)
                            val selectedImageBitmap = BitmapFactory.decodeStream(inputStream)
                            selectedImageBitmap?.let { bitmap ->
                                imageProcessing(bitmap)
                            }
                        } catch (e: IOException) {
                            Toast.makeText(
                                requireContext(),
                                "Error loading image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    //Menghubungkan dengan ML
    private fun loadModelFile(): MappedByteBuffer {
        try {
            val assetFileDescriptor =
                requireContext().assets.openFd("Coffee_disease_EfficientNetB0.tflite")
            val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        } catch (e: IOException) {
            throw RuntimeException("Error loading model", e)
        }
    }

    //menginisialisasi TFLiteInterpreter gawe menjalankan model
    private fun initializeTFLiteInterpreter() {
        val options = Interpreter.Options()
        tfliteInterpreter = Interpreter(loadModelFile(), options)
    }

    private fun loadUnquantModel(): MappedByteBuffer {
        try {
            val assetFileDescriptor =
                requireContext().assets.openFd("LeafAndNot.tflite")
            val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        } catch (e: IOException) {
            throw RuntimeException("Error loading model", e)
        }
    }

    private fun initializeUnquantTFLiteInterpreter() {
        val options = Interpreter.Options()
        tfliteUnquantInterpreter = Interpreter(loadUnquantModel(), options)
    }

    //proses prediksi model
    private fun runInference(inputFeature: TensorBuffer): Int {
        val inputShape = intArrayOf(1, 4)
        inputFeature.loadBuffer(inputFeature.buffer)
        val outputFeature = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
        tfliteInterpreter.run(inputFeature.buffer, outputFeature.buffer)
        val predictedLabels =
            outputFeature.floatArray.indices.maxByOrNull { outputFeature.floatArray[it] }
        Log.d("Predic penyakit:", predictedLabels.toString())
        return predictedLabels ?: -1

    }
    private fun runInferenceUnquant(inputFeature: TensorBuffer): Int {
        val inputShape = intArrayOf(1, 2)
        inputFeature.loadBuffer(inputFeature.buffer)
        val outputFeature = TensorBuffer.createFixedSize(inputShape, DataType.FLOAT32)
        tfliteUnquantInterpreter.run(inputFeature.buffer, outputFeature.buffer)

        val predictedUnquantLabels = outputFeature.floatArray
        val maxIndex = predictedUnquantLabels.indices.maxByOrNull { predictedUnquantLabels[it] }
        return if (maxIndex == 0) {
            0
        } else {
            1
        }
    }





    override fun onDestroy() {
        tfliteInterpreter.close()
        super.onDestroy()
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
        private const val GALLERY_REQUEST_CODE = 101
    }

}