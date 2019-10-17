package ai.tomorrow.codechallenge_kotlin.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("select * from message_table ORDER BY timeStamp ASC")
    fun getAllMessages(): LiveData<List<DatabaseMessage>>

    @Query("DELETE FROM message_table")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg messages: DatabaseMessage)

    @Query("select * from message_table WHERE areFriends = 1 ORDER BY timeStamp ASC")
    fun getFriends(): LiveData<List<DatabaseMessage>>

    @Query("select * from message_table WHERE areFriends = 0 ORDER BY timeStamp ASC")
    fun getNoFriends(): LiveData<List<DatabaseMessage>>
}