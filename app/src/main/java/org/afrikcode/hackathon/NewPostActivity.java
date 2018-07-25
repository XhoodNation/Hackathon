package org.afrikcode.hackathon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {

    private ImageView newPostImage, imagePicker, postButton;
    private EditText newPostDesc;

    private Uri postImageURI = null;

    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;

    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private static final int MAX_LENGTH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //Init Firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostDesc = (EditText) findViewById(R.id.new_post_desc);
        newPostImage = (ImageView) findViewById(R.id.new_post_image);
        imagePicker = (ImageView) findViewById(R.id.pickImage);
        postButton = (ImageView) findViewById(R.id.post_btn);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = newPostDesc.getText().toString();

                if ((TextUtils.isEmpty(desc) && postImageURI != null) || (!TextUtils.isEmpty(desc) && postImageURI != null)) {
                    try {

                        final String randomName = random();

                        final StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");
                        filePath.putFile(postImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    File newImageFile = new File(postImageURI.getPath());

                                    try {
                                        compressedImageFile = new Compressor(NewPostActivity.this)
                                                .setMaxHeight(100)
                                                .setMaxWidth(100)
                                                .setQuality(5)
                                                .compressToBitmap(newImageFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(NewPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbData = baos.toByteArray();

                                    final UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                            .child(randomName + ".jpg").putBytes(thumbData);

                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            final String downloadthumbUri = taskSnapshot.getStorage().getDownloadUrl().toString();

                                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    //Set Valu for new post

                                                    Map<String, Object> postMap = new HashMap<>();
                                                    postMap.put("image_url", uri.toString());
                                                    postMap.put("image_thumb", downloadthumbUri);
                                                    postMap.put("desc", desc);
                                                    postMap.put("timestamp", FieldValue.serverTimestamp());
                                                    postMap.put("user_id", current_user_id);

                                                    firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                                            if (task.isSuccessful()) {

                                                                Toast.makeText(NewPostActivity.this, "New Post Added Succesfully", Toast.LENGTH_SHORT).show();
                                                                finish();
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(NewPostActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //Error Handling
                                        }
                                    });


                                } else {

                                }

                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(NewPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if ((!TextUtils.isEmpty(desc) && postImageURI == null)) {

                    try {


                        Map<String, Object> postMap = new HashMap<>();
                        postMap.put("desc", desc);
                        postMap.put("timestamp", FieldValue.serverTimestamp());
                        postMap.put("user_id", current_user_id);

                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(NewPostActivity.this, "New Post Added Succesfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {

                                }

                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(NewPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(NewPostActivity.this, "My friend", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .start(NewPostActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageURI = result.getUri();

                newPostImage.setImageURI(postImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

    //Generating a random String
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
