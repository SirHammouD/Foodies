package com.project.alihammoud.foodreviewlb.view;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.project.alihammoud.foodreviewlb.R;
import com.project.alihammoud.foodreviewlb.controller.ReviewsAdapter;
import com.project.alihammoud.foodreviewlb.model.ReviewInfo;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class DashboardFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference reviews;
    private ReviewsAdapter adapter;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseUser currentUser;
    RecyclerView recyclerView;
    DocumentSnapshot lastVisible;
    private String currentUserID;
    private TextView title;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        auth = FirebaseAuth.getInstance();
        reviews = db.collection("Reviews");
        currentUser = auth.getCurrentUser();


        if (currentUser != null){
            currentUserID = auth.getCurrentUser().getUid();

            setUpRecyclerView();
            recyclerView = view.findViewById(R.id.recycle_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);


            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    adapter.refresh();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }



    /* adapter.setOnItemClickListener(new ReviewsAdapter.OnItemClickListener() {
     @Override
        public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
         String document_id = documentSnapshot.getId();
         Toast.makeText(getContext(),document_id,Toast.LENGTH_SHORT).show();
         }
        });*/

      title = (TextView) view.findViewById(R.id.title);
      title.setText(" Food Review LB");

        toolbar = view.findViewById(R.id.toolbar);
       // toolbar.setTitle(" Food Review LB");
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);
       // setHasOptionsMenu(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // disable back button
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true); // remove the left caret
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);  // remove the title

        return view;
    }

    private void setUpRecyclerView(){

        Query query = reviews.orderBy("date_posted",Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .setPrefetchDistance(2)
                .setPageSize(10)
                .build();

        FirestorePagingOptions<ReviewInfo> options = new FirestorePagingOptions
                .Builder<ReviewInfo>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ReviewInfo.class)
                .build();
        adapter = new ReviewsAdapter(options);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null)
        {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    if (currentUser != null){

    adapter.stopListening();

        }

    }

    /*public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.write_review:
                Intent intent = new Intent(getActivity(), ReviewActivity.class);
                startActivity(intent);
                return true;

            case R.id.search_button:
                Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/



}




