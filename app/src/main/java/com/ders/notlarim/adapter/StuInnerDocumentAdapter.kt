package com.ders.notlarim.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ders.notlarim.databinding.DocumentRowBinding
import com.ders.notlarim.model.StuDocument


class StuInnerDocumentAdapter(val documentList : ArrayList<StuDocument>) : RecyclerView.Adapter<StuInnerDocumentAdapter.StuInnerHolder>() {

    class StuInnerHolder(val binding : DocumentRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StuInnerHolder {
        val binding = DocumentRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StuInnerHolder(binding)
    }

    override fun onBindViewHolder(holder: StuInnerHolder, position: Int) {
        holder.binding.documentNameText.text = documentList.get(position).name

        holder.itemView.setOnClickListener {

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(documentList.get(position).url))
            holder.itemView.context.startActivity(browserIntent)

        }
    }

    override fun getItemCount(): Int {
        return documentList.size
    }


}