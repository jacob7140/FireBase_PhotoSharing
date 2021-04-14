package com.example.picturesharing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MyAccountFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int PICK_IMAGE = 100;
    private final String TAG = "data";
    Photo photo1 = new Photo();

    Uri imageUri;


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

            imageUri = data.getData();

            StorageReference storageRef = storage.getReference();
            String fileName = UUID.randomUUID().toString() + ".jpg";
            storageRef.child(mAuth.getUid()).child(fileName).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: File uploaded");
                        storePhotoInfoToFirestore(fileName);
                        photo1.setImageUri(imageUri);
                        setUpPostListener();

                    } else {
                        Log.d(TAG, "onComplete: File Not uploaded");
                        Toast.makeText(getActivity(), "Error Uploading File", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


    }

    ArrayList<Photo> photoList = new ArrayList<>();
    RecyclerView recyclerView;
    PhotosAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(user.getDisplayName() + "'s Account");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        view.findViewById(R.id.buttonPostNewPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        recyclerView = view.findViewById(R.id.myAccountRecyclerView);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PhotosAdapter();
        recyclerView.setAdapter(adapter);

        setUpPostListener();

        return view;
    }

    private void storePhotoInfoToFirestore(String photoRef){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> data = new HashMap<>();
        data.put("photoRef", photoRef);
        data.put("createdAt", Timestamp.now());
        data.put("photoUri", imageUri.toString());

        db.collection("profiles").document(mAuth.getUid()).collection("photos").document(photoRef)
                .set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "onComplete: Done...");
            }
        });

    }

    private void setUpPostListener(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("profiles").document(user.getUid()).collection("photos").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Photo photo = document.toObject(Photo.class);
                                photoList.add(photo);
                                Log.d(TAG, "onComplete: " + photoList);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder> {

        @NonNull
        @Override
        public PhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.myaccount_row_item, parent, false);
            return new PhotosViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return photoList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull PhotosViewHolder holder, int position) {
            Photo photo = photoList.get(position);
            holder.setupForumRow(photo);
        }

        class PhotosViewHolder extends RecyclerView.ViewHolder{
            TextView textViewDate;
            ImageView imageViewMyAccount, imageViewDelete;
            Photo mPhoto;


            public PhotosViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewDate = itemView.findViewById(R.id.textViewMyAccountDate);
                imageViewDelete = itemView.findViewById(R.id.imageViewDelete);

            }

            public void setupForumRow(Photo photo){
                this.mPhoto = photo;
                FirebaseFirestore db = FirebaseFirestore.getInstance();


                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy h:m a");
                textViewDate.setText(formatter.format(photo.getCreatedAt().toDate()));

                
                try {
                    Picasso.get().load(photo.getPhotoUri()).into(imageViewMyAccount);
                } catch (Exception e){
                    Log.d(TAG, "setupForumRow: Wait");
                }


//                String createdByUid = mComment.getCreatedByUid();
//                String currentUid = mAuth.getCurrentUser().getUid();

//                if (createdByUid.equals(currentUid)){
//                    imageViewDeleteForum.setVisibility(View.VISIBLE);
//                    imageViewDeleteForum.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            db.collection("forums").document(mForum.getForumId()).collection("comment").document(comment.getCommentId()).delete();
//                        }
//                    });
//
//                } else {
//                    imageViewDeleteForum.setVisibility(View.INVISIBLE);
//                }



            }
        }
    }
}