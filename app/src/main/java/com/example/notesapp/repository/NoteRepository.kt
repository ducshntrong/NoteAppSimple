package com.example.notesapp.repository

import androidx.lifecycle.LiveData
import com.example.notesapp.db.NoteDAO
import com.example.notesapp.model.Note

class NoteRepository(private val noteDao: NoteDAO) {
    val readAllData: LiveData<List<Note>> = noteDao.getAllNote()

    suspend fun addNote(note: Note){
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note){
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note){
        noteDao.deleteNote(note)
    }

    fun searchNote(query: String): LiveData<List<Note>>{
        return noteDao.searchNote(query)
    }

    fun getNotesCount(folderId: Int): LiveData<Int>{
        return noteDao.getNoteCount(folderId)
    }

    fun getNoteByFolderId(id: Int): LiveData<List<Note>>{
        return noteDao.getNotesByFolder(id)
    }
}