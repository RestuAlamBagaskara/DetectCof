package com.example.detektifkopi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton


class BottomSheet : BottomSheetDialogFragment() {

    private lateinit var btnGallery: MaterialButton
    private lateinit var btnCamera: MaterialButton
    private lateinit var listener: ImagePickerListener
    private lateinit var bottomSheetDialog: BottomSheetDialog


    interface ImagePickerListener {
        fun onGallerySelected()
        fun onCameraSelected()
    }

    fun setImagePickerListener(listener: ImagePickerListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
        btnGallery = view.findViewById(R.id.btn_gallery)
        btnCamera = view.findViewById(R.id.btn_camera)


        btnGallery.setOnClickListener {
            listener.onGallerySelected()
        }

        btnCamera.setOnClickListener {
            listener.onCameraSelected()
        }
        return view
    }


}