package com.example.csdfcommunityapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetActivity extends AppCompatActivity {
    /*
    This Activity will nicely send a reset email to your account
     */
    EditText emailId;
    Button btnResetPasswd;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.userEmail);
        btnResetPasswd = findViewById(R.id.buttonLogIn);
        btnResetPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();

                if (email.isEmpty()) {
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                } else if (!email.isEmpty()) {
                    mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(ResetActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(ResetActivity.this, "Reset Error, Please Try Again", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intToHome = new Intent(ResetActivity.this, LoginActivity.class);
                                startActivity(intToHome);
                            }
                        }
                    });
                } else {
                    Toast.makeText(ResetActivity.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
