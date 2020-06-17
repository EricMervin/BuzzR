package com.example.buzzr;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.example.buzzr.HelperClasses.sharedPrefs;
import com.example.buzzr.HelperClasses.userHelperClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileScreen extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;

    TextView usrNameTV, usrUsernameTV;
    CircleImageView usrPhoto;

    String localName, localUsername, localPhoneNo, profilePath, deviceName;
    Button deviceBtn;

    BluetoothAdapter bluetoothAdapter;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_screen);

        //Get user information from user helper class
        //Note : Data is being retrieved from local storage
        userHelperClass userData = new userHelperClass(getApplicationContext());
        localName = userData.getName();
        localUsername = userData.getUsername();
        localPhoneNo = userData.getPhoneNo();
        profilePath = userData.getProfilePhoto();
        deviceName = userData.getDeviceName();

        //Profile Photo
        loadImageFromStorage(profilePath);

        //Hooks
        usrNameTV = findViewById(R.id.profileUserName);
        usrUsernameTV = findViewById(R.id.profileUserUsername);
        usrPhoto = findViewById(R.id.profilePhoto);
        deviceBtn = findViewById(R.id.profileDeviceBtn);

        //Displaying user info
        usrNameTV.setText(localName);
        usrUsernameTV.setText("@" + localUsername);

        //Displaying user device
        deviceBtn.setText(deviceName);
    }

    public void openBluetoothScreen(View view) {
        startActivity(new Intent(this, BluetoothScreen.class));
    }

    public void logOutUser(View view) {
        sharedPrefs preference = new sharedPrefs(getApplicationContext());
        preference.setIsLoggedIn(false);
        preference.setIsLoggedOut(true);
        preference.setMDFirstTime(true);

        deleteProfilePhoto();

        userHelperClass userPref = new userHelperClass(getApplicationContext());
        userPref.setCounter(0);

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginScreen.class));
        finish();
    }

    public void openEditScreen(View view) {
        Intent intent = new Intent(this, EditUserInfoScreen.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, usrPhoto, ViewCompat.getTransitionName(usrPhoto));
        startActivity(intent, options.toBundle());
    }

    public void trialScreen(View view) {
        onBackPressed();
    }

    public void openChangePassword(View view) {
        startActivity(new Intent(this, ChangePasswordScreen.class));
    }

    private void loadImageFromStorage(String path) {
        try {
            userHelperClass userData = new userHelperClass(getApplicationContext());

            File f = new File(path, userData.getUsername() + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            CircleImageView img = findViewById(R.id.profilePhoto);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
        }
    }

    private void deleteProfilePhoto(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File oldFile = new File(directory, localUsername + ".jpg");
        oldFile.delete();
    }

    public void getHelp(View view) {
        makePhoneCall();
    }

    private void makePhoneCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserProfileScreen.this,
                    new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else{
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:08447167244"));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        userHelperClass userDataNew = new userHelperClass(getApplicationContext());
        loadImageFromStorage(profilePath);
        deviceBtn.setText(userDataNew.getDeviceName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        userHelperClass userDataNew = new userHelperClass(getApplicationContext());
        loadImageFromStorage(profilePath);
        deviceBtn.setText(userDataNew.getDeviceName());
    }
}
