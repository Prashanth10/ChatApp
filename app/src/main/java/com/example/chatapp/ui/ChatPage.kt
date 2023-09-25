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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
        subscribeTopic()
        getMessages(chatViewModel)
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
                    messages,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                MessageInput()
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
    val name by remember { mutableStateOf("ChatGroup") }

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
                painter = painterResource(id = R.drawable.group_dp),
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
                    val groupNotifications =
                        if (PrefUtil.getGroupNotifications()) "Mute" else "UnMute"
                    Log.d(
                        "flag",
                        "HeaderWithProfile: Mute Notifications ${PrefUtil.getGroupNotifications()} $groupNotifications"
                    )

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
                            text = "$groupNotifications Group",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }, onClick = {
                        isMenuVisible = false
                        if (PrefUtil.getGroupNotifications())
                            unSubscribeTopic()
                        else
                            subscribeTopic()
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
                        .clickable {
                            val selectedList = messageList?.filter { it.isSelected }
                            delete(selectedList)
//                            chatViewModel.delete()
                        }
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
    msgList: List<MsgItem>?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
    ) {
        if (msgList != null) {
            items(msgList.reversed()) { message ->
                MessageCard(chatViewModel, message)
            }
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
//                            messageItem.content = newText
                            editMsg(messageItem, newText)
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
                            if (messageItem.isMine) {
                                chatViewModel.selection(messageItem)
                                Log.d("flag", "MessageCard: Long pressed")
                            }
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
            val formatter =
                SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(messageItem.timeStamp)
            Text(text = formatter, fontSize = 10.sp, fontFamily = FontFamily.Serif)
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
fun MessageInput() {
    var inputValue by remember { mutableStateOf("") }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage() {
//        chatViewModel.addMessage(MsgItem(inputValue, true))
//        chatViewModel.addMessage(MsgItem("received", false, "Bot"))
        addMsgToDB(inputValue)
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
        .addOnSuccessListener {
            Log.d("flag", "updateToken: Success")
            PrefUtil.setToken(token)
        }
        .addOnFailureListener { Log.d("flag", "updateToken: Failure") }
}

private fun subscribeTopic() {
    FirebaseMessaging.getInstance().subscribeToTopic(Constants.KEY_TOPIC)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("flag", "subscribeTopic: subscribed")
                PrefUtil.setGroupNotifications(true)
            } else
                Log.d("flag", "subscribeTopic: failed")
        }
}

private fun unSubscribeTopic() {
    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.KEY_TOPIC)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("flag", "unSubscribeTopic: unSubscribed")
                PrefUtil.setGroupNotifications(false)
            } else
                Log.d("flag", "unSubscribeTopic: failed")
        }
}

private fun getMessages(chatViewModel: ChatViewModel) {
    val db = FirebaseFirestore.getInstance()
    db.collection(Constants.KEY_COLLECTION_CHAT)
        .orderBy(Constants.KEY_TIMESTAMP, Query.Direction.ASCENDING)
        .addSnapshotListener(eventListener(chatViewModel))
}

private fun eventListener(chatViewModel: ChatViewModel) =
    com.google.firebase.firestore.EventListener<QuerySnapshot> { value, error ->
        if (error != null)
            return@EventListener
        else if (value != null) {
            Log.d("flag", "eventListener: Got msg")
            for (docChange in value.documentChanges) {
                if (docChange.type == DocumentChange.Type.ADDED) {
                    val msg = docChange.document[Constants.KEY_MESSAGE].toString()
                    Log.d(
                        "flag", "eventListener: ${PrefUtil.getUserId()}" +
                                " ${docChange.document[Constants.KEY_SENDER_ID].toString()} ${docChange.document.id}"
                    )
                    val isMine =
                        PrefUtil.getUserId() == docChange.document[Constants.KEY_SENDER_ID].toString()
                    val name =
                        if (isMine) "Me" else docChange.document[Constants.KEY_NAME].toString()
                    val time = docChange.document.getDate(Constants.KEY_TIMESTAMP)
                    chatViewModel.addMessage(
                        MsgItem(
                            docChange.document.id,
                            msg,
                            isMine,
                            name,
                            time!!
                        )
                    )
                } else if (docChange.type == DocumentChange.Type.REMOVED) {
                    Log.d("flag", "eventListener: doc removed ${docChange.document.id}")
                    chatViewModel.delete(docChange.document.id)
                } else if (docChange.type == DocumentChange.Type.MODIFIED) {
                    val msg = docChange.document[Constants.KEY_MESSAGE].toString()
                    val time = docChange.document.getDate(Constants.KEY_TIMESTAMP)
                    Log.d("flag", "eventListener: Edit ${docChange.document.id}, $msg, $time")
                    chatViewModel.update(docChange.document.id, msg, time!!)
                }
            }
        }
    }

