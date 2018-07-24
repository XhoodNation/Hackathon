package org.afrikcode.hackathon;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.afrikcode.hackathon.presenters.AuthPresenter;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SetupActivity extends AppCompatActivity {

    private Uri postImageURI = null;
    private CircleImageView profile_img;
    private EditText etUsername, etContact;
    private Button btnSave;
    private ProgressBar progressBar;

    // Objects
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    private StorageReference storageReference;

    private Bitmap compressedImageFile;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Initializing Objects
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        etContact = (EditText) findViewById(R.id.et_contact);
        etUsername = (EditText) findViewById(R.id.et_username);
        btnSave = (Button) findViewById(R.id.btn_save);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        String currentUserName = mUser.getDisplayName();
        String currentUserEmail = mUser.getEmail();
        String currentUserPhone = mUser.getPhoneNumber();

        etContact.setText(currentUserEmail + "\n" + currentUserPhone);
        etUsername.setText(currentUserName);


        //Initializing Views
        profile_img = (CircleImageView) findViewById(R.id.profile_img);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(SetupActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }else {

                        // start picker to get image for cropping and then use the image in cropping activity
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setMinCropResultSize(512,512)
                                .start(SetupActivity.this);

                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountInfo();
            }
        });

    }

    private void saveAccountInfo() {

        final String username = etUsername.getText().toString().trim();
        if (!username.isEmpty() && postImageURI != null){
            try {
                btnSave.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                final String user_id = mAuth.getCurrentUser().getUid();

                final StorageReference filePath = storageReference.child("profile_images").child(user_id);
                filePath.putFile(postImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                            File newImageFile = new File(postImageURI.getPath());

                            try {
                                compressedImageFile = new Compressor(SetupActivity.this)
                                        .setMaxHeight(100)
                                        .setMaxWidth(100)
                                        .setQuality(5)
                                        .compressToBitmap(newImageFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,baos);
                            byte[] thumbData = baos.toByteArray();

                            final UploadTask uploadTask = storageReference.child("profile_images/thumbs")
                                    .child(user_id + ".jpg").putBytes(thumbData);

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
                                            postMap.put("full_name", username);
                                            postMap.put("timestamp", FieldValue.serverTimestamp());

                                            firebaseFirestore.collection("users").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                                    if (task.isSuccessful()){

                                                        Toast.makeText(SetupActivity.this, "", Toast.LENGTH_SHORT).show();
                                                        Intent homeIntent = new Intent(SetupActivity.this,MainActivity.class);
                                                        startActivity(homeIntent);
                                                        finish();
                                                    }else {

                                                    }

                                                    progressBar.setVisibility(View.INVISIBLE);
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



                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                });
            }catch (Exception e){
                Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageURI = result.getUri();

                profile_img.setImageURI(postImageURI);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                Toast.makeText(this, "Yet to be implemented", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
