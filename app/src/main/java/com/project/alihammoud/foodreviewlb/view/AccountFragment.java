package com.project.alihammoud.foodreviewlb.view;


import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.alihammoud.foodreviewlb.MainActivity;
import com.project.alihammoud.foodreviewlb.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment implements View.OnClickListener {

    TextView setFirstname, setLastname, setEmail, setPhone, verify, membership, setReviewsCount;
    ImageButton verifyBtn;
    String userID;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    CircleImageView setprofilepicture;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        setFirstname = view.findViewById(R.id.setFirstname);
        setLastname = view.findViewById(R.id.setLastname);
        setReviewsCount = view.findViewById(R.id.reviews_count);
        setprofilepicture = view.findViewById(R.id.profile_pic);


        view.findViewById(R.id.lougout_card).setOnClickListener(this);
        view.findViewById(R.id.personal_info).setOnClickListener(this);
        view.findViewById(R.id.my_reviews).setOnClickListener(this);

        if (currentUser!= null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference userData = db.collection("Users").document(userID);
            userData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        if (task.getResult().exists()){
                            String ID = task.getResult().getId();
                            String firstname = task.getResult().getString("first_name");
                            String lastname = task.getResult().getString("last_name");
                            String email = task.getResult().getString("email");
                            String phone = task.getResult().getString("phone_number");
                            String reviewscount = task.getResult().getString("reviews_count");
                            String formattedNumber = PhoneNumberUtils.formatNumber(phone);
                            String profilepicture = task.getResult().getString("profile_picture");

                            RequestOptions placeholderRequest = new RequestOptions();
                            placeholderRequest.placeholder(R.drawable.circle_profile);
                            Glide.with(getContext()).setDefaultRequestOptions(placeholderRequest).load(profilepicture).into(setprofilepicture);

                            setFirstname.setText(firstname);
                            setLastname.setText(lastname);
                            setReviewsCount.setText(reviewscount);
                        }
                    }
                    else {
                        String error = task.getException().getMessage();
                        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                    }
                }
            });



        }

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
    int i = v.getId();
     if (i == R.id.lougout_card){
        signOut();
    }
     else if (i == R.id.personal_info){
         Intent intent = new Intent(getActivity(), PersonalInformationActivity.class);
         startActivity(intent);
     }
     else if (i == R.id.my_reviews){
         Intent intent = new Intent(getActivity(), MyReviewsActivity.class);
         startActivity(intent);
     }

    }

    @Override
    public void onStart() {
        super.onStart();



    }

    private void signOut() {

        auth.signOut();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);



    }
   /* public void editInfo(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String value = setFirst_name.getText().toString();

        Map<String, Object> info = new HashMap<>();
        info.put("Value Key", value);
        db.collection("Users").document(userID).set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }*/
}
