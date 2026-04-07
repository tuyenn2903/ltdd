package com.example.usermanagerapp

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreManager {
    private val db = FirebaseFirestore.getInstance()

    // 1. Thêm người dùng
    fun addUser(user: User, onComplete: (Boolean) -> Unit) {
        db.collection("users")
            .add(user)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // 2. Lấy danh sách (Có lấy ID để Xóa/Sửa)
    fun getUsers(onUsersChange: (List<User>) -> Unit) {
        db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val users = snapshot?.documents?.mapNotNull { doc ->
                    val userObj = doc.toObject(User::class.java)
                    // Gán ID từ Firestore vào biến id của User để dùng cho Xóa/Sửa
                    userObj?.copy(id = doc.id)
                } ?: emptyList()

                onUsersChange(users)
            }
    }

    // 3. Sửa người dùng
    fun updateUser(userId: String, user: User, onComplete: (Boolean) -> Unit) {
        if (userId.isEmpty()) return
        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // 4. Xóa người dùng
    fun deleteUser(userId: String, onComplete: (Boolean) -> Unit) {
        if (userId.isEmpty()) return
        db.collection("users").document(userId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}