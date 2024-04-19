package com.example.notesapp.model

import android.text.Editable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.io.Serializable

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val color: Int = -1,
    @ColumnInfo(name = "folderId")
    val folderId: Int = -1
): Serializable

@Entity(tableName = "note_folders")
data class NoteFolder(
    @PrimaryKey(autoGenerate = true)
    val folderId: Int = 0,
    val name: String
): Serializable

data class NoteWithFolder(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "folderId",
        entityColumn = "id"
    )
    val folder: NoteFolder
)
