    package com.romp.khel.Fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.romp.khel.R
import com.romp.khel.adapters.MyCreatedLTPadapter
import com.romp.khel.dataclass.LookingtoPlayRoom
import com.romp.khel.keyy
import com.romp.khel.keyyy


class Myteam : Fragment() {
    var database = FirebaseDatabase.getInstance().getReference()
    var conditionref: DatabaseReference = database.child("lookingtoplay")
    var ref: DatabaseReference = database.child("teams")
    lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_myteam, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser==null) { Navigation.findNavController(view).navigate(R.id.action_myteam_to_signUp_Login_Fragment) }

        var player1: TextView =view.findViewById(R.id.p1)
        var player2: TextView =view.findViewById(R.id.p2)
        var player3: TextView =view.findViewById(R.id.p3)
        var player4: TextView =view.findViewById(R.id.p4)
        var player5: TextView =view.findViewById(R.id.p5)
        var playericon1: TextView =view.findViewById(R.id.player1)
        var playericon2: TextView =view.findViewById(R.id.player2)
        var playericon3: TextView =view.findViewById(R.id.player3)
        var playericon4: TextView =view.findViewById(R.id.player4)
        var playericon5: TextView =view.findViewById(R.id.player5)
        var teamname:TextView=view.findViewById(R.id.teamname)


        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"onCancelled called", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    if (currentUser?.email==postSnapshot.child("email1").value) {
                        player1.text=currentUser!!.displayName
                        player2.text=currentUser.email
                        teamname.text=postSnapshot.child("teamname").value.toString()
                        playericon1.text=player1.text
                    }
                }
            }
        })




        var createteambutton:Button=view.findViewById(R.id.createteam_button)
        createteambutton.setOnClickListener {
            if (auth.currentUser!=null) {Navigation.findNavController(view).navigate(R.id.action_myteam_to_create_team)}
            else {Navigation.findNavController(view).navigate(R.id.action_myteam_to_signUp_Login_Fragment)}
        }

        var jointeambutton:Button=view.findViewById(R.id.jointeam_button)
        jointeambutton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_myteam_to_join_Team)
        }

        val radapter= MyCreatedLTPadapter()
        view.findViewById<RecyclerView>(R.id.mycreatedLTP_recycleview).adapter=radapter

        val progressbar: ProgressBar =view.findViewById(R.id.mycreatedLTP_progressbar)
        progressbar.isIndeterminate
        progressbar.setVisibility(View.VISIBLE)

        var details:MutableList<LookingtoPlayRoom> = mutableListOf()

        conditionref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"onCancelled called", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                keyyy= mutableListOf()
                for (postSnapshot in dataSnapshot.children) {
                    if (postSnapshot.exists() && currentUser?.uid==postSnapshot.child("uid").value) {
                        details.add(postSnapshot.getValue<LookingtoPlayRoom>()!!)
                        keyyy.add(postSnapshot.key.toString())
                    }

                }
                radapter.data=details
                progressbar.setVisibility(View.INVISIBLE)
            }

        })
    }
}
