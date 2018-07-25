package org.afrikcode.hackathon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvSkills, tvInterest, tvAddress, tvContact;
    private CircleImageView imgProfile;
    private ImageView imgEdit;

    // Objects
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;

    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initializing Objects
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        tvName = (TextView) findViewById(R.id.tv_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvContact = (TextView) findViewById(R.id.tv_contact);
        tvInterest = (TextView) findViewById(R.id.tv_interest);
        tvSkills = (TextView) findViewById(R.id.tv_skills);

        imgEdit = (ImageView) findViewById(R.id.img_edit_profile);
        imgProfile = (CircleImageView) findViewById(R.id.img_profile);

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SetupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()){

                        String name = task.getResult().getString("full_name");
                        String image_url = task.getResult().getString("image_url");
                        String skills = task.getResult().getString("skills");
                        String interest = task.getResult().getString("interest");
                        String address = task.getResult().getString("address");
                        String contact = task.getResult().getString("contact");

                        tvAddress.setText(address);
                        tvContact.setText(contact);
                        tvSkills.setText(skills);
                        tvInterest.setText(interest);
                        tvName.setText(name);

                        RequestOptions placeholderRequestOptions = new RequestOptions();
                        placeholderRequestOptions.placeholder(R.drawable.dpp);

                        try {
                            Glide.with(ProfileActivity.this).setDefaultRequestOptions(placeholderRequestOptions).load(image_url).into(imgProfile);
                        }catch (Exception e){

                        }



                    }else {


                    }

                }else {

                    String error = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}