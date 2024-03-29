package com.example.maisbarato.presentation.view.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.maisbarato.databinding.OfertaItemBinding
import com.example.maisbarato.data.model.Oferta
import java.text.SimpleDateFormat
import java.util.*

class ListaOfertaAdapter(private var listaOferta: List<Oferta>, val clickListener: (Oferta) -> Unit) :
    RecyclerView.Adapter<ListaOfertaAdapter.OfertaViewHolder>() {

    inner class OfertaViewHolder(private val binding: OfertaItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(oferta: Oferta, clickListener: (Oferta) -> Unit) {

            oferta.apply {

                Glide.with(binding.imagemOferta)
                    .load(oferta.listaUrlImagem.firstOrNull())
                    .into(binding.imagemOferta)

                val data = Date(oferta.dataInclusao)
                val formatDate = SimpleDateFormat("dd/MM/yyyy")
                val dateText = formatDate.format(data)

                binding.precoAntigo.paintFlags = binding.precoAntigo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                binding.tituloPromocao.text = titulo
                binding.nomeLoja.text = nomeLoja
                binding.dataInclusao.text = dateText
                binding.precoAntigo.text = valorAntigo?.toString()
                binding.precoNovo.text = valorNovo.toString()

                binding.root.setOnClickListener { clickListener(oferta) }
            }
        }

    }

    fun atualizaListaOferta(listaOferta: List<Oferta>) {
        this.listaOferta = listaOferta
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertaViewHolder {
        val ofertaItemBinding = OfertaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return OfertaViewHolder(ofertaItemBinding)
    }

    override fun onBindViewHolder(holder: OfertaViewHolder, position: Int) {
        holder.bind(listaOferta[position], clickListener)
    }

    override fun getItemCount(): Int  = listaOferta.size

}