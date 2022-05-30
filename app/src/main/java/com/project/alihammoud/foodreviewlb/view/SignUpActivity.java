package com.project.alihammoud.foodreviewlb.view;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.alihammoud.foodreviewlb.R;
import com.project.alihammoud.foodreviewlb.model.UserInfo;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "EmailPasswordSignUp";
    private ProgressBar loading;
    private Uri profile_img_uri = null;
    private FirebaseAuth auth;
    private EditText edtFirstname,edtLastname, edtPassword, edtEmail, edtPhone, edtCity;
    private CircleImageView add_profile_img;
    private StorageReference storageReference;
    private  Uri image_url;
    private FirebaseFirestore db;
    private String userID;
    private String date_birthday;
    private Button edtBirthday;
    private RadioGroup radio_Group_Gender;
    private RadioButton radio_Button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        loading = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Wyn Akalet");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        // Views
        edtFirstname = (EditText)findViewById(R.id.edtFirstName);
        edtLastname = (EditText)findViewById(R.id.edtLastName);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        edtCity = (EditText)findViewById(R.id.edtCity);
        add_profile_img = (CircleImageView) findViewById(R.id.add_profile_img);
        edtBirthday = (Button) findViewById(R.id.edtBirthday);


        //Buttons
        findViewById(R.id.btnsignup).setOnClickListener(this);
        findViewById(R.id.add_profile_img).setOnClickListener(this);
        findViewById(R.id.edtBirthday).setOnClickListener(this);


        radio_Group_Gender = (RadioGroup) findViewById(R.id.radio_Group_Gender);


         }


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        if (!validateForm()) {
            return;
        }

        loading.setVisibility(View.VISIBLE);

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           final FirebaseUser account = auth.getCurrentUser();

                            account.sendEmailVerification().addOnCompleteListener( new OnCompleteListener() {
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this,
                                                "Email verification sent to " + account.getEmail(),
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(SignUpActivity.this,
                                                "Failed to send  email verification.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                            db = FirebaseFirestore.getInstance();
                            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            Date today = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("-dd-MM-yyyy-hh:mm:ss");
                            String dateToStr = format.format(today);


                            // String time = FieldValue.serverTimestamp().toString();

                            final UUID UUID_random = UUID.randomUUID();
                            Log.d("version", "version: "+UUID_random.version());

                            String imageName = UUID_random+dateToStr;


                            final StorageReference image_path = storageReference.child("Profile Pictures").child(userID).child( imageName+".jpg");
                            image_path.putFile(profile_img_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()){
                                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(final Uri uri) {
                                                image_url = uri;
                                                int selectedId = radio_Group_Gender.getCheckedRadioButtonId();
                                                radio_Button = (RadioButton) findViewById(selectedId);
                                                String gender = radio_Button.getText().toString().trim();

                                                DocumentReference newUserInfo = db.collection("Users").document(userID);
                                                UserInfo userInfo = new UserInfo();
                                                userInfo.setFirst_name(edtFirstname.getText().toString().trim());
                                                userInfo.setLast_name(edtLastname.getText().toString().trim());
                                                userInfo.setEmail(edtEmail.getText().toString().trim());
                                                userInfo.setPassword(edtPassword.getText().toString().trim());
                                                userInfo.setCity(edtCity.getText().toString().trim());
                                                userInfo.setPhone_number(edtPhone.getText().toString().trim());
                                                userInfo.setReviews_count("0");
                                                userInfo.setUser_type("Member");
                                                userInfo.setDate_of_birth(date_birthday);
                                                userInfo.setGender(gender);
                                                userInfo.setProfile_picture(image_url.toString());
                                                newUserInfo.set(userInfo);
                                            }
                                        });

                                    }
                                    else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });




                            // Sign in success, update UI with the signed-in info's information
                          // Toast.makeText(getApplicationContext(),"Account Created, Welcome!",Toast.LENGTH_SHORT).show();
                            updateUI(account);
                        } else {
                            String errorMEssage = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(),errorMEssage,Toast.LENGTH_LONG).show();
                        }
                        loading.setVisibility(View.INVISIBLE);
                    }
                });

        // [END create_user_with_email]
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnsignup) {
            createAccount(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());
        }
        else if (i == R.id.add_profile_img){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    //Toast.makeText(SignUpActivity.this,"Permission Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else {
                   // Toast.makeText(SignUpActivity.this,"You already have Permission", Toast.LENGTH_SHORT).show();
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(SignUpActivity.this);
                }
            }
        }

        else if (i == R.id.edtBirthday){
            showDatePickerDialog();
        }

        }


    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                 this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date_birthday = "" + month + "/" + dayOfMonth + "/" + year;
        edtBirthday.setText(date_birthday);


    }

    private boolean validateForm() {
        boolean valid = true;

        String email = edtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Required.");
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        String password = edtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Required.");
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        String firstname = edtFirstname.getText().toString();
        if (TextUtils.isEmpty(firstname)) {
            edtFirstname.setError("Required.");
            valid = false;
        } else {
            edtFirstname.setError(null);
        }

        String lastname = edtLastname.getText().toString();
        if (TextUtils.isEmpty(lastname)) {
            edtLastname.setError("Required.");
            valid = false;
        } else {
            edtLastname.setError(null);
        }

        String city = edtCity.getText().toString();
        if (TextUtils.isEmpty(city)) {
            edtCity.setError("Required.");
            valid = false;
        } else {
            edtCity.setError(null);
        }

        String date = date_birthday;
        if (TextUtils.isEmpty(date)) {
            edtBirthday.setError("Required.");
            valid = false;
        }
        else {
            edtBirthday.setError(null);
        }

        String phone = edtPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Required.");
            valid = false;
        } else if (phone.length() < 10){
            edtPhone.setError("Enter a Valid Phone Number.");
            valid = false;
        }
        else {
            edtPhone.setError(null);
        }

        if (profile_img_uri == null){
            Toast.makeText(SignUpActivity.this,"Please add a profile picture.", Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);

    }


    private void updateUI(FirebaseUser account) {
        // hideProgressDialog();
        if (account != null) {
            //DO THE SIGNIN METHODBY GETTING USERNAME AND PASSWORD FROM THE USER CLASS

        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        // menuInflater.inflate(R.menu.);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                 profile_img_uri = result.getUri();  // get the result and pass it into uri
                 add_profile_img.setImageURI(profile_img_uri); // set the circle image view to the picture uri


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



}
