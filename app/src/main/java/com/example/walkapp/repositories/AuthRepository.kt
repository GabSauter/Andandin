package com.example.walkapp.repositories

import androidx.credentials.GetCredentialRequest
import com.example.walkapp.Secrets
import com.example.walkapp.models.LeaderboardUser
import com.example.walkapp.models.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    fun getCurrentUser() = auth.currentUser

    fun buildGoogleIdTokenRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(Secrets.default_web_client_id)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    suspend fun signInWithGoogle(googleIdToken: String): FirebaseAuth {
        try {
            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            auth.signInWithCredential(firebaseCredential).await()

            if (!userExists(auth.currentUser!!.uid)) {
                createUser(auth.currentUser!!.uid, auth.currentUser!!.displayName!!)
            }

            return auth
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun userExists(userId: String): Boolean {
        return try {
            val document = db.collection("users")
                .document(userId)
                .get()
                .await()

            document.exists()
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun createUser(userId: String, nickname: String) {
        val user =
            User(id = userId, nickname = nickname, xp = 0, walkingGoal = 150, avatarIndex = 0)
        val leaderboardUser = LeaderboardUser(
                nickname = "",
                distance = 0.0,
                avatarIndex = 0,
                monthAndYear = ""
            )

        try {
            db.runBatch { batch ->
                val userRef = db.collection("users").document(userId)
                batch.set(userRef, user.toMap())

                val leaderboardRef = db.collection("leaderboards").document(userId)
                batch.set(leaderboardRef, leaderboardUser.toMap())
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }

    fun signOut() {
        auth.signOut()
    }
}