package com.romp.khel.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.romp.khel.R
import com.romp.khel.dataclass.users


class After_Login : Fragment() {
    lateinit var name: TextView
    lateinit var email: TextView
    lateinit var id: TextView
    lateinit var signin: GoogleSignInAccount
    lateinit var signoutbutton: Button
    var database = FirebaseDatabase.getInstance().getReference()
    var conditionref: DatabaseReference = database.child("users")
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_after__login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        name= view.findViewById(R.id.display_name)
        email=view.findViewById(R.id.account_info_email)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        var flag=false

        val progressbar: ProgressBar =view.findViewById(R.id.afterlogin_progressbar)
        progressbar.isIndeterminate
        progressbar.setVisibility(View.VISIBLE)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        var mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        signoutbutton=view.findViewById(R.id.signout_button2)
        signin= GoogleSignIn.getLastSignedInAccount(activity)!!
        if (signin!=null) {
            name.text=signin.displayName
            email.text=signin.email

            conditionref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity,"onCancelled called", Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    flag=false
                    for (postSnapshot in dataSnapshot.children) {
                        if (postSnapshot.exists() && currentUser?.email==postSnapshot.child("email").value) {
                            flag=true
                            progressbar.setVisibility(View.INVISIBLE)
                        }
                    }
                    if (flag==false) {
                        if (currentUser!=null) {
                            var detail = users(
                                currentUser!!.email,
                                currentUser!!.displayName,
                                currentUser!!.uid
                     //           currentUser!!.photoUrl.toString()
                            )
                            database.child("users").push().setValue(detail).addOnSuccessListener {
                                progressbar.setVisibility(View.INVISIBLE)
                            }
                        }
                    }
                }
            })

        }
        signoutbutton.setOnClickListener {
            mGoogleSignInClient.signOut()
            Firebase.auth.signOut()
            view?.let { Navigation.findNavController(it).navigate(R.id.action_after_Login_to_signUp_Login_Fragment) }
        }



    }

}