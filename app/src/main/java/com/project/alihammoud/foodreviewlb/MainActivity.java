package com.project.alihammoud.foodreviewlb;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.alihammoud.foodreviewlb.view.HomeActivity;
import com.project.alihammoud.foodreviewlb.view.ResetPasswordActivity;
import com.project.alihammoud.foodreviewlb.view.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPasswordSignIn";
    private Button Login, ForgetPass, SignUp;
    private ProgressBar loading;

    private EditText edtPassword, edtUsername;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Access a Cloud Firestore instance from your Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        loading = (ProgressBar) findViewById(R.id.progressBar);

        Login = (Button) findViewById(R.id.btnsignin);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn(edtUsername.getText().toString().trim(),edtPassword.getText().toString().trim());

            }
        });



        SignUp = (Button) findViewById(R.id.signup);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });

        ForgetPass = (Button) findViewById(R.id.forgot_pass);
        ForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn(final String email, final String password) {
        Log.d(TAG, "signIn:" + email);

        if (!validateForm()) {
            return;
        }
        loading.setVisibility(View.VISIBLE);


// [START sign_in_with_email]
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            auth = FirebaseAuth.getInstance();
                            user = auth.getCurrentUser();
                            FirebaseUser account = auth.getCurrentUser();
                            updateUI(account);

                            Toast.makeText(MainActivity.this, "Welcome Back!", Toast.LENGTH_LONG).show();

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "\"Wrong E-mail or Password",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        loading.setVisibility(View.INVISIBLE);
                        // [END_EXCLUDE]*/
                    }
                });
        // [END sign_in_with_email]
    }


    private void updateUI(FirebaseUser account) {
        // hideProgressDialog();
        if (account != null) {
            //DO THE SIGNIN METHODBY GETTING USERNAME AND PASSWORD FROM THE USER CLASS

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = edtUsername.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edtUsername.setError("Required.");
            valid = false;
        } else {
            edtUsername.setError(null);
        }

        String password = edtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Required.");
            valid = false;
        } else {
            edtPassword.setError(null);
        }


        return valid;
    }
}
