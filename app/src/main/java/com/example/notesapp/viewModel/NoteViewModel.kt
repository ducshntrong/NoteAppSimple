package com.example.notesapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notesapp.db.NoteDatabase
import com.example.notesapp.model.Note
import com.example.notesapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteViewModel(application: Application): AndroidViewModel(application) {
    val readAllData: LiveData<List<Note>>
    private val repository: NoteRepository

    init {
        val noteDao = NoteDatabase.getInstance(application).getNoteDao()
        repository = NoteRepository(noteDao)
        readAllData = repository.readAllData
    }

    fun addNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.addNote(note)
        }
    }

    fun updateNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteNote(note)
        }
    }

    fun searchNote(query: String): LiveData<List<Note>> {
        return repository.searchNote(query)
    }

    fun getNoteByFolderId(id: Int): LiveData<List<Note>> {
        return repository.getNoteByFolderId(id)
    }

    fun getNotesCount(folderId: Int): LiveData<Int>{
        return repository.getNotesCount(folderId)
    }
}