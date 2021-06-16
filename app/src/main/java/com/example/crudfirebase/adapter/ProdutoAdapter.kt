package com.example.crudfirebase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crudfirebase.R
import com.example.crudfirebase.model.Produto

class ProdutoAdapter (private var listaProdutos:ArrayList<Produto>) : RecyclerView.Adapter<ProdutoAdapter.MyViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textoId: TextView
        var textNome: TextView
        var textoPreco: TextView

        init {
            textoId = view.findViewById(R.id.textoId)
            textNome = view.findViewById(R.id.textoNome)
            textoPreco = view.findViewById(R.id.textoPreco)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ProdutoAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_produto, parent, false) as View
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textoId.text = listaProdutos.get(position).id.toString()
        holder.textNome.text = listaProdutos.get(position).nome
        holder.textoPreco.text = listaProdutos.get(position).preco.toString()
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClicked(
                holder.itemView,
                position
            )
        }
    }

    override fun getItemCount() = listaProdutos.size

    interface OnItemClickListener {
        fun onItemClicked(view: View, position: Int)
    }
}