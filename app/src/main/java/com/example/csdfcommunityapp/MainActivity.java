package com.example.csdfcommunityapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    /*
     The following are declared globally so that they can be accessed anywhere.
     The RC_SIGN_IN is declared to 1 to check the permission, whether user
     has a successful sign in
     The code uses Firebase to verify the authentication & Storage of data
     */
    EditText emailId, password,rePassword;
    Button btnSignUp;
    ImageView tvLogIn;
    ImageView tvResetPassword;
    FirebaseAuth mFirebaseAuth;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInButton = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // This provides the currently signed in user. If they have a active login session, then they are directly logged in

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Intent i = new Intent(MainActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
        else{

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn();
                }
            });


            mFirebaseAuth = FirebaseAuth.getInstance();
            emailId = findViewById(R.id.userEmail);
            password = findViewById(R.id.userPasswd);
            rePassword = findViewById(R.id.reUserPasswd);
            btnSignUp = findViewById(R.id.buttonLogIn);
            tvLogIn = findViewById(R.id.signUpActivity);
            tvResetPassword = findViewById(R.id.resetActivity);

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String email = emailId.getText().toString();
                    String pwd = password.getText().toString();
                    String rePwd = rePassword.getText().toString();

                    if(email.isEmpty()){
                        emailId.setError("Please enter email id");
                        emailId.requestFocus();
                    }
                    else  if(pwd.isEmpty()){
                        password.setError("Please enter your password");
                        password.requestFocus();
                    }
                    else if(rePwd.isEmpty()){
                        rePassword.setError("Please re-enter your password");
                        rePassword.requestFocus();
                    }
                    else  if((email.isEmpty() && pwd.isEmpty())||(rePwd.isEmpty())){
                        Toast.makeText(MainActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                    }
                    else  if(!(email.isEmpty() && pwd.isEmpty() && rePwd.isEmpty())){
                        if(!pwd.equals(rePwd)){
                            rePassword.setError("Passwords do not match!!!");
                            rePassword.requestFocus();
                        }else {
                            mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // The following code will run only if everything goes out smoothly and the new user is created.
                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    }
                                }
                            });
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Error Occurred!",Toast.LENGTH_SHORT).show();

                    }
                }
            });

            /*
            The returning user will not be able to create a duplicate account (Settings can be changed in the firebase console)
            The Login Button will take the user from the sign Up Activity to login activity
             */

            tvLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(i);
                }
            });

            /*
            If the user has forgotten the password, then he will be taken to the Reset Email activity
            Note: If the user has opted to Login through his Google Credentials, then his email
            cannot be used to reset the password.
            To access, he needs to reset his Google Account password.
            This will hold through unless there is another way implemented to LogIn
             */
            tvResetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this,ResetActivity.class);
                    startActivity(i);
                }
            });
        }

    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //This method is important to check or handle the request code to see the SIGN In and pass it to the handler
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(MainActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    /*
     The following method again performs the check to see if the user exists or not.
     If not, then it nicely lets you go to the new Activity with your stamp
    */
    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        //  Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "Account Failed", Toast.LENGTH_SHORT).show();
        }
    }

    //The following method will invoke the successful activity change
    private void updateUI(FirebaseUser fUser){

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account !=  null){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));


        }

    }
}
