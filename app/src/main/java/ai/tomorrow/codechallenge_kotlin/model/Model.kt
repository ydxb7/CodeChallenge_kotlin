package ai.tomorrow.codechallenge_kotlin.model

import androidx.room.Entity

data class To(
    val id: String,
    val name: String
)

data class From(
    val id: String,
    val name: String
)

data class NetworkMessage(
    val to: To,
    val from: From,
    val timestamp: Long,
    val areFriends: Boolean
)


@Entity(tableName = "message_table")
data class DatabaseMessage(
    val toId: String,
    val toName: String,
    val fromId: String,
    val fromName: String,
    val timestamp: Long,
    val areFriends: Boolean
)

fun List<NetworkMessage>.asDatabaseModel(myAddress: String): Array<DatabaseMessage> {
    return map {
        DatabaseMessage(
            toId = it.to.id,
            toName = it.to.name,
            fromId = it.from.id,
            fromName = it.from.name,
            timestamp = it.timestamp,
            areFriends = it.areFriends
        )
    }.toTypedArray()
}

