package com.project.alihammoud.foodreviewlb.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.alihammoud.foodreviewlb.R;
import com.project.alihammoud.foodreviewlb.controller.ReviewsAdapter;
import com.project.alihammoud.foodreviewlb.model.ReviewInfo;

public class MyReviewsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reviews = db.collection("Reviews");
    private ReviewsAdapter adapter;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseUser currentUser;
    RecyclerView recyclerView;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reviews);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        currentUserId = currentUser.getUid();

        if (currentUser != null){

            setUpRecyclerView();
            recyclerView = findViewById(R.id.recycle_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(MyReviewsActivity.this));
            recyclerView.setAdapter(adapter);

            swipeRefreshLayout = findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                   adapter.refresh();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }



        /*adapter.setOnItemClickListener(new ReviewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String document_id = documentSnapshot.getId();
                Toast.makeText(MyReviewsActivity.this,document_id,Toast.LENGTH_SHORT).show();
            }
        });*/


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Reviews");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

    }

    private void setUpRecyclerView(){
        Query query = reviews.orderBy("date_posted",Query.Direction.DESCENDING).whereEqualTo("user_id",currentUserId);
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setEnablePlaceholders(false)
                .setPageSize(5)
                .build();
        FirestorePagingOptions<ReviewInfo> options = new FirestorePagingOptions
                .Builder<ReviewInfo>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ReviewInfo.class)
                .build();
        adapter = new ReviewsAdapter(options);
    }



}
