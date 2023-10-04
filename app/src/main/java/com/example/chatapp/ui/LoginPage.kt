package com.example.chatapp.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.App
import com.example.chatapp.R
import com.example.chatapp.constants.Constants
import com.example.chatapp.dataclass.UserData
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.utils.PrefUtil
import com.example.chatapp.utils.Utils
import com.example.chatapp.viewmodels.LoginViewModel
import com.example.chatapp.viewmodels.LoginViewModel2
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginPage(navController: NavController) {
    var userData by remember { mutableStateOf(UserData()) }
    ChatAppTheme(darkTheme = true) {
        Surface() {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        color = Color.Transparent,
                    )
            ) {

                Box(
                    modifier = Modifier
                        /*.background(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(25.dp, 5.dp, 25.dp, 5.dp)
                        )*/
                        .align(Alignment.Center),
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.user_sign_in),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),

                        )
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(modifier = Modifier.height(80.dp))

                        Text(
                            text = "Sign In",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 130.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RegisterAccountDetails(
                            data = "Email Address",
                            value = userData.email,
                            onValueChange = { userData = userData.copy(email = it) }
                        )

                        Spacer(modifier = Modifier.padding(3.dp))
                        RegisterPassword(data = "Enter Password",
                            value = userData.password,
                            onValueChange = { userData = userData.copy(password = it) })

                        Spacer(modifier = Modifier.padding(10.dp))
                        /*GradientButton(
                            gradientColors = gradientColor,
                            cornerRadius = cornerRadius,
                            nameButton = "Login",
                            roundedCornerShape = RoundedCornerShape(
                                topStart = 30.dp,
                                bottomEnd = 30.dp
                            ),
                            onSubmitClick = {
                                val email = userData.email
                                val password = userData.password
                                if (validation(email, password)) {
                                    Log.d("flag", "LoginPage: Validation passed")
                                    signIn(userData, navController)
                                }
                            }
                        )*/
                        val gradientColors = listOf(Color(0xFF484BF1), Color(0xFF673AB7))
                        val cornerRadius = 16.dp
                        val roundedCornerShape = RoundedCornerShape(
                            topStart = 30.dp,
                            bottomEnd = 30.dp
                        )
                        var isLoading by remember { mutableStateOf(false) }
                        val loginViewModel: LoginViewModel2 = hiltViewModel()
                        val loginSuccess = loginViewModel.loginSuccess.observeAsState(false)
//                        val loginSuccess by LoginViewModel().loginSuccess.observeAsState(false)
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, end = 32.dp),
                            onClick = {
                                val email = userData.email
                                val password = userData.password
                                if (validation(email, password)) {
                                    isLoading = true
                                    Log.d("flag", "LoginPage: Validation passed")
                                    loginViewModel.loginUser(userData)
                                    if(loginSuccess.value) {
                                        isLoading = false
                                        navController.navigate("chat_page") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    }
                                    /*CoroutineScope(Dispatchers.IO).launch {
                                        signIn(userData, navController)
                                        isLoading = false
                                    }*/
                                }
                            },
                            contentPadding = PaddingValues(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(cornerRadius),
                            enabled = !isLoading
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(colors = gradientColors),
                                        shape = roundedCornerShape
                                    )
                                    .clip(roundedCornerShape)
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .padding(4.dp)
                                    )
                                } else {
                                    Text(
                                        text = "Login",
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.padding(10.dp))
                        TextButton(onClick = {

                            navController.navigate("register_page") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }

                        }) {
                            Text(
                                text = "Create An Account",
                                letterSpacing = 1.sp,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun validation(
    email: String,
    password: String,
): Boolean {
    Log.d("flag", "$email $password")
    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(App.ctx, "Some of the fields are empty", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}

private fun signIn(userData: UserData, navController: NavController) {
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
                navController.navigate("chat_page") {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                Utils.showToast("Unable to sign in")
            }
        }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    val navController = rememberNavController()
    LoginPage(navController = navController)
}

