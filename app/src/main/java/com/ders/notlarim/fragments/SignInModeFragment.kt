package com.ders.notlarim.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ders.notlarim.activities.MainActivity
import com.ders.notlarim.databinding.FragmentSignInModeBinding
import com.google.firebase.auth.FirebaseAuth


class SignInModeFragment : Fragment() {

    private var _binding : FragmentSignInModeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth : FirebaseAuth
    var loginType = ""
    var value = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInModeBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        val prefences = requireActivity().getSharedPreferences("com.stayfit.stayfit", Context.MODE_PRIVATE)
        hizliGiris(prefences)

        binding.studentLoginButton.setOnClickListener {

            val action = SignInModeFragmentDirections.actionSignInModeFragmentToStuSignInFragment()
            Navigation.findNavController(it).navigate(action)
            prefences.edit().putString("login","student").apply()

        }

        binding.teacherLoginButton.setOnClickListener {

            val action = SignInModeFragmentDirections.actionSignInModeFragmentToTeacherSignInFragment()
            Navigation.findNavController(it).navigate(action)
            prefences.edit().putString("login","teacher").apply()

        }

        binding.adminLoginButton.setOnClickListener {

            val action = SignInModeFragmentDirections.actionSignInModeFragmentToAdminSignInFragment()
            Navigation.findNavController(it).navigate(action)
            prefences.edit().putString("login","admin").apply()
        }
    }


    private fun hizliGiris(prefences  : SharedPreferences) {
        if (mAuth.currentUser != null ) {

            loginType = prefences.getString("login","")!!

            if (loginType.equals("student")) {
                value = 2
            } else if (loginType.equals("teacher")){
                value = 4
            } else if (loginType.equals("admin")){
                value = 5
            } else
                value = 0

            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("definite",value)
            startActivity(intent)
            requireActivity().finish()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}