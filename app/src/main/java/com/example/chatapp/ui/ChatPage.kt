package com.example.chatapp.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chatapp.App
import com.example.chatapp.R
import com.example.chatapp.constants.Constants
import com.example.chatapp.dataclass.MsgItem
import com.example.chatapp.dataclass.MsgUI
import com.example.chatapp.prefs.PrefManager
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.utils.PrefUtil
import com.example.chatapp.utils.Utils
import com.example.chatapp.viewmodels.ChatViewModel
import com.example.chatapp.viewmodels.ThemeViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat

var firstLoad: Boolean = true

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun ChatPage(navController: NavController) {
    val chatViewModel: ChatViewModel = viewModel()
    val messages by chatViewModel.messages.observeAsState()
    val themeViewModel: ThemeViewModel = viewModel()
    val darkThemeEnabled by themeViewModel.darkThemeEnabled.observeAsState()
    Log.d("flag", "ChatPage: reload")

    if (PrefUtil.getUserId() != "" && firstLoad) {
        firstLoad = false
        getToken()
    }

    ChatAppTheme(darkTheme = darkThemeEnabled!!) {
        Surface() {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                HeaderWithProfile(themeViewModel, chatViewModel, navController)
                MessageList(
                    chatViewModel,
                    messages!!,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                MessageInput(chatViewModel)
            }
        }
    }
}

