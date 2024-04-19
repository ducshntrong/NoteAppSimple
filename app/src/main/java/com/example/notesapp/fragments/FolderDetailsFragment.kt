package com.example.notesapp.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapters.NoteAdapter
import com.example.notesapp.databinding.FragmentFolderDetailsBinding
import com.example.notesapp.model.NoteFolder
import com.example.notesapp.viewModel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import java.util.concurrent.TimeUnit

class FolderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentFolderDetailsBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteViewModel: NoteViewModel
    lateinit var folder: NoteFolder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFolderDetailsBinding.inflate(layoutInflater)
        noteAdapter = NoteAdapter(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.window?.statusBarColor = Color.parseColor("#9E9D9D")
        folder = arguments?.getSerializable("folder") as NoteFolder
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> setUpRecycleView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecycleView(3)
        }
        showListNotes()
        deleteNote()

        binding.tvFolderName.text = folder.name

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnAdd.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("idFolder", folder.folderId)
            findNavController().navigate(R.id.action_folderDetailsFragment_to_saveFragment, bundle)
        }
    }

    private fun setUpRecycleView(spanCount: Int) {
        binding.rvNotes.setHasFixedSize(true)
        binding.rvNotes.setItemViewCacheSize(10)
        binding.rvNotes.layoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        //thiết lập chính sách khôi phục trạng thái của Adapter để ngăn khôi phục trạng thái khi RecyclerView trống
        noteAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvNotes.adapter = noteAdapter
        postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
        showListNotes()
    }

    private fun showListNotes() {
        noteViewModel.getNoteByFolderId(folder.folderId).observe(viewLifecycleOwner){ note ->
            if (note.isEmpty()) {
                binding.rvNotes.adapter = null
                binding.noData.visibility = View.VISIBLE
            }else {
                noteAdapter.setNoteList(note)
                binding.rvNotes.adapter = noteAdapter
//                binding.rvNotes.requestLayout()
                binding.noData.visibility = View.GONE
            }
        }
    }

    private fun deleteNote(){
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = noteAdapter.getNoteByPosition(position)
                noteViewModel.deleteNote(note)
                Snackbar.make(requireView(), "Note was delete", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        noteViewModel.addNote(note)
                    }.show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvNotes)
    }
}