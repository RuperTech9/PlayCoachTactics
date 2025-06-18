package com.example.playcoachtactics.data.repositories

import android.annotation.SuppressLint
import android.content.Context
import com.example.playcoachtactics.R
import com.example.playcoachtactics.data.models.PlayerInfo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TeamRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val context: Context
) {
    suspend fun getUserTeamId(userId: String): String? {
        val snapshot = firestore.collection("users").document(userId).get().await()
        return snapshot.getString("teamId")
    }

    @SuppressLint("DiscouragedApi")
    suspend fun getPlayersForTeam(teamId: String): List<PlayerInfo> {
        val snapshot = firestore.collection("teams")
            .document(teamId)
            .collection("players")
            .get().await()

        return snapshot.documents.mapNotNull { doc ->
            val number = doc.getLong("number")?.toInt() ?: return@mapNotNull null
            val firstName = doc.getString("firstname") ?: ""
            val lastName = doc.getString("lastname") ?: ""
            val nickname = doc.getString("nickname") ?: ""
            val position = doc.getString("position") ?: "Jugador"
            val imageResName = doc.getString("imageResName") ?: "ic_jugador"

            val resId = context.resources.getIdentifier(imageResName, "drawable", context.packageName)

            PlayerInfo(
                id = doc.id,
                number = number,
                firstName = firstName,
                lastName = lastName,
                nickname = nickname,
                position = position,
                imageResName = imageResName,
                imageResId = if (resId != 0) resId else R.drawable.ic_jugador
            )
        }
    }

    suspend fun updatePlayer(teamId: String, player: PlayerInfo) {
        firestore.collection("teams")
            .document(teamId)
            .collection("players")
            .document(player.id)
            .update(
                mapOf(
                    "nickname" to player.nickname,
                    "number" to player.number,
                    "position" to player.position
                )
            ).await()
    }

    suspend fun deletePlayer(teamId: String, playerId: String) {
        firestore.collection("teams")
            .document(teamId)
            .collection("players")
            .document(playerId)
            .delete()
            .await()
    }

    suspend fun addPlayer(teamId: String, player: PlayerInfo) {
        val data = mapOf(
            "firstname" to player.firstName,
            "lastname" to player.lastName,
            "nickname" to player.nickname,
            "number" to player.number,
            "position" to player.position,
            "imageResName" to player.imageResName
        )
        firestore.collection("teams")
            .document(teamId)
            .collection("players")
            .add(data)
            .await()
    }
}