private fun addMsgToDB(inputValue: String) {
    val db = FirebaseFirestore.getInstance()
    val message = HashMap<String, Any>()
    message[Constants.KEY_SENDER_ID] = PrefUtil.getUserId()
    message[Constants.KEY_NAME] = PrefUtil.getUserName()
    message[Constants.KEY_MESSAGE] = inputValue
    message[Constants.KEY_TIMESTAMP] = Calendar.getInstance().time
    db.collection(Constants.KEY_COLLECTION_CHAT).add(message)
//    sendTopic(inputValue)
    sendNotification(inputValue)
}

/*private fun sendTopic(msg: String) {
    val topic = Constants.KEY_TOPIC
    val message = RemoteMessage.Builder(topic)
        .setData(mapOf("userName" to PrefUtil.getUserName(), "msgContent" to msg))
        .build()
    FirebaseMessaging.getInstance().send(message)
    Log.d("flag", "sendTopic: ${message.data["msgContent"]}")
}*/

private fun sendNotification(msg: String) {
    val serverKey =
        "AAAAdILLGgg:APA91bGoM5fR3stMNlT8utYseQCFJmwAMMx7Ahi11mM6DokRIfcT5JjgUcip2vWDAWtLCAJ-_nliDpGsXCoG5OdcJtotHUXQfFY1MdXxU80jz2T5zANLh9AAv5XYQDGE7eaXO8T-RJvU"
    val client = OkHttpClient()
    val url = "https://fcm.googleapis.com/fcm/send"
    val jsonBody = """
        {
            "to": "/topics/messageTopic",
            "data": {
                "msgContent": "$msg",
                "userName": "${PrefUtil.getUserName()}"
            }
        }
    """.trimIndent()

    val mediaType = "application/json".toMediaTypeOrNull()
    val requestBody = jsonBody.toRequestBody(mediaType)

    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "key=$serverKey")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("flag", "onFailure: Notification post failed")
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            Log.d("flag", "Notification Post $ onResponse: $responseBody")
        }
    })
}

private fun delete(selectedList: List<MsgItem>?) {
    val db = FirebaseFirestore.getInstance()
    selectedList?.forEach {
        Log.d("flag", "delete: ${it.chatId}")
        db.collection(Constants.KEY_COLLECTION_CHAT)
            .document(it.chatId)
            .delete()
    }
}

private fun editMsg(msgItem: MsgItem, newMsg: String) {
    val db = FirebaseFirestore.getInstance()
    val documentReference = db.collection(Constants.KEY_COLLECTION_CHAT)
        .document(msgItem.chatId)
    val map = hashMapOf<String, Any>(
        Constants.KEY_MESSAGE to newMsg,
        Constants.KEY_TIMESTAMP to Calendar.getInstance().time
    )
    documentReference.update(map)
        .addOnSuccessListener {
            Log.d("flag", "editMsg: Update success")
        }.addOnFailureListener {
            Log.d("flag", "editMsg: Update failed $it")
        }
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
            firstLoad = true
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
            MessageInput()
        }
    }
}