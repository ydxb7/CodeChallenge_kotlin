package ai.tomorrow.codechallenge_kotlin.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DatabaseMessage::class], version = 1)
abstract class MessageDatabase : RoomDatabase() {
    abstract val messageDao: MessageDao
}

private lateinit var INSTANCE: MessageDatabase

fun getDatabase(context: Context): MessageDatabase {
    synchronized(MessageDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                MessageDatabase::class.java,
                "message_database"
            ).fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}