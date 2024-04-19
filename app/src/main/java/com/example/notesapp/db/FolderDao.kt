package com.example.notesapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.notesapp.model.NoteFolder

@Dao
interface FolderDao {
    @Insert
    suspend fun insert(folder: NoteFolder)

    @Update
    suspend fun update(folder: NoteFolder)

    @Delete
    suspend fun delete(folder: NoteFolder)

    @Query("SELECT * FROM note_folders ORDER BY folderId DESC")
    fun getAllFolders(): LiveData<List<NoteFolder>>

    @Query("DELETE FROM note_folders")
    suspend fun deleteAllFolders()
}