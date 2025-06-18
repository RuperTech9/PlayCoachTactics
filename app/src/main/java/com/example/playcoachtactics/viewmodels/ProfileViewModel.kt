package com.example.playcoachtactics.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playcoachtactics.data.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    var firstname by mutableStateOf("")
        private set
    var lastname by mutableStateOf("")
        private set
    var teamName by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(true)
        private set

    fun loadProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                val uid = userRepository.auth.currentUser?.uid ?: return@launch
                val userDoc = userRepository.firestore.collection("users").document(uid).get().await()
                firstname = userDoc.getString("firstname") ?: ""
                lastname = userDoc.getString("lastname") ?: ""
                val teamId = userDoc.getString("teamId") ?: ""

                val teamDoc = userRepository.firestore.collection("teams").document(teamId).get().await()
                teamName = teamDoc.getString("name") ?: teamId
            } catch (e: Exception) {
                teamName = ""
            } finally {
                isLoading = false
            }
        }
    }

    fun logout(onLogout: () -> Unit) {
        userRepository.auth.signOut()
        onLogout()
    }
}
