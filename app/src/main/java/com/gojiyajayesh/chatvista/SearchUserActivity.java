package com.gojiyajayesh.chatvista;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gojiyajayesh.chatvista.adapters.UserSearchAdapter;
import com.gojiyajayesh.chatvista.models.UserAvailabilityModel;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {
    private ImageButton backButton, searchButton;
    private EditText searchInput;
    private RecyclerView recyclerView;
    private UserSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        backButton = findViewById(R.id.search_backBtn);
        searchButton = findViewById(R.id.FindUserBtn);
        searchInput = findViewById(R.id.userSearchInput);
        searchInput.requestFocus();
        recyclerView = findViewById(R.id.searchRecyclerView);

        backButton.setOnClickListener(view -> onBackPressed());

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchInputText = searchInput.getText().toString().trim();
                if (searchInputText.length() >= 3) {
                    searchRecyclerView(searchInputText);
                } else {
                    clearSearchResults();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchButton.setOnClickListener(v -> {
            String searchInputText = searchInput.getText().toString().trim();
            searchRecyclerView(searchInputText);
        });
    }

    private void searchRecyclerView(String searchInputText) {
        recyclerView.setAdapter(null);
        Query queryByUsername = FirebaseUtils.allUserCollectionReference().orderBy("username").startAt(searchInputText.toLowerCase()).endAt(searchInputText.toLowerCase() + "\uf8ff");
        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>().setQuery(queryByUsername, Users.class).build();
        adapter = new UserSearchAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void clearSearchResults() {
        if (adapter != null) {
            adapter.stopListening();
            recyclerView.setAdapter(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserStatus(false);
    }

    private void updateUserStatus(boolean connected) {
        UserAvailabilityModel userAvailabilityModel = new UserAvailabilityModel();
        userAvailabilityModel.setIsOnline(connected);
        FirebaseDatabase.getInstance().getReference().child("Availability").child(FirebaseUtils.currentUserId()).setValue(userAvailabilityModel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserStatus(true);
        if (adapter != null) {
            String searchInputText = searchInput.getText().toString().trim();
            searchRecyclerView(searchInputText);
        }
    }
}
