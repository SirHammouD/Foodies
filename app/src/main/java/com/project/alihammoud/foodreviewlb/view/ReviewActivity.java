package com.project.alihammoud.foodreviewlb.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.alihammoud.foodreviewlb.R;
import com.project.alihammoud.foodreviewlb.model.ReviewInfo;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ReviewActivity extends AppCompatActivity {

    private EditText edtDesc, edtRestaurantName;
    private Button post_review;
    private RatingBar rating_bar;
    private ImageButton review_img_button;
    private StorageReference storageReference;
    private Uri image_url;
    private FirebaseFirestore db;
    private String userID;
    private Uri review_img_uri = null;
    private FirebaseAuth auth;
    private ProgressBar loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        loading = (ProgressBar) findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Write a review");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);


        edtDesc = findViewById(R.id.edtDesc);
        edtRestaurantName = findViewById(R.id.edtRestaurantName);
        rating_bar = findViewById(R.id.rating_bar);
        review_img_button = findViewById(R.id.review_img);

        post_review = findViewById(R.id.post);
        post_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReview();
            }
        });

        review_img_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(ReviewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        //Toast.makeText(SignUpActivity.this,"Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(ReviewActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else {
                        // Toast.makeText(SignUpActivity.this,"You already have Permission", Toast.LENGTH_SHORT).show();
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setMinCropResultSize(512,512)
                                .setAspectRatio(1,1)
                                .start(ReviewActivity.this);
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
       // menuInflater.inflate(R.menu.);
        return super.onCreateOptionsMenu(menu);
    }

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.: // id of the icon in menu
                saveReview(); // method to invoke
                return  true;
                default:
                    return super.onOptionsItemSelected(item);

        }

    }*/

    private void postReview(){

        if (!validateForm()) {
            return;
        }

        loading.setVisibility(View.VISIBLE);

        if(review_img_uri == null){
                                DocumentReference newReviewPost = db.collection("Reviews").document();
                                ReviewInfo reviewInfo = new ReviewInfo();
                                reviewInfo.setUser_id(userID);
                                reviewInfo.setRestaurant_name(edtRestaurantName.getText().toString().trim());
                                reviewInfo.setDescription(edtDesc.getText().toString().trim());
                                reviewInfo.setRating(rating_bar.getRating());
                                newReviewPost.set(reviewInfo); // add on complete listener t handle errors
            loading.setVisibility(View.INVISIBLE);
            finish();
        }
        else {
            Date today = new Date();
            SimpleDateFormat format = new SimpleDateFormat("-dd-MM-yyyy-hh:mm:ss");
            String dateToStr = format.format(today);


           // String time = FieldValue.serverTimestamp().toString();

            final UUID UUID_random = UUID.randomUUID();
            Log.d("version", "version: "+UUID_random.version());

            String imageName = UUID_random+dateToStr;

            final StorageReference image_path = storageReference.child("Reviews Images").child(userID).child( imageName+".jpg");
            image_path.putFile(review_img_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        image_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                image_url = uri;
                                DocumentReference newReviewPost = db.collection("Reviews").document();
                                ReviewInfo reviewInfo = new ReviewInfo();
                                reviewInfo.setUser_id(userID);
                                reviewInfo.setRestaurant_name(edtRestaurantName.getText().toString().trim());
                                reviewInfo.setDescription(edtDesc.getText().toString().trim());
                                reviewInfo.setRating(rating_bar.getRating());
                                reviewInfo.setReview_image(image_url.toString());
                                newReviewPost.set(reviewInfo);
                            }
                        });
                    }
                    else {
                        String error = task.getException().getMessage();
                        Toast.makeText(ReviewActivity.this, error, Toast.LENGTH_LONG).show();
                    }
                }
            });
            loading.setVisibility(View.INVISIBLE);
            finish();
        }


        //newReviewPost.add(new ReviewInfo(userID,edtRestaurantName.getText().toString().trim(),edtDesc.getText().toString().trim(),"","city") );


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                review_img_uri = result.getUri();  // get the result and pass it into uri
                review_img_button.setImageURI(review_img_uri); // set the circle image view to the picture uri


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String desc = edtDesc.getText().toString();
        if (TextUtils.isEmpty(desc)) {
            edtDesc.setError("Required.");
            valid = false;
        } else {
            edtDesc.setError(null);
        }

        String restaurant = edtRestaurantName.getText().toString();
        if (TextUtils.isEmpty(restaurant)) {
            edtRestaurantName.setError("Required.");
            valid = false;
        } else {
            edtRestaurantName.setError(null);
        }

        if (review_img_uri == null){
            Toast.makeText(ReviewActivity.this,"Please add a photo.", Toast.LENGTH_LONG).show();
            valid = false;
        }


        return valid;
    }


    /*Date now = new Date();
    long timestamp = now.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    String dateStr = sdf.format(timestamp);*/
}
