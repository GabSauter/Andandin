package com.example.walkapp.helpers

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class CredentialHelper(private val context: Context, private val credentialManager: CredentialManager) {
    suspend fun getGoogleIdToken(request: GetCredentialRequest): String {
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        return googleIdTokenCredential.idToken
    }
}