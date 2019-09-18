package com.example.potap.findme.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataManager {

    private static DataManager instance;

    private DatabaseReference databaseReference;
    private DatabaseReference usersReference;
    private DatabaseReference eventsReference;
    private FirebaseAuth firebaseAuth;

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public DatabaseReference getUsersReference() {
        return usersReference;
    }

    public DatabaseReference getEventsReference() {
        return eventsReference;
    }

    private DataManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = databaseReference.child("users");
        eventsReference = databaseReference.child("events");
    }


    public static DataManager getInstance() {
        {
            if (instance == null)
                instance = new DataManager();
            return instance;
        }
    }

}
