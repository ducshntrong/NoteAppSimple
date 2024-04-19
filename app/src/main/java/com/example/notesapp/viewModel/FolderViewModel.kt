package com.example.notesapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notesapp.db.NoteDatabase
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteFolder
import com.example.notesapp.repository.FolderRepository
import com.example.notesapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderViewModel(application: Application): AndroidViewModel(application) {
    val readAllData: LiveData<List<NoteFolder>>
    private val repository: FolderRepository

    init {
        val folderDao = NoteDatabase.getInstance(application).getFolderDao()
        repository = FolderRepository(folderDao)
        readAllData = repository.readAllData
    }

    fun addFolder(folder: NoteFolder){
        viewModelScope.launch(Dispatchers.IO){
            repository.addFolder(folder)
        }
    }

    fun updateFolder(folder: NoteFolder){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateFolder(folder)
        }
    }

    fun deleteFolder(folder: NoteFolder){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteFolder(folder)
        }
    }

    fun deleteAllFolders(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAllFolders()
        }
    }
}