package com.example.potap.findme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {

    private Button loginButton;
    private ImageButton registrationButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText emailText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.bt_login);
        progressBar = findViewById(R.id.progress_bar);
        emailText = findViewById(R.id.email_edit_text);
        passwordText = findViewById(R.id.password_edit_text);
        registrationButton = findViewById(R.id.user_profile_photo);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //User is signed in
                } else {
                    //User is signed out
                }
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                if (email.isEmpty() || password.isEmpty())
                    Toast.makeText(AuthenticationActivity.this, "Email or Password is empty", Toast.LENGTH_SHORT).show();
                else
                    signing(email, password);
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                if (email.isEmpty() || password.isEmpty())
                    Toast.makeText(AuthenticationActivity.this, "Email or Password is empty", Toast.LENGTH_SHORT).show();
                else
                    registration(email, password);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //       updateUI(currentUser);
    }

    private void signing(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                    Toast.makeText(AuthenticationActivity.this, "Authentication is successful", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(AuthenticationActivity.this, "User isn't registered", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registration(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                    Toast.makeText(AuthenticationActivity.this, "Registration is successful", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(AuthenticationActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
