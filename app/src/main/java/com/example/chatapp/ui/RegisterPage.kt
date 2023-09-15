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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.App
import com.example.chatapp.R
import com.example.chatapp.constants.Constants
import com.example.chatapp.dataclass.RegisterFormData
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.utils.Utils
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterPage(navController: NavController) {
    var formData by remember { mutableStateOf(RegisterFormData()) }
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
                        .align(Alignment.Center),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.user_reg),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(180.dp)
                                .fillMaxWidth(),
                        )
                        Text(
                            text = "Create An Account",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RegisterAccountDetails(
                            data = "Name",
                            value = formData.name,
                            onValueChange = { formData = formData.copy(name = it) }
                        )
//                        RegisterName()

                        Spacer(modifier = Modifier.padding(3.dp))
                        RegisterAccountDetails(
                            data = "Email Address",
                            value = formData.email,
                            onValueChange = { formData = formData.copy(email = it) }
                        )
//                        RegisterEmail()

                        Spacer(modifier = Modifier.padding(3.dp))
                        RegisterPassword("Enter Password",
                            value = formData.password,
                            onValueChange = { formData = formData.copy(password = it) }
                        )

                        Spacer(modifier = Modifier.padding(3.dp))
                        RegisterPassword("Confirm Password",
                            value = formData.confirmPassword,
                            onValueChange = { formData = formData.copy(confirmPassword = it) }
                        )

                        val gradientColor = listOf(Color(0xFF484BF1), Color(0xFF673AB7))
                        val cornerRadius = 16.dp

                        Spacer(modifier = Modifier.padding(10.dp))
                        /* Button(
                     onClick = {},
                     modifier = Modifier
                         .fillMaxWidth(0.8f)
                         .height(50.dp)
                 ) {
                     Text(text = "Login", fontSize = 20.sp)
                 }*/
                        GradientButton(
                            gradientColors = gradientColor,
                            cornerRadius = cornerRadius,
                            nameButton = "Create An Account",
                            roundedCornerShape = RoundedCornerShape(
                                topStart = 30.dp,
                                bottomEnd = 30.dp
                            ),
                            onSubmitClick = {
                                val name = formData.name
                                val email = formData.email
                                val password = formData.password
                                val confirmPassword = formData.confirmPassword
                                if (validation(name, email, password, confirmPassword)) {
                                    Log.d("flag", "RegisterPage: Validation passed")
                                    signUp(name, email, password, navController)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        TextButton(onClick = {
                            navController.navigate("login_page") {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }

                        }) {
                            Text(
                                text = "Sign In",
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

@Composable
private fun GradientButton(
    gradientColors: List<Color>,
    cornerRadius: Dp,
    nameButton: String,
    roundedCornerShape: RoundedCornerShape,
    onSubmitClick: () -> Unit
) {

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp, end = 32.dp),
        onClick = onSubmitClick,

        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors = gradientColors),
                    shape = roundedCornerShape
                )
                .clip(roundedCornerShape)
                /*.background(
                    brush = Brush.linearGradient(colors = gradientColors),
                    shape = RoundedCornerShape(cornerRadius)
                )*/
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nameButton,
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterAccountDetails(data: String, value: String, onValueChange: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
        label = {
            Text(
                data,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        placeholder = { Text(text = data) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = if (data == "Name") KeyboardType.Text else KeyboardType.Email
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(0.8f),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                // do something here
            }
        )

    )
}

//password
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterPassword(data: String, value: String, onValueChange: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordHidden by rememberSaveable { mutableStateOf(true) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(topEnd = 12.dp, bottomStart = 12.dp),
        label = {
            Text(
                data,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        visualTransformation =
        if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        //  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Password
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val visibilityIcon =
                    if (passwordHidden) painterResource(R.drawable.visible) else painterResource(R.drawable.visibility)
                // Please provide localized description for accessibility services
                val description = if (passwordHidden) "Show password" else "Hide password"
                Icon(painter = visibilityIcon, contentDescription = description)
            }
        },
        modifier = Modifier.fillMaxWidth(0.8f),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                // do something here
            }
        )
    )
}

private fun validation(
    name: String,
    email: String,
    password: String,
    confirmPassword: String
): Boolean {
    Log.d("flag", "validation: $name $email $password $confirmPassword ")
    if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        Toast.makeText(App.ctx, "Some of the fields are empty", Toast.LENGTH_SHORT).show()
        return false
    } else if (password != confirmPassword) {
        Toast.makeText(App.ctx, "Passwords doesn't match", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}

private fun signUp(name: String, email: String, password: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val user = HashMap<String, kotlin.Any>()
    user[Constants.KEY_NAME] = name
    user[Constants.KEY_EMAIL] = email
    user[Constants.KEY_PASSWORD] = password
    db.collection(Constants.KEY_COLLECTION_USERS)
        .add(user)
        .addOnSuccessListener {
            Utils.showToast("Successfully registered")
            navController.navigate("login_page") {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
        .addOnFailureListener {
            Utils.showToast(it.message!!)
        }

}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    val navController = rememberNavController()
    RegisterPage(navController = navController)
}