package com.example.picturesharing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MyAccountFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PICK_IMAGE = 100;
    private final String TAG = "data";


    public MyAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && data != null && resultCode == Activity.RESULT_OK){
            Log.d(TAG, "onActivityResult: We are back...");

            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);

            StorageReference storageRef = storage.getReference();
            String fileName = UUID.randomUUID().toString() + ".jpg";
            storageRef.child(mAuth.getUid()).child(fileName).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: File uploaded");
                        storePhotoInfoToFirestore(fileName);
//                        setUpPostListener(fileName);

                    } else {
                        Log.d(TAG, "onComplete: File Not uploaded");
                        Toast.makeText(getActivity(), "Error Uploading File", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


    }

    ImageView imageView;
    ArrayList<Photo> photoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(user.getDisplayName() + "'s Account");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        imageView = view.findViewById(R.id.imageView);

        view.findViewById(R.id.buttonPostNewPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });


        return view;
    }

    private void storePhotoInfoToFirestore(String photoRef){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> data = new HashMap<>();
        data.put("photoRef", photoRef);
        data.put("createdAt", Timestamp.now());

        db.collection("profiles").document(mAuth.getUid()).collection("photos").document(photoRef)
                .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: Done...");
            }
        });

    }

//    private void setUpPostListener(String photoRef){
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("profiles").document(user.getUid()).collection("photos").orderBy("createdAt", Query.Direction.DESCENDING)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
//                        if (error == null){
//                            photoList.clear();
//                            for (QueryDocumentSnapshot document : querySnapshot){
//                                Photo photo = document.toObject(Photo.class);
//                                photo.setPhotoRef(photoRef);
//                                photoList.add(photo);
//                            }
//
//                        } else{
//                            error.printStackTrace();
//                        }
//                    }
//                });
//    }
}