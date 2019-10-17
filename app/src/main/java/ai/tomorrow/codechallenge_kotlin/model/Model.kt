package ai.tomorrow.codechallenge_kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
    val id: String,
    val name: String
)

@Entity(tableName = "message_table")
data class DatabaseMessage(
    val toId: String,
    val toName: String,
    val fromId: String,
    val fromName: String,
    @PrimaryKey
    val timestamp: String,
    val areFriends: Boolean
)