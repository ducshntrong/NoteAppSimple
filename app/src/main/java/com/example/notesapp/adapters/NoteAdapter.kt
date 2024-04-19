package com.example.notesapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.databinding.NoteItemLayoutBinding
import com.example.notesapp.fragments.NoteFragment
import com.example.notesapp.model.Note
import io.noties.markwon.Markwon

class NoteAdapter(private var folderDetails: Boolean = false): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
    private var listNote:List<Note> = ArrayList()
    inner class NoteViewHolder(binding: NoteItemLayoutBinding): RecyclerView.ViewHolder(binding.root){
        val title = binding.noteItemTitle
        val content = binding.noteContentItem
        val date = binding.noteItemDate
        val noteItem = binding.noteItemLayout
        val root = binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNoteList(listNote: List<Note>){
        this.listNote = listNote
        notifyDataSetChanged()
    }

    fun getNoteByPosition(position: Int): Note{
        return listNote[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(NoteItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listNote.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.root.apply {
            val id = listNote[position].id
            val title = listNote[position].title
            val content = listNote[position].content
            val date = listNote[position].date
            val color = listNote[position].color
            val folderId = listNote[position].folderId
            holder.title.text = title
            holder.content.text = content
            holder.date.text = date
            holder.noteItem.setBackgroundColor(color)

            holder.root.setOnClickListener {
                val note = Note(id, title, content, date, color, folderId)
                val bundle = Bundle()
                bundle.putSerializable("note", note)
                if (folderDetails){
                    findNavController().navigate(R.id.action_folderDetailsFragment_to_updateFragment, bundle)
                }else{
                    findNavController().navigate(R.id.action_noteFragment_to_updateFragment, bundle)
                }
            }
        }
    }
}

