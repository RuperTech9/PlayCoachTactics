package com.example.playcoachtactics.data.repositories

import com.example.playcoachtactics.data.models.RelativeOffset
import com.example.playcoachtactics.data.models.SavedFormation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FormationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val formationsRef = firestore.collection("formations")

    suspend fun saveFormation(data: Map<String, Any>) {
        formationsRef.add(data).await()
    }

    suspend fun loadFormations(userId: String, teamId: String): List<Triple<String, List<Pair<Int, RelativeOffset>>, String>> {
        val snapshot = formationsRef
            .whereEqualTo("userId", userId)
            .whereEqualTo("teamId", teamId)
            .get().await()

        return snapshot.documents.mapNotNull { doc ->
            val name = doc.getString("name") ?: return@mapNotNull null
            val positions = doc.get("positions") as? List<Map<String, Any>> ?: return@mapNotNull null
            val posList = positions.mapNotNull { p ->
                val number = (p["number"] as? Long)?.toInt() ?: return@mapNotNull null
                val x = (p["xPercent"] as? Double)?.toFloat() ?: return@mapNotNull null
                val y = (p["yPercent"] as? Double)?.toFloat() ?: return@mapNotNull null
                number to RelativeOffset(x, y)
            }
            Triple(name, posList, doc.id)
        }
    }

    suspend fun deleteFormation(documentId: String) {
        formationsRef.document(documentId).delete().await()
    }

    suspend fun saveFormationAsModel(formation: SavedFormation, userId: String, teamId: String) {
        val data = hashMapOf(
            "name" to formation.name,
            "userId" to userId,
            "teamId" to teamId,
            "positions" to formation.positions.map { pos ->
                hashMapOf(
                    "number" to pos.number,
                    "xPercent" to pos.offset.xPercent,
                    "yPercent" to pos.offset.yPercent
                )
            }
        )
        formationsRef.add(data).await()
    }

}