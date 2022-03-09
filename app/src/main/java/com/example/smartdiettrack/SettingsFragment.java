package com.example.smartdiettrack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";
    UserProfile userProfile;
    private CheckBoxPreference cameraPreference;
    private Preference biometricsPreference;
    private Preference logoutPreference;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey){
        setPreferencesFromResource(R.xml.preferences, rootKey);
        Log.d(TAG, "onCreatePreferences");

        cameraPreference = (CheckBoxPreference) findPreference("pref_camera");
        userProfile = (UserProfile) findPreference("pref_profile");
        logoutPreference = findPreference("pref_logout");
        biometricsPreference = findPreference("pref_biometrics");
        registerPreferenceChangeCallbacks();
        registerPermissionCallbacks();

        createRequestLogin();
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
        updatePreferenceStatus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    private void createRequestLogin() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    public void registerPreferenceChangeCallbacks(){
        cameraPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isChecked = ((CheckBoxPreference) preference).isChecked();
                if(isChecked){
                    dialogRedirectToSettings();
                }
                else{
                    Log.d(TAG, "Requesting camera permission");
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                }

                //returning false to indicate that update is handled manually
                return false;
            }
        });

        logoutPreference.setOnPreferenceClickListener(preference -> {
            logoutUser();
            return true;
        });

        biometricsPreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getContext(), BiometricInputActivity.class);
            startActivity(intent);
            return true;
        });
    }

    public void registerPermissionCallbacks(){
        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if(cameraPreference != null){
                if(isGranted){
                    Toast.makeText(getContext(), "Camera permission granted.",
                            Toast.LENGTH_SHORT).show();
                    cameraPreference.setChecked(true);
                }
                else{
                    Toast.makeText(getContext(), "Microphone permission denied.",
                            Toast.LENGTH_SHORT).show();
                    cameraPreference.setChecked(false);
                }
            }
        });
    }

    public void updatePreferenceStatus(){
        cameraPreference = (CheckBoxPreference) findPreference("pref_camera");
        if(cameraPreference != null){
            cameraPreference.setChecked(ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
        }
    }

    public void dialogRedirectToSettings(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Open System Settings")
                .setMessage("Permissions, once granted, need to be revoked from the system settings.")
                .setCancelable(false)
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        redirectToSettings();
                    }
                })
                .setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void redirectToSettings(){
        Log.d(TAG, "redirectToSettings");
        String packageName = requireContext().getPackageName();
        Uri packageURI = Uri.fromParts("package", packageName, null);
        Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);

        try{
            startActivity(settingsIntent);
        } catch (ActivityNotFoundException e){
            Log.d(TAG, "Cannot open system settings!");
        }

    }

    public void logoutUser(){
        FirebaseAuth.getInstance().signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent logoutIntent = new Intent(getContext(), LoginActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
            }
        });
    }
}