package com.example.notesapp.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.databinding.BottomSheetLayoutBinding
import com.example.notesapp.databinding.FragmentSaveBinding
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteFolder
import com.example.notesapp.viewModel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.yahiaangelo.markdownedittext.MarkdownEditText
import io.noties.markwon.Markwon
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

class SaveFragment : Fragment() {
    private var folderId: Int = 0
    private lateinit var binding: FragmentSaveBinding
    private lateinit var noteViewModel: NoteViewModel
    private var selectedColor: Int = -1 // Mặc định không có màu nền được chọn
    private val dateFormat = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.ENGLISH)
    private val currentDate = Date()
    private val currentDateTime = dateFormat.format(currentDate)
    lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSaveBinding.inflate(layoutInflater)
        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        folderId = arguments?.getInt("idFolder", 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etNote.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.bottomBar.visibility = View.VISIBLE
                binding.etNote.setStylesBar(binding.stylesBar)
            } else {
                binding.bottomBar.visibility = View.GONE
            }
        }

        binding.lastEdited.text = currentDateTime
        binding.fabColorPick.setOnClickListener { showBottomSheet() }
        binding.saveNote.setOnClickListener { saveNote() }
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString()
        val content = binding.etNote.text.toString()
        val noteColor = selectedColor

        note = Note(0, title, content, currentDateTime, noteColor, folderId)

        if (title.isNotEmpty() && content.isNotEmpty()){
            noteViewModel.addNote(note)
            findNavController().popBackStack()
        }else Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireActivity())
        val bottomSheetBinding = BottomSheetLayoutBinding.inflate(LayoutInflater.from(requireActivity()))
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        // Thiết lập màu nền cho các view dựa trên giá trị màu đã lưu
        selectedColor?.let { color ->
            binding.noteContentFragmentParent.setBackgroundColor(color)
            bottomSheetBinding.bottomSheetParent.setBackgroundColor(color)
            bottomSheetBinding.colorPicker.setSelectedColor(color)
            activity?.window?.statusBarColor = color
        }

        bottomSheetBinding.colorPicker.setOnColorSelectedListener { color ->
            selectedColor = color
            binding.noteContentFragmentParent.setBackgroundColor(color)
            bottomSheetBinding.bottomSheetParent.setBackgroundColor(color)
            activity?.window?.statusBarColor = color
        }
        // Lưu màu nền đã chọn
        selectedColor?.let { color ->
            this.selectedColor = color
        }
    }

}