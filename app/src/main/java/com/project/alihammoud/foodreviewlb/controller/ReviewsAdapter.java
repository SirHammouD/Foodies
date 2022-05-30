package com.project.alihammoud.foodreviewlb.controller;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.alihammoud.foodreviewlb.view.BottomSheetFragmentComment;
import com.project.alihammoud.foodreviewlb.R;
import com.project.alihammoud.foodreviewlb.model.ReviewInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;


public class ReviewsAdapter extends FirestorePagingAdapter<ReviewInfo, ReviewsAdapter.ReviewsHolder> {
    private String currentUserID;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private Context context;
    private BottomSheetFragmentComment bottomSheetFragmentComment;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public ReviewsAdapter(@NonNull FirestorePagingOptions<ReviewInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ReviewsHolder holder, final int position, @NonNull final ReviewInfo model) {

        final String document_id = getItem(position).getId();

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        currentUserID = currentUser.getUid();

        db = FirebaseFirestore.getInstance();

        if (currentUser != null){
            holder.checkLikes(document_id);
            holder.checkBookmarks(document_id);
            //holder.checkDislikes(document_id);
        }


        holder.mitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Toast.makeText(holder.itemView.getContext(),document_id,Toast.LENGTH_SHORT).show();
            }
        });

        holder.desc.setText(model.getDescription());
        holder.rating_bar.setRating(model.getRating());
        holder.setReviewImage(model.getReview_image());

        final String reviewUserID = model.getUser_id();
        DocumentReference userData = db.collection("Users").document(reviewUserID);
        userData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {

                        String firstname = task.getResult().getString("first_name");
                        String lastname = task.getResult().getString("last_name");
                        String userPicture = task.getResult().getString("profile_picture");

                        holder.setUserData(firstname,lastname,userPicture);

                    }
                }
            }
        });


        long millisecond = model.getDate_posted().getTime();
        String dateString = DateFormat.format("dd/MM/yyyy",new Date(millisecond)).toString();
        holder.dated_posted.setText(dateString);


        if (currentUser != null){

            db.collection("Reviews").document(document_id).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e == null){
                        if (!documentSnapshot.isEmpty()){

                            int count = documentSnapshot.size();
                            holder.updateLikesCount(count);
                        }
                        else {
                            holder.updateLikesCount(0);
                        }
                    }

                }
            });

            db.collection("Reviews").document(document_id).collection("Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e == null){
                        if (!documentSnapshot.isEmpty()){
                            int count = documentSnapshot.size();
                            holder.updateCommentsCount(count);
                        }
                        else {
                            holder.updateCommentsCount(0);
                        }

                    }

                }
            });


        }


        holder.like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"liked",Toast.LENGTH_SHORT).show();

                db.collection("Reviews").document(document_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()) {

                            db.collection("Reviews").document(document_id).collection("Likes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (!task.getResult().exists()) {
                                        Map<String, Object> likesMap = new HashMap<>();
                                        likesMap.put("timestamp", FieldValue.serverTimestamp());
                                        db.collection("Reviews").document(document_id).collection("Likes").document(currentUserID).set(likesMap);
                                        holder.like_button.setImageResource(R.drawable.ic_thumb_up_black_liked_24dp);
                                    } else {
                                        db.collection("Reviews").document(document_id).collection("Likes").document(currentUserID).delete();
                                        holder.like_button.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                                    }

                                }
                            });
                        }
                        else {

                            Toast.makeText(context, "This review has been removed, please refresh your feed.", Toast.LENGTH_LONG).show();
                        }
                    }


                });



            }
        });

        holder.bookmark_botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Reviews").document(document_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            db.collection("Users").document(currentUserID).collection("Bookmarks").document(document_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (!task.getResult().exists()){
                                        Map<String, Object> bookmarkMap = new HashMap<>();
                                        bookmarkMap.put("timestamp", FieldValue.serverTimestamp());
                                        db.collection("Users").document(currentUserID).collection("Bookmarks").document(document_id).set(bookmarkMap);
                                        holder.bookmark_botton.setImageResource(R.drawable.ic_bookmark_black_24dp);
                                    }
                                    else {
                                        db.collection("Users").document(currentUserID).collection("Bookmarks").document(document_id).delete();
                                        holder. bookmark_botton.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                                    }

                                }
                            });
                        }
                        else {
                            Toast.makeText(context, "This review has been removed, please refresh your feed.", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });


        /*holder.dislike_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"disliked",Toast.LENGTH_SHORT).show();

                db.collection("Reviews").document(document_id).collection("Dislikes").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            db.collection("Reviews").document(document_id).collection("Dislikes").document(currentUserID).set(likesMap);
                            holder.dislike_button.setImageResource(R.drawable.ic_thumb_down_black_dislike_24dp);
                        }
                        else {
                            db.collection("Reviews").document(document_id).collection("Dislikes").document(currentUserID).delete();
                            holder.dislike_button.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                        }

                    }
                });

            }
        });*/


        holder.comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetFragmentComment = new BottomSheetFragmentComment();

                Bundle bundle = new Bundle();
                bundle.putString("docID", document_id );
                bottomSheetFragmentComment.setArguments(bundle);

                bottomSheetFragmentComment.show( ((FragmentActivity) context).getSupportFragmentManager(),bottomSheetFragmentComment.getTag());

            }
        });





    }


    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state){
            case LOADED:
                Log.d("PAGING_LOG", "TOTAL ITEMS LOADED: " + getItemCount());
                break;
            case ERROR:
                Log.d("PAGING_LOG", "ERROR");
                break;
            case FINISHED:
                Log.d("PAGING_LOG", "ALL DATA IS LOADED");
                break;
            case LOADING_MORE:
                Log.d("PAGING_LOG", "LOADING MORE");
                break;
            case LOADING_INITIAL:
                Log.d("PAGING_LOG", "FIRST TIME LOAD");
                break;

        }
    }

    @NonNull
    @Override
    public ReviewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_dashboard,parent,false);
        context = parent.getContext();


        return new ReviewsHolder(v);
    }

    class ReviewsHolder extends RecyclerView.ViewHolder{
        TextView desc, first_name, last_name, dated_posted, like_count, comment_count;
        ImageButton like_button, dislike_button, comment_button, bookmark_botton;
        RatingBar rating_bar;
        ImageView card_image;
        CircleImageView users_profile_picture;
        View mitemView;



        public ReviewsHolder(@NonNull final View itemView) {
            super(itemView);
            mitemView = itemView;

            like_button = itemView.findViewById(R.id.like_button);
           // dislike_button = itemView.findViewById(R.id.dislike_button);
            comment_button = itemView.findViewById(R.id.comment_button);
            desc = itemView.findViewById(R.id.desc);
            rating_bar = itemView.findViewById(R.id.rating_bar);
            dated_posted = mitemView.findViewById(R.id.date_posted);
            bookmark_botton =  mitemView.findViewById(R.id.bookmark_botton);




            // itemView.setOnClickListener(new View.OnClickListener() {
             //   @Override
             //   public void onClick(View v) {
                //    int position = getAdapterPosition();
                //    if (position != RecyclerView.NO_POSITION ){

                //       Toast.makeText(itemView.getContext(),users_id,Toast.LENGTH_SHORT).show();
                //    }
                   /* if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }*/

             //   }
         //   });

        }

        private void  setReviewImage(String downloadUri){
            card_image = mitemView.findViewById(R.id.card_image);

            if (downloadUri != null){
                Glide.with(context).load(downloadUri).into(card_image);
            }

        }


        private void setUserData(String firstName, String lastName, String profilePicture){
            first_name = itemView.findViewById(R.id.first_name);
            last_name = itemView.findViewById(R.id.last_name);
            users_profile_picture = mitemView.findViewById(R.id.users_profile_picture);

            first_name.setText(firstName);
            last_name.setText(lastName);

            if (profilePicture != null){

                Glide.with(context).load(profilePicture).into(users_profile_picture);
            }


        }


        private void checkLikes(String documentID){
            db.collection("Reviews").document(documentID).collection("Likes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e == null){
                        if (documentSnapshot.exists()){
                            like_button.setImageResource(R.drawable.ic_thumb_up_black_liked_24dp);
                        }
                        else {
                            like_button.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        }
                    }

                }
            });

        }

        private void checkBookmarks(String documentID){
            db.collection("Users").document(currentUserID).collection("Bookmarks").document(documentID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e == null){
                        if (documentSnapshot.exists()){
                            bookmark_botton.setImageResource(R.drawable.ic_bookmark_black_24dp);
                        }
                        else {
                            bookmark_botton.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                        }
                    }
                }
            });
        }


        private void updateLikesCount(int count){
            like_count = mitemView.findViewById(R.id.like_count);
            like_count.setText(count+"");

        }

        private void updateCommentsCount(int count){
            comment_count = mitemView.findViewById(R.id.comment_count);
            comment_count.setText(count+"");

        }

      /*  private void checkDislikes(String documentID){
            db.collection("Reviews").document(documentID).collection("Dislikes").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e == null){
                        if (documentSnapshot.exists()){
                            dislike_button.setImageResource(R.drawable.ic_thumb_down_black_dislike_24dp);
                        }
                        else {
                            dislike_button.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                        }
                    }

                }
            });


        }*/



    }



}
