package com.ders.notlarim.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import com.ders.notlarim.R
import com.ders.notlarim.activities.MainActivity
import com.ders.notlarim.databinding.FragmentStuSignUpBinding
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class StuSignUpFragment : Fragment() {

    private var _binding : FragmentStuSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var db : FirebaseFirestore
    private val TAG = "StuSignUpFragment"
    private lateinit var mAuth: FirebaseAuth
    var reference : ListenerRegistration? = null
    var userUid = ""
    private lateinit var email : String
    private lateinit var nameSurname : String
    private var pass : String = ""
    private var pass2 : String = ""
    private var selectedCheckBoxList = arrayListOf<CheckBox>()
    private var checkBoxList = arrayListOf<CheckBox>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStuSignUpBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        checkBoxList = arrayListOf<CheckBox>(binding.mathCheckBox,
            binding.fizikCheckBox,
            binding.biyolojiCheckBox,
            binding.kimyaCheckBox,
            binding.tarihCheckBox,
            binding.edebiyatCheckBox)



        binding.studentSignUpButton.setOnClickListener {

            binding.progressBarSignUp.visibility = View.VISIBLE

            email = binding.editTextEmailSignUp.text.toString()
            nameSurname = binding.editTextNameSurnameSignUp.text.toString()
            pass = binding.editTextPassSignUp.text.toString()
            pass2 = binding.editTextPassAgainSignUp.text.toString()

            databaseCollection()

        }
    }

    private fun databaseCollection() {

        reference = db.collection("Student")
            .whereEqualTo("namesurname",nameSurname).addSnapshotListener { value, error ->

                Log.i(TAG,"nameSurname: " + nameSurname)

                if (value !=null) {

                    Log.i(TAG,"not null")

                    if (!value.isEmpty) {
                        Log.i(TAG,"not empty")

                        Toast.makeText(context,"Please try another username!",
                            Toast.LENGTH_LONG).show()

                        binding.progressBarSignUp.visibility = View.GONE

                        reference?.remove()

                    } else {
                        Log.i(TAG,"empty")
                        kontroller()
                    }

                } else {
                    Log.i(TAG,"null")
                    kontroller()
                }

                if (error!= null)
                    Log.i(TAG,"error: " + error)

            }
    }

    private fun kontroller() {


        if (email.equals("")
            || nameSurname.equals("")
            || pass.equals("")
            || pass2.equals("") ){

            reference?.remove()

            Toast.makeText(activity,"Please fill in the required fields!", Toast.LENGTH_LONG).show()
            binding.progressBarSignUp.visibility = View.GONE

        } else if (!pass.equals(pass2)){

            reference?.remove()

            Toast.makeText(activity,"Passwords must match!", Toast.LENGTH_LONG).show()

            binding.progressBarSignUp.visibility = View.GONE

        }  else if (areAllCheckBoxesUnchecked()){

            reference?.remove()

            Toast.makeText(activity,"Please select at least one course!", Toast.LENGTH_LONG).show()

            binding.progressBarSignUp.visibility = View.GONE

        } else  {

            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener { task ->

                if (task.isSuccessful){

                    reference?.remove()

                    userUid = mAuth.currentUser?.uid.toString()

                    binding.progressBarSignUp.visibility = View.GONE
                    saveSelectedCheckBoxes()
                    val hashMap = hashMapOf<Any,Any>()
                    val email = mAuth.currentUser?.email

                    email?.let { hashMap.put("email", it) }
                    nameSurname.let { hashMap.put("namesurname",it) }
                    val nameList = arrayListOf<String>()
                    selectedCheckBoxList.let {
                        it.forEach {
                            nameList.add(it.text.toString())
                        }
                        hashMap.put("lessons",nameList)
                    }

                    db.collection("Student").document(userUid).set(hashMap).addOnSuccessListener {

                        Log.i(TAG,"added one student more")

                        val intent = Intent(activity, MainActivity::class.java)
                        intent.putExtra("namesurname",nameSurname)
                        intent.putExtra("lessons",nameList)
                        intent.putExtra("email",email)
                        intent.putExtra("definite",1)
                        startActivity(intent)
                        requireActivity().finish()

                        Toast.makeText(activity,"Welcome ${nameSurname}", Toast.LENGTH_LONG).show()

                    }.addOnFailureListener {

                        Log.e(TAG,"Failed to create student user, try again!")

                    }



                }

            }.addOnFailureListener { exception ->


                try {
                    throw exception
                }   catch (e: FirebaseAuthUserCollisionException) {

                    reference?.remove()

                    Toast.makeText(activity,"This email address is already in use by another account",
                        Toast.LENGTH_LONG).show()
                    binding.progressBarSignUp.visibility = View.GONE

                } catch(e : FirebaseAuthWeakPasswordException) {

                    reference?.remove()

                    Toast.makeText(activity,"Please enter a password of at least 6 digits", Toast.LENGTH_LONG).show()
                    binding.progressBarSignUp.visibility = View.GONE

                } catch (e: FirebaseNetworkException) {

                    reference?.remove()

                    Toast.makeText(activity,"Please check your internet connection", Toast.LENGTH_LONG).show()
                    binding.progressBarSignUp.visibility = View.GONE

                } catch(e : FirebaseAuthInvalidCredentialsException) {

                    reference?.remove()

                    Toast.makeText(activity,e.localizedMessage, Toast.LENGTH_LONG).show()
                    binding.progressBarSignUp.visibility = View.GONE

                } catch (e: Exception) {

                    reference?.remove()

                    Toast.makeText(activity,e.localizedMessage, Toast.LENGTH_LONG).show()
                    binding.progressBarSignUp.visibility = View.GONE
                }

            }


        }
    }

    private fun areAllCheckBoxesUnchecked(): Boolean {
        for (checkBox in checkBoxList) {
            if (checkBox.isChecked) {
                return false
            }
        }
        return true
    }

    private fun saveSelectedCheckBoxes() {
        selectedCheckBoxList.clear()

        for (checkBox in checkBoxList) {
            if (checkBox.isChecked) {
                selectedCheckBoxList.add(checkBox)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}