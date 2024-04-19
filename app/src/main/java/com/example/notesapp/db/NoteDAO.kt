package com.example.notesapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteWithFolder

@Dao
interface NoteDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAllNote(): LiveData<List<Note>>

    @Query("SELECT * FROM note_table WHERE title LIKE '%' || :query || '%' OR content " +
            "LIKE '%' || :query || '%' OR date LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchNote(query: String): LiveData<List<Note>>

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT COUNT(*) FROM note_table WHERE folderId = :folderId")
    fun getNoteCount(folderId: Int): LiveData<Int>

    @Transaction
    @Query("SELECT * FROM note_table WHERE folderId = :folderId")
    fun getNotesByFolder(folderId: Int): LiveData<List<Note>>
}