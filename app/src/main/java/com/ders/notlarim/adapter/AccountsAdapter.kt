package com.ders.notlarim.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.ders.notlarim.activities.SignActivity
import com.ders.notlarim.databinding.AccountsRowBinding
import com.ders.notlarim.model.Account
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class AccountsAdapter(val accountsList : ArrayList<Account>, val fromWho: String, val db: FirebaseFirestore) : RecyclerView.Adapter<AccountsAdapter.AccountHolder>() {

    class AccountHolder(val binding: AccountsRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
        val binding = AccountsRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AccountHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountHolder, position: Int) {
        holder.binding.userNameText.text = accountsList.get(position).name

        holder.binding.deleteButton.setOnClickListener {

            val alertDialog = AlertDialog.Builder(holder.itemView.context)

            alertDialog.setTitle("Hesap Sil")
            alertDialog.setMessage("Bu hesabı silmek istediğinize emin misiniz?")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Evet") { dialog,which ->

                var documentRef : DocumentReference? = null

                if (fromWho.equals("t")){
                    documentRef = db.collection("Teacher").document(accountsList.get(position).documentId)
                } else {
                    documentRef = db.collection("Student").document(accountsList.get(position).documentId)
                }

                documentRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(holder.itemView.context,"Kişi başarıyla silindi!",Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(holder.itemView.context,"Silinirken hata oluştu!",Toast.LENGTH_LONG).show()
                    }

            }.setNeutralButton("İptal") { dialog,which ->


            }

            alertDialog.show()


        }
    }

    override fun getItemCount(): Int {
        return accountsList.size
    }
}