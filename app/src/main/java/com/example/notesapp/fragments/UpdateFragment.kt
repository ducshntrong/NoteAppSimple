package com.example.notesapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.databinding.BottomSheetDeleteBinding
import com.example.notesapp.databinding.BottomSheetLayoutBinding
import com.example.notesapp.databinding.FragmentUpdateBinding
import com.example.notesapp.model.Note
import com.example.notesapp.viewModel.NoteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.yahiaangelo.markdownedittext.MarkdownEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpdateFragment : Fragment() {
    private lateinit var binding: FragmentUpdateBinding
    private lateinit var noteViewModel: NoteViewModel
    lateinit var note: Note
    private var selectedColor: Int = -1 // Mặc định không có màu nền được chọn
    //biến boolean để kiểm tra xem đã chọn màu hay chưa
    private var isColorSelected: Boolean = false
    private val dateFormat = SimpleDateFormat("dd/MM/yy hh:mm a", Locale.ENGLISH)
    private val currentDate = Date()
    private val currentDateTime = dateFormat.format(currentDate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        binding = FragmentUpdateBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        note = arguments?.getSerializable("note") as Note
        activity?.window?.statusBarColor = note.color
        return binding.root
    }

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
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        setView()
        binding.fabColorPick.setOnClickListener { showBottomSheet() }
        binding.backBtn.setOnClickListener { findNavController().navigateUp() }
        binding.updateNote.setOnClickListener { updateNote() }
        binding.moreNote.setOnClickListener { performOptionsMenuClick() }
    }

    private fun updateNote() {
        val title = binding.etTitle.text.toString()
        val content = binding.etNote.text.toString()
        val noteColor = if (isColorSelected) selectedColor else note.color

        val note = Note(note.id, title, content, currentDateTime, noteColor, note.folderId)
        if (title.isNotEmpty() && content.isNotEmpty()){
            noteViewModel.updateNote(note)
            findNavController().popBackStack()
        }else Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("SetTextI18n")
    private fun setView(){
        binding.etTitle.setText(note.title)
        binding.etNote.setText(note.content)
        binding.lastEdited.text = currentDateTime
        binding.noteContentFragmentParent.setBackgroundColor(note.color)
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun performOptionsMenuClick() {
        val popupMenu = PopupMenu(requireContext(), binding.moreNote)
        popupMenu.inflate(R.menu.menu_delete)

        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
            when(it.itemId){
                R.id.delete -> showBottomSheetDelete()
                R.id.share -> shareNote()
            }
            true
        })
        //hiển thị biểu tượng (icon) cho các menu item trong một PopupMenu trong Android
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenu)
        menu.javaClass
            .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            .invoke(menu, true)
        popupMenu.show()
    }

    private fun showBottomSheet() {
        val dialog = BottomSheetDialog(requireActivity())
        val bottomSheetBinding = BottomSheetLayoutBinding.inflate(LayoutInflater.from(requireActivity()))
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()

        bottomSheetBinding.bottomSheetContainer.setBackgroundColor(note.color)
        bottomSheetBinding.colorPicker.setSelectedColor(note.color)

        // Thiết lập màu nền cho các view dựa trên giá trị màu đã lưu
        //Nếu selectedColor khác null, biểu thức trong mệnh đề let sẽ được thực thi.
        selectedColor?.let { color ->
            if (isColorSelected) {//kiểm tra xem đã chọn màu hay chưa
                //Nếu isColorSelected là true, tức là màu đã được chọn trước đó
                binding.noteContentFragmentParent.setBackgroundColor(color)
                bottomSheetBinding.bottomSheetParent.setBackgroundColor(color)
                bottomSheetBinding.colorPicker.setSelectedColor(color)
                activity?.window?.statusBarColor = color
            }
        }

        bottomSheetBinding.colorPicker.setOnColorSelectedListener { color ->
            selectedColor = color
            isColorSelected = true
            binding.noteContentFragmentParent.setBackgroundColor(color)
            bottomSheetBinding.bottomSheetParent.setBackgroundColor(color)
            activity?.window?.statusBarColor = color
        }
        // Lưu màu nền đã chọn
        selectedColor?.let { color ->
            this.selectedColor = color
        }
    }

    private fun showBottomSheetDelete() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyleTheme)
        val bottomSheetDeleteBinding = BottomSheetDeleteBinding.inflate(LayoutInflater.from(requireActivity()))
        dialog.setContentView(bottomSheetDeleteBinding.root)
        dialog.show()

        bottomSheetDeleteBinding.btnCancel.setOnClickListener{ dialog.dismiss()}
        bottomSheetDeleteBinding.btnDelete.setOnClickListener{
            noteViewModel.deleteNote(note)
            dialog.dismiss()
            findNavController().popBackStack()
        }
    }

    private fun shareNote(){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, binding.etTitle.text.toString())
        shareIntent.putExtra(Intent.EXTRA_TEXT, binding.etNote.text.toString())
        try {
            startActivityForResult(Intent.createChooser(shareIntent, "Send Email"), 1)
        }catch (e:Exception){
            Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
        }
    }

}