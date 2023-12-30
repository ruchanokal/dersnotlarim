package com.ders.notlarim.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ders.notlarim.databinding.DocumentRowBinding

class DocumentAdapter(val documentList : ArrayList<String>) : RecyclerView.Adapter<DocumentAdapter.DocumentHolder>() {

    class DocumentHolder(val binding: DocumentRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentHolder {
        val binding = DocumentRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DocumentHolder(binding)
    }

    override fun onBindViewHolder(holder: DocumentHolder, position: Int) {
        holder.binding.documentNameText.text = documentList.get(position)
    }

    override fun getItemCount(): Int {
        return documentList.size
    }
}