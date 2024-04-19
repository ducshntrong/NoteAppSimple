package com.example.notesapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notesapp.model.Note
import com.example.notesapp.model.NoteFolder
import com.example.notesapp.repository.NoteRepository

@Database(entities = [Note::class, NoteFolder::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    //Cơ sở dữ liệu cần biết về DAO. khai báo một giá trị trừu tượng trả
    // về phần mở rộng NoteDAO
    abstract fun getNoteDao(): NoteDAO
    abstract fun getFolderDao(): FolderDao

    companion object{
        //Với thuộc tính được đánh dấu @Volatile trong Kotlin, các thay đổi của thuộc tính đó
        // sẽ được cập nhật đồng bộ giữa các luồng và đảm bảo tính nhất quán của dữ liệu.
        //Biến INSTANCE sẽ giữ một tham chiếu đến cơ sở dữ liệu, khi một biến đã được tạo. Điều này giúp
        //tránh phải mở liên tục các kết nối tới cơ sở dữ liệu, điều này rất tốn kém về mặt tính toán.
        @Volatile
        private var INSTANCE:NoteDatabase? = null
        fun getInstance(context: Context): NoteDatabase{
            val tempInstance = INSTANCE
            //kiểm tra xem tempInstance có null hay không tức là chưa có cơ sở dữ liệu.
            if (tempInstance!= null){
                return tempInstance
            }
            synchronized(this){
                //gọi Room.databaseBuilder và cung cấp ngữ cảnh mà bạn đã chuyển vào,
                //lớp cơ sở dữ liệu và tên cho cơ sở dữ liệu, userDatabase
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}