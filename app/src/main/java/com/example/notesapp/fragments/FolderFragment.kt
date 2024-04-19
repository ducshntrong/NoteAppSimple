package com.example.notesapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.adapters.FolderAdapter
import com.example.notesapp.databinding.AddFolderBinding
import com.example.notesapp.databinding.FragmentFolderBinding
import com.example.notesapp.model.NoteFolder
import com.example.notesapp.viewModel.FolderViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale


class FolderFragment : Fragment() {
    private lateinit var binding: FragmentFolderBinding
    private lateinit var folderViewModel: FolderViewModel
    private lateinit var folderAdapter: FolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFolderBinding.inflate(layoutInflater)
        folderViewModel = ViewModelProvider(this)[FolderViewModel::class.java]
        folderAdapter = FolderAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.window?.statusBarColor = Color.parseColor("#9E9D9D")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvFolder.setHasFixedSize(true)
        binding.rvFolder.setItemViewCacheSize(10)
        binding.rvFolder.layoutManager = LinearLayoutManager(requireActivity())
        folderViewModel.readAllData.observe(viewLifecycleOwner){
            if (it.isEmpty()) {
                binding.rvFolder.adapter = null
                binding.noData.visibility = View.VISIBLE
            }else {
                folderAdapter.setFolderList(it)
                binding.rvFolder.adapter = folderAdapter
                binding.noData.visibility = View.GONE
            }
        }

        binding.backBtn.setOnClickListener { findNavController().navigateUp() }
        binding.btnAdd.setOnClickListener { showBottomSheetAdd() }
        binding.btnDeleteAll.setOnClickListener {
            val dialog = AlertDialog.Builder(requireActivity())
            dialog.apply {
                setTitle("Delete All Folders")
                setMessage("Do you want to delete all folders?")
                setNegativeButton("No"){ dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                }
                setPositiveButton("Yes"){ _: DialogInterface, _: Int ->
                    folderViewModel.deleteAllFolders()
                }
            }.show()
        }
        deleteFolder()
    }

    private fun showBottomSheetAdd() {
        val dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetStyleTheme)
        val bottomSheetAddBinding = AddFolderBinding.inflate(LayoutInflater.from(requireActivity()))
        dialog.setContentView(bottomSheetAddBinding.root)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog.show()

        bottomSheetAddBinding.btnCancel.setOnClickListener { dialog.dismiss() }
        bottomSheetAddBinding.btnAdd.setOnClickListener {
            val folderName = bottomSheetAddBinding.edtFolderName.text.toString()
            if (folderName.isNotEmpty()){
                val folder = NoteFolder(0, bottomSheetAddBinding.edtFolderName.text.toString())
                folderViewModel.addFolder(folder)
                dialog.dismiss()
            }else{
                Toast.makeText(requireContext(), "Please enter folder name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteFolder(){
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

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val folder = folderAdapter.getFolderByPosition(position)
                folderViewModel.deleteFolder(folder)
                Snackbar.make(requireView(), "Folder was delete", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        folderViewModel.addFolder(folder)
                    }.show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.rvFolder)
    }
}