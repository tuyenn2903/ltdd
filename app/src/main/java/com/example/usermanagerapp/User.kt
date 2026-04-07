package com.example.usermanagerapp

/**
 * Lớp dữ liệu User phải có đầy đủ các trường để MainActivity có thể gọi được
 */
data class User(
    val id: String = "",         // Dùng cho Xóa/Sửa
    val username: String = "",
    val password: String = "",
    val role: String = "User",   // Thuộc tính đang bị thiếu dẫn đến lỗi của bạn
    val imageUrl: String = ""    // Dùng cho hiển thị ảnh
)