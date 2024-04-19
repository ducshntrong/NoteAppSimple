package com.example.notesapp.repository

import androidx.lifecycle.LiveData
import com.example.notesapp.db.FolderDao
import com.example.notesapp.db.NoteDAO
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteFolder

class FolderRepository(private val folderDao: FolderDao) {
    val readAllData: LiveData<List<NoteFolder>> = folderDao.getAllFolders()

    suspend fun addFolder(folder: NoteFolder){
        folderDao.insert(folder)
    }

    suspend fun updateFolder(folder: NoteFolder){
        folderDao.update(folder)
    }

    suspend fun deleteFolder(folder: NoteFolder){
        folderDao.delete(folder)
    }

    suspend fun deleteAllFolders(){
        folderDao.deleteAllFolders()
    }

}