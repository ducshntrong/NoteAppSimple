package com.example.notesapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapters.NoteAdapter
import com.example.notesapp.databinding.FragmentNoteBinding
import com.example.notesapp.model.Note
import com.example.notesapp.viewModel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import java.util.concurrent.TimeUnit


class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNoteBinding
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var listNote: List<Note>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNoteBinding.inflate(layoutInflater)
        noteAdapter = NoteAdapter(false)
        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration = 350
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        activity?.window?.statusBarColor = Color.parseColor("#9E9D9D")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        //set view theo chiều ngang, dọc
        when(resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> setUpRecycleView(2)
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecycleView(3)
        }

        showListNotes()
        deleteNote()

        binding.addNoteFab.setOnClickListener {
            findNavController().navigate(R.id.action_noteFragment_to_saveFragment)
        }
        binding.innerFab.setOnClickListener {
            findNavController().navigate(R.id.action_noteFragment_to_saveFragment)
        }
        binding.btnFolder.setOnClickListener {
            findNavController().navigate(R.id.action_noteFragment_to_folderFragment)
        }

        binding.appBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            if (verticalOffset.plus(totalScrollRange) == 0) {
                // AppBarLayout đã cuộn đến đỉnh
                binding.chatFabText.visibility = View.VISIBLE
            } else {
                // AppBarLayout không cuộn đến đỉnh
                binding.chatFabText.visibility = View.GONE
            }
        }

        binding.searchNote.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                noteViewModel.searchNote(query!!).observe(viewLifecycleOwner){
                    noteAdapter.setNoteList(it)
                }
                return true
            }
        })
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
        noteViewModel.readAllData.observe(viewLifecycleOwner){ note ->
            listNote = note
            if (note.isEmpty()) {
                binding.rvNotes.adapter = null
                binding.noData.visibility = View.VISIBLE
            }else {
                noteAdapter.setNoteList(listNote)
                binding.rvNotes.adapter = noteAdapter
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