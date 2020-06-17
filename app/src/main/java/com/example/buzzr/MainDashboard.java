package com.example.buzzr;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import com.example.buzzr.HelperClasses.sharedPrefs;
import com.example.buzzr.HelperClasses.userHelperClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainDashboard extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;

    TextView welcomeTV, counterTV;

    String localName, localUsername, localPhoneNumber, localPassword, photoPath;
    CircleImageView usrPhoto;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dashboard);

        //User Data
        userHelperClass userData = new userHelperClass(getApplicationContext());
        localName = userData.getName();
        localUsername = userData.getUsername();
        localPhoneNumber = userData.getPhoneNo();
        localPassword = userData.getPassword();
        photoPath = userData.getProfilePhoto();
        userData.setDeviceName("No device found");

        sharedPrefs preference = new sharedPrefs(getApplicationContext());

        if (preference.getMDFirstTime()) {
            loadFromFirebase();
            preference.setMDFirstTime(false);
        } else {
            loadImageFromStorage(photoPath);
        }

        //Hooks
        welcomeTV = findViewById(R.id.dashboardWelcomeTV);
        counterTV = findViewById(R.id.faceCounter);
        usrPhoto = findViewById(R.id.mainProfilePhoto);

        // Setting text
        String arr[] = localName.split(" ", 2);
        String firstName = arr[0];
        welcomeTV.setText("Welcome, " + firstName);

        //Counter
        setCounterText();
    }

    private void setCounterText() {
        sharedPrefs prefs = new sharedPrefs(getApplicationContext());

        if (prefs.getMDFirstTime()) {
            prefs.setCounter(-1);
        }

        counterTV.setText(String.valueOf(prefs.getCounter()));
        //counterTV.setText("39");
    }

    private void loadImageFromStorage(String path) {
        try {
            userHelperClass userData = new userHelperClass(getApplicationContext());

            File f = new File(path, userData.getUsername() + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            CircleImageView img = (CircleImageView) findViewById(R.id.mainProfilePhoto);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            loadFromFirebase();
        }
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, localUsername + ".jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String filePath = directory.getAbsolutePath();

        userHelperClass userData = new userHelperClass(getApplicationContext());
        userData.setProfilePhoto(filePath);
    }

    private void loadFromFirebase() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("profilePhotos");

        StorageReference reference = storageReference.child(localUsername);

        final long ONE_MEGABYTE = 1024 * 1024;
        reference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                saveToInternalStorage(bitmap);
                usrPhoto.setImageBitmap(bitmap);
            }
        });
    }

    public void openUserProfile(View view) {
        Intent intent = new Intent(this, UserProfileScreen.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, usrPhoto, ViewCompat.getTransitionName(usrPhoto));
        startActivity(intent, options.toBundle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        userHelperClass userDataNew = new userHelperClass(getApplicationContext());
        String profilePathNew = userDataNew.getProfilePhoto();
        loadImageFromStorage(profilePathNew);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        userHelperClass userDataNew = new userHelperClass(getApplicationContext());
        String profilePathNew = userDataNew.getProfilePhoto();
        loadImageFromStorage(profilePathNew);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userHelperClass userDataNew = new userHelperClass(getApplicationContext());
        String profilePathNew = userDataNew.getProfilePhoto();
        String userNameNew = userDataNew.getName();

        String arr[] = userNameNew.split(" ", 2);
        String firstName = arr[0];
        welcomeTV.setText("Welcome, " + firstName);

        loadImageFromStorage(profilePathNew);
    }

    public void openStats(View view) {
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }

    public void getHelp(View view) {
        makePhoneCall();
    }

    private void makePhoneCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainDashboard.this,
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

    public void addOne(View view) {
        sharedPrefs preference = new sharedPrefs(getApplicationContext());
        preference.setCounter(preference.getCounter());
        setCounterText();
    }
}
