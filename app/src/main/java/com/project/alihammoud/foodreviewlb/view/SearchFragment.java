package com.project.alihammoud.foodreviewlb.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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


public class SearchFragment extends Fragment implements View.OnClickListener {

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
    String searchResult;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


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



        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Search");
        ((HomeActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false); // disable back button
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true); // remove the left caret
        ((HomeActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);  // remove the title



        return view;
    }


    @Override
    public void onClick(View v) {


    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

           /* case R.id.write_review:
                Intent intent = new Intent(getActivity(), ReviewActivity.class);
                startActivity(intent);
                return true;*/

            case R.id.search_button:
                SearchView sv = (SearchView) item.getActionView().findViewById(R.id.search_button);
                sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        searchResult = query;
                        Toast.makeText(getActivity(), query, Toast.LENGTH_LONG).show();

                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        return false;
                    }
                });

                Toast.makeText(getActivity(), "Search", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void setUpRecyclerView(){
        if (searchResult != null){
            Query query = reviews.orderBy("date_posted",Query.Direction.DESCENDING).whereEqualTo("Description", searchResult);
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


    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null)
        {
            if (searchResult != null){
                adapter.startListening();
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (currentUser != null){
            if (searchResult != null){
                adapter.stopListening();
           }

        }

    }
}
