package com.ders.notlarim.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ders.notlarim.databinding.StuLessonsRowBinding
import com.ders.notlarim.model.StuDocument

class StuDocumentAdapter(val lessonsList : ArrayList<String>, val stuDocumentList : ArrayList<StuDocument>) : RecyclerView.Adapter<StuDocumentAdapter.StuDocumentHolder>() {

    class StuDocumentHolder(val binding : StuLessonsRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StuDocumentHolder {
       val binding = StuLessonsRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
       return StuDocumentHolder(binding)
    }

    override fun onBindViewHolder(holder: StuDocumentHolder, position: Int) {

        val lesson = lessonsList.get(position)
        holder.binding.lessonsText.text = lesson

        val filteredDocList = stuDocumentList.filter { it.lesson.equals(lesson) }

        var isVisible = false
        holder.binding.stuDocumentRecyclerView.visibility = View.GONE
        holder.binding.dokumanBosUyariText.visibility = View.GONE

        holder.binding.mainLayout.setOnClickListener {

            isVisible = !isVisible

            if (filteredDocList.isNotEmpty()) {
                holder.binding.stuDocumentRecyclerView.visibility = if(isVisible) View.VISIBLE else View.GONE
            } else {
                holder.binding.dokumanBosUyariText.visibility = if(isVisible) View.VISIBLE else View.GONE
            }
        }

        if (filteredDocList.isNotEmpty()){
            val innerAdapter = StuInnerDocumentAdapter(filteredDocList as ArrayList<StuDocument>)
            holder.binding.stuDocumentRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.binding.stuDocumentRecyclerView.adapter = innerAdapter
        } else {
            holder.binding.stuDocumentRecyclerView.visibility = View.GONE
        }




    }

    override fun getItemCount(): Int {
        return lessonsList.size
    }
}