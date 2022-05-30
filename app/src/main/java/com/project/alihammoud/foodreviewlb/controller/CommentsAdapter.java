package com.project.alihammoud.foodreviewlb.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.alihammoud.foodreviewlb.R;
import com.project.alihammoud.foodreviewlb.model.CommentInfo;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter  extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

private Context context;
private FirebaseFirestore db;

    public  List<CommentInfo> comments_list;

    public CommentsAdapter(List<CommentInfo> comments_list){

        this.comments_list = comments_list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_comments,parent,false);
        context = parent.getContext();



        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        db = FirebaseFirestore.getInstance();
        //final String commentID = comments_list.get(position)

        String comment = comments_list.get(position).getComment();
        holder.setComment(comment);

        final String commentUserID = comments_list.get(position).getUser_id();
        DocumentReference userData = db.collection("Users").document(commentUserID);
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

    }

    @Override
    public int getItemCount() {
        return comments_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView comment, first_name, last_name;
        CircleImageView users_profile_picture;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            mView = itemView;



        }

        public void setComment(String CommentText){

            comment = mView.findViewById(R.id.comment);
            comment.setText(CommentText);
        }


        private void setUserData(String firstName, String lastName, String profilePicture){
            first_name = mView.findViewById(R.id.first_name);
            last_name = mView.findViewById(R.id.last_name);
            users_profile_picture = mView.findViewById(R.id.users_profile_picture);

            first_name.setText(firstName);
            last_name.setText(lastName);

            if (profilePicture != null){

                Glide.with(context).load(profilePicture).into(users_profile_picture);
            }


        }

    }
}
