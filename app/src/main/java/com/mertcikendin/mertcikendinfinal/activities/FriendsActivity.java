package com.mertcikendin.mertcikendinfinal.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.mertcikendin.mertcikendinfinal.R;
import com.mertcikendin.mertcikendinfinal.adapters.FriendAdapter;
import com.mertcikendin.mertcikendinfinal.databinding.ActivityFriendsBinding;
import com.mertcikendin.mertcikendinfinal.models.Friend;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
    private ActivityFriendsBinding activityFriendsBinding = null;
    private FriendAdapter friendAdapter = null;
    private ArrayList<Friend> friendArrayList = null;

    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser = null;
    private FirebaseDatabase firebaseDatabase = null;
    private DatabaseReference databaseReference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFriendsBinding = DataBindingUtil.setContentView(FriendsActivity.this, R.layout.activity_friends);

        initializeFirebase();
        setLayoutManager();
        setItems();

        activityFriendsBinding.ivFriendsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("FRIENDSHIP");
    }

    private void setLayoutManager() {
        activityFriendsBinding.rvFriends.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FriendsActivity.this, LinearLayoutManager.VERTICAL, false);
        activityFriendsBinding.rvFriends.setLayoutManager(linearLayoutManager);
    }

    private void setItems() {
        friendArrayList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendArrayList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    Friend friend = new Friend(d.getKey(), String.valueOf(d.child("from").getValue()), String.valueOf(d.child("to").getValue()), String.valueOf(d.child("controller").getValue()));

                    if (friend.getTo().equals(firebaseUser.getEmail())) {
                        friendArrayList.add(friend);
                    }


                }
                setAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setAdapter() {
        friendAdapter = new FriendAdapter(FriendsActivity.this, friendArrayList);
        friendAdapter.notifyDataSetChanged();
        activityFriendsBinding.rvFriends.setAdapter(friendAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FriendsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}