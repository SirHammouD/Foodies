package com.project.alihammoud.foodreviewlb.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.alihammoud.foodreviewlb.R;

public class PersonalInformationActivity extends AppCompatActivity implements View.OnClickListener {

    TextView setFirstname, setLastname, setEmail, setPhone, verify, setUserType, setBirthday, setGender;
    ImageButton verifyBtn;
    String userID;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Account Information");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        setFirstname = findViewById(R.id.setFirstname);
        setLastname =findViewById(R.id.setLastname);
        setEmail = findViewById(R.id.setEmail);
        setPhone = findViewById(R.id.setPhone);
        setUserType = findViewById(R.id.setUserType);
        setBirthday = findViewById(R.id.setBirthday);
        setGender = findViewById(R.id.setGender);

        verify = findViewById(R.id.verify);
        verifyBtn = (ImageButton) findViewById(R.id.verify_button);
        findViewById(R.id.verify_button).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.verify_button)
        {
            currentUser.reload();

            if ( currentUser.isEmailVerified()){

                verify.setText("Verified");
                verifyBtn.setVisibility(View.GONE);

            }
            else {

                currentUser.sendEmailVerification().addOnCompleteListener( new OnCompleteListener() {
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(PersonalInformationActivity.this,
                                    " Email verification resent to " + currentUser.getEmail(),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PersonalInformationActivity.this,
                                    "Failed to resend email verification",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }


        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser!= null){

            if (currentUser.isEmailVerified()){
                verify.setText("Verified");
                verifyBtn.setVisibility(View.GONE);

            }
            else {
                verify.setText("Not Verified");
                verifyBtn.setVisibility(View.VISIBLE);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference userData = db.collection("Users").document(userID);

            userData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if (task.getResult().exists()){
                            String firstname = task.getResult().getString("first_name");
                            String lastname = task.getResult().getString("last_name");
                            String email = task.getResult().getString("email");
                            String phone = task.getResult().getString("phone_number");
                            String usertype = task.getResult().getString("user_type");
                            String formattedNumber = PhoneNumberUtils.formatNumber(phone);
                            String dob = task.getResult().getString("date_of_birth");
                            String gender = task.getResult().getString("gender");

                            setFirstname.setText(firstname);
                            setLastname.setText(lastname);
                            setEmail.setText(email);
                            setPhone.setText(formattedNumber);
                            setUserType.setText(usertype);
                            setBirthday.setText(dob);
                            setGender.setText(gender);


                            // Map<String, Object> info = documentSnapshot.getData();
                            //setFirst_name.setText(ID);
                        }
                    }
                    else {
                        String error = task.getException().getMessage();
                        Toast.makeText(PersonalInformationActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                }
            });


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        // menuInflater.inflate(R.menu.);
        return super.onCreateOptionsMenu(menu);
    }

}