@Composable
fun HeaderWithProfile(
    themeViewModel: ThemeViewModel,
    chatViewModel: ChatViewModel,
    navController: NavController
) {
    var isMenuVisible by remember { mutableStateOf(false) }
    val name by remember { mutableStateOf("Bot") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            // Profile Picture
            Image(
                painter = painterResource(id = R.drawable.profile_pic),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))

            // User Name
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }


        Row(
            horizontalArrangement = Arrangement.End
        ) {
            val messageList by chatViewModel.messages.observeAsState()
            val selections = messageList?.count { it.isSelected }
            if (selections == 0) {
                IconButton(
                    onClick = {
                        isMenuVisible = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                DropdownMenu(
                    expanded = isMenuVisible,
                    onDismissRequest = { isMenuVisible = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val themeText =
                        if (themeViewModel.darkThemeEnabled.value!!) "Light" else "Dark"

                    DropdownMenuItem(text = {
                        Text(
                            text = "$themeText Theme",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }, onClick = {
                        isMenuVisible = false
                        themeViewModel.toggleDarkTheme()
                    })

                    DropdownMenuItem(text = {
                        Text(
                            text = "Logout",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }, onClick = {
                        Log.d("flag", "HeaderWithProfile: logout button click")
                        signOut(navController)
                        isMenuVisible = false
                    })
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.icons_delete),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(top = 3.dp, end = 2.dp)
                        .clickable { chatViewModel.delete() }
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageList(
    chatViewModel: ChatViewModel,
    msgList: List<MsgItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
    ) {
        items(msgList.reversed()) { message ->
            MessageCard(chatViewModel, message)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageCard(chatViewModel: ChatViewModel, messageItem: MsgItem) {
    val bgColor =
        if (messageItem.isSelected) MaterialTheme.colorScheme.inverseOnSurface else Color.Unspecified
    val msgUI = if (messageItem.isMine) {
        MsgUI(
            arrangement = Arrangement.End,
            alignment = Alignment.End,
            cardColor = MaterialTheme.colorScheme.primary,
            userNameColor = MaterialTheme.colorScheme.surfaceTint,
            msgColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        MsgUI(
            arrangement = Arrangement.Start,
            alignment = Alignment.Start,
            cardColor = MaterialTheme.colorScheme.secondaryContainer,
            userNameColor = MaterialTheme.colorScheme.inverseSurface,
            msgColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor),
        horizontalArrangement = msgUI.arrangement
    ) {

        var isDialogOpen by remember { mutableStateOf(false) }
        if (isDialogOpen) {
            var newText by remember { mutableStateOf(messageItem.content) }
            AlertDialog(
                onDismissRequest = {
                    isDialogOpen = false
                },
                title = {
                    Text(text = "Edit Message")
                },
                text = {
                    TextField(
                        value = newText,
                        onValueChange = {
                            newText = it
                        }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            Log.d("flag", "MessageCard: Edit output $newText")
                            messageItem.content = newText
                            isDialogOpen = false
                        }
                    ) {
                        Text(text = "Save")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isDialogOpen = false
                        }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }

        if (messageItem.isMine) {
            Image(
                painter = painterResource(id = R.drawable.icons_edit),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .padding(bottom = 2.dp, end = 2.dp)
                    .clickable { isDialogOpen = true }
                    .align(Alignment.CenterVertically)
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = msgUI.alignment,
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 340.dp)
                    .combinedClickable(
                        enabled = true,
                        onClickLabel = "Long press to select",
                        role = null,
                        onLongClickLabel = "message selected",
                        onLongClick = {
                            chatViewModel.selection(messageItem)
                            Log.d("flag", "MessageCard: Long pressed")
                        },
                        onDoubleClick = { },
                        onClick = {
                            if (messageItem.isSelected) {
                                Log.d("flag", "MessageCard: Clicked")
                                chatViewModel.selection(messageItem)
                            }
                        }
                    ),
                shape = cardShapeFor(messageItem),
                colors = CardDefaults.cardColors(msgUI.cardColor),
            ) {
                Text(
                    text = messageItem.userName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = msgUI.userNameColor,
                    modifier = Modifier
                        .align(msgUI.alignment)
                        .padding(1.dp)
                )
                Text(
                    modifier = when {
                        messageItem.isMine -> Modifier.padding(7.dp, 3.dp, 20.dp, 5.dp)
                        else -> Modifier.padding(20.dp, 3.dp, 7.dp, 5.dp)
                    },
                    text = messageItem.content,
                    color = msgUI.msgColor,
                )
            }
            val formatter = SimpleDateFormat("HH:mm")
            val formatted = formatter.format(messageItem.timeStamp)
            Text(text = formatted, fontSize = 10.sp, fontFamily = FontFamily.Serif)
        }
    }
}


@Composable
fun cardShapeFor(message: MsgItem): Shape {
    val roundedCorners = RoundedCornerShape(16.dp)
    return when {
        message.isMine -> roundedCorners.copy(topEnd = CornerSize(0))
        else -> roundedCorners.copy(topStart = CornerSize(0))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(chatViewModel: ChatViewModel) {
    var inputValue by remember { mutableStateOf("") }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage() {
        chatViewModel.addMessage(MsgItem(inputValue, true))
        chatViewModel.addMessage(MsgItem("received", false, "Bot"))
        inputValue = ""
    }
    Row(modifier = Modifier.padding(3.dp)) {
        TextField(
            modifier = Modifier.weight(1f),
            value = inputValue,
            onValueChange = { inputValue = it },
            placeholder = { Text(text = "Type your message", color = Color.Gray) },
            keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Send),
            keyboardActions = KeyboardActions { sendMessage() },
        )
        Button(
            modifier = Modifier
                .height(56.dp)
                .width(70.dp)
                .padding(2.dp, 0.dp, 0.dp, 0.dp),
            onClick = { sendMessage() },
            enabled = inputValue.isNotBlank(),
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                modifier = Modifier.size(70.dp)
            )
        }
    }
}

private fun getToken() = FirebaseMessaging.getInstance().token.addOnSuccessListener(::updateToken)

private fun updateToken(token: String) {
    val db = FirebaseFirestore.getInstance()
    Log.d("flag", "updateToken: ${PrefUtil.getUserId()}")
    val documentReference =
        db.collection(Constants.KEY_COLLECTION_USERS).document(PrefUtil.getUserId())
    documentReference.update(Constants.KEY_FCM_TOKEN, token)
        .addOnSuccessListener { Log.d("flag", "updateToken: Success") }
        .addOnFailureListener { Log.d("flag", "updateToken: Failure") }
}

private fun signOut(navController: NavController) {
    Utils.showToast("Signing out")
    Log.d("flag", "signOut: signing out")
    val db = FirebaseFirestore.getInstance()
    val documentReference =
        db.collection(Constants.KEY_COLLECTION_USERS).document(PrefUtil.getUserId())
    val data = HashMap<String, Any>()
    data[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
    Log.d("flag", "signOut: token delete")
    documentReference.update(data)
        .addOnSuccessListener {
            PrefManager.clearPreferences()
            Log.d("flag", "signOut: clear preference")
            navController.navigate("login_page") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
        .addOnFailureListener { Utils.showToast("Unable to sign out") }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val chatViewModel: ChatViewModel = viewModel()
    val messages by chatViewModel.messages.observeAsState()
    val themeViewModel: ThemeViewModel = viewModel()
    ChatAppTheme {
        Column(Modifier.fillMaxSize()) {
            HeaderWithProfile(themeViewModel, chatViewModel, NavController(App.ctx))
            MessageList(
                chatViewModel,
                messages!!,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            MessageInput(chatViewModel)
        }
    }
}