package com.example.crudfirebase.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Produto(val id: Int?=null, val nome: String?=null, val preco: Double?=null, @Exclude val key: String? = null): Serializable