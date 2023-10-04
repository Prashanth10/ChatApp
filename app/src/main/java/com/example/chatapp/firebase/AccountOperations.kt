package com.example.chatapp.firebase

import android.util.Log
import androidx.navigation.NavController
import com.example.chatapp.constants.Constants
import com.example.chatapp.dataclass.UserData
import com.example.chatapp.utils.PrefUtil
import com.example.chatapp.utils.Utils
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class AccountOperations {

    fun loginUser(userData: UserData, successCallback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, userData.email)
            .whereEqualTo(Constants.KEY_PASSWORD, userData.password)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null && it.result.documents.size > 0) {
                    val documentSnapshot = it.result.documents[0]
                    PrefUtil.setSignedIn(true)
                    PrefUtil.setUserId(documentSnapshot.id)
                    Log.d("flag", "signIn: userId ${documentSnapshot.id}")
                    PrefUtil.setUserName(documentSnapshot.getString(Constants.KEY_NAME)!!)
                    successCallback(true)
                } else {
                    Utils.showToast("Unable to sign in")
                }
            }
    }
}