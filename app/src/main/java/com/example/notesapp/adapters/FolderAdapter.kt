package com.example.notesapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.databinding.ItemFolderBinding
import com.example.notesapp.databinding.NoteItemLayoutBinding
import com.example.notesapp.fragments.FolderDetailsFragment
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteFolder
import com.example.notesapp.viewModel.NoteViewModel
import jp.wasabeef.recyclerview.animators.holder.AnimateViewHolder

class FolderAdapter(): RecyclerView.Adapter<FolderAdapter.FolderViewHolder>(){
    private var listFolder:List<NoteFolder> = ArrayList()
    inner class FolderViewHolder(binding: ItemFolderBinding): RecyclerView.ViewHolder(binding.root){
        val name = binding.nameFolder
        val countNote = binding.countNotes
        val root = binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFolderList(listFolder: List<NoteFolder>){
        this.listFolder = listFolder
        notifyDataSetChanged()
    }

    fun getFolderByPosition(position: Int): NoteFolder{
        return listFolder[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        return FolderViewHolder(ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listFolder.size
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.root.apply {
            val id = listFolder[position].folderId
            val name = listFolder[position].name
            holder.name.text = name
            holder.countNote.text = id.toString()

            holder.root.setOnClickListener {
                val folder = NoteFolder(id, name)
                val bundle = Bundle()
                bundle.putSerializable("folder", folder)
                findNavController().navigate(R.id.action_folderFragment_to_folderDetailsFragment, bundle)
            }
        }
    }
}

