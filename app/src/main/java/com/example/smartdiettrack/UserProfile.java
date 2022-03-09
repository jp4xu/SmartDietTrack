package com.example.smartdiettrack;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import android.os.Bundle;

public class UserProfile extends Preference {

    public UserProfile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder){
        super.onBindViewHolder(holder);
        TextView userName = (TextView) holder.findViewById(R.id.user_name);
        ImageView userPhoto = (ImageView) holder.findViewById(R.id.user_photo);
        TextView userEmail = (TextView) holder.findViewById(R.id.user_email);

        setUserProfile(userName, userEmail, userPhoto);
    }

    public void setUserProfile(TextView userName, TextView userEmail, ImageView userPhoto){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            userName.setText(name);
            Picasso.get().load(photoUrl).resize(250, 250).transform(new CropCircleTransformation()).into(userPhoto);
            userEmail.setText(email);
        }
    }
}