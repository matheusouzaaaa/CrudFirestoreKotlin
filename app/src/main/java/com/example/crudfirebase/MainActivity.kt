package com.example.crudfirebase

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crudfirebase.adapter.ProdutoAdapter
import com.example.crudfirebase.model.Produto
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), ProdutoAdapter.OnItemClickListener{

    private val REQ_CADASTRO = 1;
    private val REQ_DETALHE = 2;
    private var listaProdutos: ArrayList<Produto> = ArrayList()
    private var posicaoAlterar = -1

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ProdutoAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ProdutoAdapter(listaProdutos)
        viewAdapter.onItemClickListener = this
        Log.e("LISTA", listaProdutos.toString())

        // listar do firebase
        db.collection("produtos").addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore Error", error.message.toString())
                }

                for (dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){

                        var produto = Produto(dc.document.toObject(Produto::class.java).id, dc.document.toObject(Produto::class.java).nome, dc.document.toObject(Produto::class.java).preco, dc.document.id)
                        listaProdutos.add(produto)
                    }
                }

                Log.e("LISTA3", listaProdutos.toString())

                viewAdapter.notifyDataSetChanged()
            }

        })

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        Log.e("LISTA2", listaProdutos.toString())

    }

    override fun onItemClicked(view: View, position: Int) {
        val it = Intent(this, DetalheActivity::class.java)
        this.posicaoAlterar = position
        val produto = listaProdutos.get(position)
        it.putExtra("produto", produto)
        startActivityForResult(it, REQ_DETALHE)
    }

    fun abrirFormulario(view: View) {
        val it = Intent(this, CadastroActivity::class.java)
        startActivityForResult(it, REQ_CADASTRO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CADASTRO) {
            if (resultCode == Activity.RESULT_OK) {
                val produto = data?.getSerializableExtra("produto") as Produto

                // Add a new document with a generated ID
                db.collection("produtos")
                .add(produto)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot added")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

                viewAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Cadastro realizada com sucesso!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (requestCode == REQ_DETALHE) {
            if (resultCode == DetalheActivity.RESULT_EDIT) {
                val produto = data?.getSerializableExtra("produto") as Produto
                listaProdutos.set(this.posicaoAlterar, produto)

                // atualizar no banco firestore

                db.collection("produtos").document(produto.key.toString())
                    .update("id",produto.id?.toInt(), "nome", produto.nome.toString(), "preco" ,produto.preco?.toFloat()
                    )
                    .addOnSuccessListener { document ->
                        Toast.makeText(this, "Atualizado com sucesso", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "DocumentSnapshot added")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show()
                        Log.w("Firebase", "Error adidin documen", e)
                    }
                viewAdapter.notifyDataSetChanged()
            } else if (resultCode == DetalheActivity.RESULT_DELETE) {
                val produto = data?.getSerializableExtra("produto") as Produto
                listaProdutos.removeAt(this.posicaoAlterar)

                db.collection("produtos").document(produto.key.toString())
                    .delete()
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(this, "Deletgiado com sucesso", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Registro deletado com sucesso!")
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show()
                        Log.w("Firebase", "Error deleting document", e)
                    }

                viewAdapter.notifyDataSetChanged()
            }
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val db = Firebase.firestore
//
//        // Create a new user with a first and last name
//        val user = hashMapOf(
//            "nome" to "Produto 2",
//            "preco" to 20
//        )
//
//        db.collection("produtos")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents.", exception)
//            }
//
//        // Add a new document with a generated ID
//        /*db.collection("produtos")
//        .add(user)
//        .addOnSuccessListener { documentReference ->
//            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//        }
//        .addOnFailureListener { e ->
//            Log.w(TAG, "Error adding document", e)
//        }*/
//    }

}