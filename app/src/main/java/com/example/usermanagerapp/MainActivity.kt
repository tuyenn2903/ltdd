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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    LoginScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val firestoreManager = remember { FirestoreManager() }
    val context = LocalContext.current

    // Biến trạng thái
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var role by remember { mutableStateOf("User") }
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    var userList by remember { mutableStateOf(listOf<User>()) }

    // Launcher chọn ảnh - Đảm bảo gán lại imageUri để hiển thị ảnh mới
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri // Khi chọn ảnh mới, cập nhật ngay lập tức
        }
    }

    LaunchedEffect(Unit) {
        firestoreManager.getUsers { users -> userList = users }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "QUẢN LÝ NGƯỜI DÙNG", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        // Khu vực hiển thị ảnh đại diện
        IconButton(onClick = { launcher.launch("image/*") }, modifier = Modifier.size(100.dp)) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(CircleShape).border(2.dp, Color.Blue, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.Gray)
            }
        }
        Text("Nhấn để chọn ảnh", style = MaterialTheme.typography.labelSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Tên đăng nhập") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Mật khẩu") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = role == "User", onClick = { role = "User" })
            Text("User")
            Spacer(modifier = Modifier.width(20.dp))
            RadioButton(selected = role == "Admin", onClick = { role = "Admin" })
            Text("Admin")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Nút Lưu / Cập nhật
        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    val user = User(
                        id = selectedUserId ?: "",
                        username = username,
                        password = password,
                        role = role,
                        imageUrl = imageUri?.toString() ?: ""
                    )

                    if (selectedUserId == null) {
                        firestoreManager.addUser(user) { success ->
                            if (success) {
                                username = ""; password = ""; imageUri = null; role = "User"
                                Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        firestoreManager.updateUser(selectedUserId!!, user) { success ->
                            if (success) {
                                selectedUserId = null; username = ""; password = ""; imageUri = null; role = "User"
                                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (selectedUserId == null) "LƯU NGƯỜI DÙNG" else "CẬP NHẬT")
        }

        // --- KHÔI PHỤC NÚT HỦY CHỈNH SỬA ---
        if (selectedUserId != null) {
            TextButton(
                onClick = {
                    selectedUserId = null
                    username = ""
                    password = ""
                    imageUri = null
                    role = "User"
                }
            ) {
                Text("Hủy bỏ chỉnh sửa", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()

        // Danh sách
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(userList) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    onClick = {
                        selectedUserId = user.id
                        username = user.username
                        password = user.password
                        role = user.role
                        imageUri = if (user.imageUrl.isNotEmpty()) Uri.parse(user.imageUrl) else null
                    }
                ) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = user.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = rememberVectorPainter(Icons.Default.Person)
                        )
                        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(user.username, fontWeight = FontWeight.Bold)
                            Text("Quyền: ${user.role}", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = {
                            firestoreManager.deleteUser(user.id) { success ->
                                if (success) Toast.makeText(context, "Đã xóa!", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}