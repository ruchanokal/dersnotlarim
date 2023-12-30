package com.ders.notlarim.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ders.notlarim.activities.SignActivity
import com.ders.notlarim.adapter.AccountsAdapter
import com.ders.notlarim.adapter.DocumentAdapter
import com.ders.notlarim.adapter.StuDocumentAdapter
import com.ders.notlarim.databinding.FragmentMainBinding
import com.ders.notlarim.model.Account
import com.ders.notlarim.model.StuDocument
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MainFragment : Fragment() {

    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val TAG = "MainFragment"
    var userUid = ""
    private lateinit var alertDialog: AlertDialog.Builder
    var kullaniciAdi = ""
    private var lessonsList = arrayListOf<String>()

    var documentAdapter : DocumentAdapter? = null
    var stuDocumentAdapter : StuDocumentAdapter? = null
    var teacherAccountsAdapter : AccountsAdapter? = null
    var studentAccountsAdapter : AccountsAdapter? = null

    var documentList = arrayListOf<String>()
    var stuDocumentList = arrayListOf<StuDocument>()
    var teacherAccountsList = arrayListOf<Account>()
    var studentAccountsList = arrayListOf<Account>()

    val READ_REQUEST_CODE = 0
    val WRITE_REQUEST_CODE = 1
    private lateinit var pickFileLauncher: ActivityResultLauncher<String>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        val view = binding.root
        registerLauncher()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        userUid = mAuth.currentUser?.uid.toString()

        giris()

        signOut()

    }



    private fun giris() {

        val intent = requireActivity().intent
        val definiteNumber = intent.getIntExtra("definite",0)

        Log.i(TAG,"definiteNumber --> " + definiteNumber)

        if ( definiteNumber == 1) {

            Log.i(TAG,"yeni oluşturulan (öğrenci) kullanıcısı --> " + kullaniciAdi)
            binding.studentLayout.visibility = View.VISIBLE
            kullaniciAdi = intent.getStringExtra("namesurname")!!
            lessonsList = intent.getStringArrayListExtra("lessons")!!
            binding.nameSurnameText.setText(kullaniciAdi)
            studentDokumanListele()

        } else if ( definiteNumber == 2) {

            Log.i(TAG,"öğrenci giriş yaptı --> " + mAuth.currentUser?.email)
            binding.studentLayout.visibility = View.VISIBLE
            kullaniciAdiGetir("Student")
        } else if ( definiteNumber == 3) {

            Log.i(TAG,"yeni oluşturulan (öğretmen) kullanıcısı --> " + kullaniciAdi)
            binding.teacherLayout.visibility = View.VISIBLE
            kullaniciAdi = intent.getStringExtra("namesurname")!!
            binding.nameSurnameText.setText(kullaniciAdi)
            teacherDokumanListele()

        } else if ( definiteNumber == 4) {

            Log.i(TAG,"(öğretmen) giriş yaptı --> " + mAuth.currentUser?.email)
            binding.teacherLayout.visibility = View.VISIBLE
            kullaniciAdiGetir("Teacher")
            teacherDokumanListele()

        } else if ( definiteNumber == 5) {

            Log.i(TAG,"(admin) giriş yaptı --> " + mAuth.currentUser?.email)
            binding.adminLayout.visibility = View.VISIBLE
            kullaniciAdiGetir("Admin")
            kullanicilariListele()

        }

    }

    private fun kullanicilariListele() {

        teacherAccountsAdapter = AccountsAdapter(teacherAccountsList,"t",db)
        binding.teacherAccountsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.teacherAccountsRecyclerView.adapter = teacherAccountsAdapter

        db.collection("Teacher").addSnapshotListener { value, error ->

            if (error != null){

                Toast.makeText(requireContext(),"Error occured: " + error.localizedMessage,Toast.LENGTH_LONG).show()

            } else {

                if (value != null) {

                    if (!value.isEmpty){

                        teacherAccountsList.clear()
                        val documents = value.documents
                        for (document in documents){

                            val nameSurname = document.getString("namesurname")
                            if (nameSurname != null) {
                                Log.i(TAG,"teacher nameSurname: " + nameSurname)
                                val account = Account(nameSurname,document.id)
                                teacherAccountsList.add(account)
                            }

                        }

                        Log.i(TAG,"teacherList size: " + teacherAccountsList.size)


                        teacherAccountsAdapter?.notifyDataSetChanged()

                    }

                }
            }
        }

        studentAccountsAdapter = AccountsAdapter(studentAccountsList,"s",db)
        binding.studentAccountsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.studentAccountsRecyclerView.adapter = studentAccountsAdapter

        db.collection("Student").addSnapshotListener { value, error ->

            if (error != null){

                Toast.makeText(requireContext(),"Error occured: " + error.localizedMessage,Toast.LENGTH_LONG).show()

            } else {

                if (value != null) {

                    if (!value.isEmpty){

                        studentAccountsList.clear()
                        val documents = value.documents
                        for (document in documents){

                            val nameSurname = document.getString("namesurname")
                            if (nameSurname != null) {
                                Log.i(TAG,"stu nameSurname: " + nameSurname)
                                val account = Account(nameSurname,document.id)
                                studentAccountsList.add(account)
                            }

                        }
                        Log.i(TAG,"studentList size: " + studentAccountsList.size)

                        studentAccountsAdapter?.notifyDataSetChanged()


                    }

                }


            }

        }

    }

    private fun studentDokumanListele() {

        stuDocumentAdapter = StuDocumentAdapter(lessonsList,stuDocumentList)
        binding.lessonsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.lessonsRecyclerView.adapter = stuDocumentAdapter

        db.collection("Teacher").addSnapshotListener { value, error ->

            if (error != null){

                Toast.makeText(requireContext(),"Error occured: " + error.localizedMessage,Toast.LENGTH_LONG).show()

            } else {

                if (value != null) {

                    if (!value.isEmpty){

                        val documents = value.documents

                        for (document in documents){

                            val selectedLesson = document.getString("selectedlesson")

                            if (lessonsList.contains(selectedLesson)){

                                Log.i(TAG,"lessonList contains")

                                val documentArray = document.get("documentArray") as ArrayList<Map<String,Any>>?

                                documentArray?.forEach {

                                    Log.i(TAG,"documentArray name: " + it.get("name"))
                                    val stuDocument = selectedLesson?.let { sLesson ->
                                        StuDocument(it.get("name") as String,
                                            it.get("url") as String, sLesson
                                        )
                                    }
                                    if (stuDocument != null) {
                                        stuDocumentList.add(stuDocument)
                                        Log.i(TAG,"stuDocumentList added: " + stuDocument.name)
                                    }
                                }
                            }

                        }

                        stuDocumentAdapter?.notifyDataSetChanged()

                    }

                }


            }



        }
    }

    private fun teacherDokumanListele() {

        binding.addDocumentButton.setOnClickListener {
            izinKontrol(it)
        }

        documentAdapter = DocumentAdapter(documentList)
        binding.dokumanRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dokumanRecyclerView.adapter = documentAdapter

        db.collection("Teacher").document(userUid).addSnapshotListener { value, error ->

            if (error != null){

                Toast.makeText(requireContext(),"Error occured: " + error.localizedMessage,Toast.LENGTH_LONG).show()

            } else {

                if (value != null) {

                    if (value.exists()){

                        documentList.clear()

                        val documentArray = value.get("documentArray") as ArrayList<Map<String,Any>>?

                        documentArray?.forEach {
                            documentList.add(it.get("name") as String)
                        }
                        documentAdapter?.notifyDataSetChanged()

                    }

                }


            }



        }


    }

    private fun izinKontrol(view : View) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Permission needed!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission",
                    View.OnClickListener {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            // izin verildiyse...
            val mimeType = "application/pdf"
            pickFileLauncher.launch(mimeType)
        }
    }

    private fun registerLauncher() {

        pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                Log.i(TAG,"uri not null -> " + uri)
                val storageRef = Firebase.storage.reference
                val pdfRef = storageRef.child("pdfs").child(uri.lastPathSegment!!)
                Log.i(TAG,"uri not null-2 -> " + uri)
                binding.progressBarStorage.visibility = View.VISIBLE

                pdfRef.putFile(uri).addOnSuccessListener { taskSnapshot ->

                        Log.i(TAG,"dosya storage a yüklendi")

                        pdfRef.downloadUrl.addOnSuccessListener { lastUri ->

                            val downloadUrl = lastUri.toString()

                            val documentRef = db.collection("Teacher").document(userUid)

                            val fileData = hashMapOf(
                                "name" to uri.lastPathSegment!!,
                                "url" to downloadUrl
                            )
                            documentRef.update("documentArray",FieldValue.arrayUnion(fileData))
                            Log.i(TAG,"dosya firestora a yüklendi: " + downloadUrl)
                            binding.progressBarStorage.visibility = View.GONE
                            Toast.makeText(requireContext(),"Döküman başarıyla eklendi!",Toast.LENGTH_LONG).show()


                        }


                     }
                    .addOnFailureListener { exception ->
                        Log.e(TAG,"yükleme başarısız: " + exception.localizedMessage)
                        binding.progressBarStorage.visibility = View.GONE

                    }
            }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                //permission granted
                val mimeType = "application/pdf"
                pickFileLauncher.launch(mimeType)
            } else {
                //permission denied
                Toast.makeText(requireActivity(), "Permisson needed!", Toast.LENGTH_LONG).show()
            }
        }
    }



    private fun kullaniciAdiGetir(collectionName: String) {

        db.collection(collectionName).document(userUid).get().addOnSuccessListener { doc ->

            if (doc != null) {

                if (doc.exists()){

                    kullaniciAdi = doc["namesurname"] as String
                    binding.nameSurnameText.text = kullaniciAdi

                    if (collectionName.equals("Student")){
                        val mylessonsList = doc["lessons"] as ArrayList<String>?
                        mylessonsList?.let {
                            lessonsList.addAll(it)
                        }
                        studentDokumanListele()
                    }


                }
            }

        }.addOnFailureListener {

            Toast.makeText(requireContext(),"Lütfen internet bağlantınızı kontrol edin!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun signOut() {

        binding.signOutButton.setOnClickListener {

            alertDialog = AlertDialog.Builder(requireContext())

            alertDialog.setTitle("Çıkış Yap")
            alertDialog.setMessage("Çıkış yapmak istediğinize emin misiniz?")
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Evet") { dialog,which ->

                mAuth.signOut()
                val intent = Intent(requireActivity(), SignActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            }.setNeutralButton("İptal") { dialog,which ->


            }

            alertDialog.show()


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding= null
    }

}