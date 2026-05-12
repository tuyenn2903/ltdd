package com.example.usermanagerapp

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val auth = remember { FirebaseAuth.getInstance() }
    // Kiểm tra xem đã có user đăng nhập chưa
    var currentUser by remember { mutableStateOf(auth.currentUser) }

    if (currentUser == null) {
        // Nếu chưa đăng nhập -> Hiện màn hình Login
        LoginAuthScreen(onLoginSuccess = { currentUser = auth.currentUser })
    } else {
        // Nếu đã đăng nhập -> Hiện màn hình Quản lý người dùng
        UserManagerScreen(onLogout = {
            auth.signOut()
            currentUser = null
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAuthScreen(onLoginSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ĐĂNG NHẬP HỆ THỐNG", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email (ví dụ: admin@gmail.com)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email.trim(), password)                        .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onLoginSuccess()
                        } else {
                            Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_LONG).show()                            }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("VÀO HỆ THỐNG")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagerScreen(onLogout: () -> Unit) {
    val firestoreManager = remember { FirestoreManager() }
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var role by remember { mutableStateOf("User") }
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    var userList by remember { mutableStateOf(listOf<User>()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> if (uri != null) imageUri = uri }

    LaunchedEffect(Unit) {
        firestoreManager.getUsers { users -> userList = users }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header có nút Đăng xuất
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("QUẢN LÝ NGƯỜI DÙNG", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = onLogout) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phần Form nhập liệu (Giữ nguyên logic của bạn)
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { launcher.launch("image/*") }, modifier = Modifier.size(100.dp)) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri, contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape).border(2.dp, Color.Blue, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.Gray)
                }
            }
            Text("Nhấn để chọn ảnh", style = MaterialTheme.typography.labelSmall)

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Tên") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Mật khẩu") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = role == "User", onClick = { role = "User" })
                Text("User")
                Spacer(modifier = Modifier.width(20.dp))
                RadioButton(selected = role == "Admin", onClick = { role = "Admin" })
                Text("Admin")
            }

            Button(
                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        val user = User(id = selectedUserId ?: "", username = username, password = password, role = role, imageUrl = imageUri?.toString() ?: "")
                        if (selectedUserId == null) {
                            firestoreManager.addUser(user) { if (it) { username = ""; password = ""; imageUri = null; role = "User"; Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show() } }
                        } else {
                            firestoreManager.updateUser(selectedUserId!!, user) { if (it) { selectedUserId = null; username = ""; password = ""; imageUri = null; role = "User"; Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show() } }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedUserId == null) "LƯU NGƯỜI DÙNG" else "CẬP NHẬT")
            }

            if (selectedUserId != null) {
                TextButton(onClick = { selectedUserId = null; username = ""; password = ""; imageUri = null; role = "User" }) {
                    Text("Hủy bỏ chỉnh sửa", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()

        // Danh sách hiển thị
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(userList) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    onClick = {
                        selectedUserId = user.id; username = user.username; password = user.password; role = user.role
                        imageUri = if (user.imageUrl.isNotEmpty()) Uri.parse(user.imageUrl) else null
                    }
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = user.imageUrl, contentDescription = null,
                            modifier = Modifier.size(50.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = rememberVectorPainter(Icons.Default.Person)
                        )
                        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(user.username, fontWeight = FontWeight.Bold)
                            Text("Quyền: ${user.role}", color = if(user.role == "Admin") Color.Red else Color.Blue, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = {
                            firestoreManager.deleteUser(user.id) { if (it) Toast.makeText(context, "Đã xóa!", Toast.LENGTH_SHORT).show() }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}