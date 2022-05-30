package com.project.alihammoud.foodreviewlb.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.alihammoud.foodreviewlb.CommentID;
import com.project.alihammoud.foodreviewlb.R;
import com.project.alihammoud.foodreviewlb.controller.CommentsAdapter;
import com.project.alihammoud.foodreviewlb.model.CommentInfo;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetFragmentComment extends BottomSheetDialogFragment {
    private Toolbar toolbar;
    private String document_id;

    private String currentUserID;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<CommentInfo> comments_list;
    private CommentsAdapter commentsAdapter;
    private ImageButton comment_button;
    private EditText comment_text;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private CommentID commentID;

    public BottomSheetFragmentComment(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_comments,container,false);

        document_id = this.getArguments().getString("docID");


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        currentUserID = currentUser.getUid();
        db = FirebaseFirestore.getInstance();

        comments_list = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(comments_list);

        Query firstQuery =  db.collection("Reviews").document(document_id).collection("Comments")
                .orderBy("date_posted", Query.Direction.DESCENDING)
                .limit(20);

        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null){
                    if (!documentSnapshot.isEmpty()){

                        if (isFirstPageFirstLoad){
                            lastVisible = documentSnapshot.getDocuments().get(documentSnapshot.size()-1);
                        }

                        for (DocumentChange documentChange: documentSnapshot.getDocumentChanges()){

                            if (documentChange.getType() == DocumentChange.Type.ADDED){

                                String commentID = documentChange.getDocument().getId();

                                CommentInfo commentAdded = documentChange.getDocument().toObject(CommentInfo.class);

                                if (isFirstPageFirstLoad){
                                    comments_list.add(commentAdded);
                                }
                                else {
                                    comments_list.add(0,commentAdded);
                                }


                                commentsAdapter.notifyDataSetChanged();

                            }

                            else if (documentChange.getType() == DocumentChange.Type.REMOVED){

                                CommentInfo commentRemoved = documentChange.getDocument().toObject(CommentInfo.class);
                                comments_list.remove(commentRemoved);
                                commentsAdapter.notifyItemRemoved(documentChange.getOldIndex());
                                commentsAdapter.notifyDataSetChanged();

                            }

                            else if (documentChange.getType() == DocumentChange.Type.MODIFIED){

                                CommentInfo commentModified = documentChange.getDocument().toObject(CommentInfo.class);
                                if (documentChange.getOldIndex() == documentChange.getNewIndex()) {
                                    // Item changed but remained in same position
                                    comments_list.set(documentChange.getOldIndex(),commentModified);
                                    commentsAdapter.notifyItemChanged(documentChange.getOldIndex());
                                }else {
                                    // Item changed and changed position
                                    comments_list.remove(documentChange.getOldIndex());
                                    comments_list.add(documentChange.getNewIndex(),commentModified);
                                    commentsAdapter.notifyItemMoved(documentChange.getOldIndex(),documentChange.getNewIndex());
                                }

                                commentsAdapter.notifyDataSetChanged();

                            }
                        }

                        isFirstPageFirstLoad = false;
                    }


                }

            }
        });



       // db.collection("Reviews").document(document_id).collection("Comments")

        if (currentUser != null){

            //setUpRecyclerView();
            recyclerView = view.findViewById(R.id.recycle_view);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
           // mLayoutManager.setReverseLayout(true);
           // mLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(commentsAdapter);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachedBottom = !recyclerView.canScrollVertically(5);

                    if (reachedBottom){

                        String desc = lastVisible.getString("comment");
                        Toast.makeText(getContext(),"Reached: " + desc ,Toast.LENGTH_SHORT).show();
                        loadMore();

                       /* final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                            }
                        },5000);*/
                    }
                }
            });


        }


        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Comments");
        toolbar.setTitleTextColor(Color.BLACK);

        comment_text = view.findViewById(R.id.comment_text);

        comment_button = view.findViewById(R.id.comment_button);
        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Reviews").document(document_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            DocumentReference newComment = db.collection("Reviews").document(document_id).collection("Comments").document();
                            CommentInfo commentInfo = new CommentInfo();
                            commentInfo.setUser_id(currentUserID);
                            commentInfo.setComment(comment_text.getText().toString().trim());
                            newComment.set(commentInfo);
                            comment_text.getText().clear();
                        }
                        else {
                            Toast.makeText(getContext(), "This review has been removed, please refresh your feed.", Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    public  void loadMore(){
        Query nextQuery =  db.collection("Reviews").document(document_id).collection("Comments")
                .orderBy("date_posted", Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(10);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e == null){
                    if (!documentSnapshot.isEmpty()){

                        lastVisible = documentSnapshot.getDocuments().get(documentSnapshot.size()-1);

                        for (DocumentChange documentChange: documentSnapshot.getDocumentChanges()){

                            if (documentChange.getType() == DocumentChange.Type.ADDED){

                                String commentID = documentChange.getDocument().getId();

                                CommentInfo commentAdded = documentChange.getDocument().toObject(CommentInfo.class);
                                comments_list.add(commentAdded);
                                commentsAdapter.notifyDataSetChanged();
                            }

                            else if (documentChange.getType() == DocumentChange.Type.REMOVED){

                                CommentInfo commentRemoved = documentChange.getDocument().toObject(CommentInfo.class);
                                comments_list.remove(commentRemoved);
                                commentsAdapter.notifyItemRemoved(documentChange.getOldIndex());
                                commentsAdapter.notifyDataSetChanged();
                            }

                            else if (documentChange.getType() == DocumentChange.Type.MODIFIED){

                                // modifying

                                CommentInfo commentModified = documentChange.getDocument().toObject(CommentInfo.class);
                                if (documentChange.getOldIndex() == documentChange.getNewIndex()) {
                                    // Item changed but remained in same position
                                    comments_list.set(documentChange.getOldIndex(),commentModified);
                                    commentsAdapter.notifyItemChanged(documentChange.getOldIndex());
                                }else {
                                    // Item changed and changed position
                                    comments_list.remove(documentChange.getOldIndex());
                                    comments_list.add(documentChange.getNewIndex(),commentModified);
                                    commentsAdapter.notifyItemMoved(documentChange.getOldIndex(),documentChange.getNewIndex());
                                }

                                commentsAdapter.notifyDataSetChanged();

                            }
                        }
                    }

                }

            }
        });
    }
    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return  dialog;
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

}
