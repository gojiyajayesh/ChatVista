package com.gojiyajayesh.chatvista.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gojiyajayesh.chatvista.R;
import com.gojiyajayesh.chatvista.adapters.StatusListAdapter;
import com.gojiyajayesh.chatvista.models.Users;
import com.gojiyajayesh.chatvista.utils.AndroidUtils;
import com.gojiyajayesh.chatvista.utils.FirebaseUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StatusScreenFragment extends Fragment {
    RecyclerView statusRecyclerView;
    ArrayList<Users> list;
    FirebaseDatabase database;
    StatusListAdapter adapter;
    FirebaseStorage storage;
    String StatusUrl;
    ProgressDialog mProgressDialog;

    public StatusScreenFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_list, container, false);
        statusRecyclerView = view.findViewById(R.id.StatusListRecyclerView);
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add);
        storage = FirebaseStorage.getInstance();
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 63);
        });
        database = FirebaseDatabase.getInstance();
        list = new ArrayList<>();
        adapter = new StatusListAdapter(list, getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        statusRecyclerView.setLayoutManager(layoutManager);
        statusRecyclerView.setAdapter(adapter);
        imageLoadMethod();
        return view;
    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        imageLoadMethod();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        imageLoadMethod();
//    }

    public void imageLoadMethod() {
        String currentUserId = FirebaseUtils.currentUserId();
        if (currentUserId != null) {
            database.getReference().child("UserConnections").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userId = dataSnapshot.child("userId").getValue(String.class);
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String fullName = dataSnapshot.child("fullName").getValue(String.class);
                        String profileId = dataSnapshot.child("profileId").getValue(String.class);
                        String StatusUrl = dataSnapshot.child("StatusUrl").getValue(String.class);
                        Long lastUpdate = dataSnapshot.child("lastStatusUpdateTime").getValue(Long.class);
                        Users users = new Users();
                        users.setUserId(userId);
                        users.setUsername(username);
                        users.setFullName(fullName);
                        users.setProfileId(profileId);
                        users.setLastStatusUpdateTime(lastUpdate);
                        users.setStatusUrl(StatusUrl);
                        if (users.getUserId().equals(currentUserId)) {
                            users.setUsername(username + " (Me)");
                        }
                        list.add(users);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getData() != null) {
            inProgress(true);
            Uri photoPath = data.getData();
            Bitmap bitmap = compressImage(photoPath);
            uploadImageToFirebase(bitmap);
        }
    }

    // Method to compress the image
    private Bitmap compressImage(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);
        return bitmap;
    }

    private void uploadImageToFirebase(Bitmap bitmap) {
        File compressedFile = saveBitmapToFile(bitmap);
        final StorageReference reference = storage.getReference().child("UserStatusPhoto's").child(FirebaseUtils.currentUserId());
        reference.putFile(Uri.fromFile(compressedFile)).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri -> {
            String statusUrl = uri.toString();
            inProgress(false);
            HashMap<String, Object> updateMap = new HashMap<>();
            updateMap.put("StatusUrl", statusUrl);
            updateMap.put("lastStatusUpdateTime", new Date().getTime());
            FirebaseDatabase.getInstance().getReference().child("Users").
                    child(FirebaseUtils.currentUserId()).updateChildren(updateMap).
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    AndroidUtils.customToast(getContext(), "Status Updated", 1);
                }
            }).addOnFailureListener(e -> {
            });
            DatabaseReference userConnectionsRef = FirebaseDatabase.getInstance().getReference().child("UserConnections").child(FirebaseUtils.currentUserId());
            userConnectionsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        Users user = childSnapshot.getValue(Users.class);
                        if (user != null) {
                            String UserId = user.getUserId();
                            FirebaseDatabase.getInstance().getReference().
                                    child("UserConnections").child(UserId)
                                    .child(FirebaseUtils.currentUserId()).updateChildren(updateMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            AndroidUtils.customToast(getContext(), "Status Updated2", 1);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            AndroidUtils.customToast(getContext(), "Fail2", 1);
                                        }
                                    });
                        }
                    }
                    inProgress(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });

        }));
    }

    private void inProgress(boolean isProgress) {
        if (isProgress) {
            mProgressDialog = new ProgressDialog(requireContext());
            mProgressDialog.setTitle("Please Wait...");
            mProgressDialog.setMessage("Status uploading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    // Method to save the compressed Bitmap to a file
    private File saveBitmapToFile(Bitmap bitmap) {
        File filesDir = requireContext().getFilesDir();
        File imageFile = new File(filesDir, "compressed_image.jpg");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }
}
