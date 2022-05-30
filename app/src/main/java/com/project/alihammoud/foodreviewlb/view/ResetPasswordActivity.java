package com.project.alihammoud.foodreviewlb.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.project.alihammoud.foodreviewlb.R;

public class ResetPasswordActivity extends AppCompatActivity {
    private Button reset;
    private EditText EmailField;
    private FirebaseAuth auth;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        auth = FirebaseAuth.getInstance();

        EmailField = (EditText) findViewById(R.id.emailField);
        loading = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Reset password");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);

                if (!validateForm()) {
                    return;
                }

                String userEmail = EmailField.getText().toString().trim();
                auth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ResetPasswordActivity.this,"Please check your Email to reset your password",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            String message =  task.getException().getMessage();
                            Toast.makeText(ResetPasswordActivity.this,"Error Occurred: " + message,Toast.LENGTH_SHORT).show();
                        }

                        loading.setVisibility(View.GONE);
                    }
                });



            }
        });




    }

    private boolean validateForm() {
        boolean valid = true;

        String userEmail = EmailField.getText().toString();
        if (TextUtils.isEmpty(userEmail)) {
            EmailField.setError("Required.");
            valid = false;
        } else {
            EmailField.setError(null);
        }


        return valid;
    }
}
