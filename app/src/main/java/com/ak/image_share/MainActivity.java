package com.ak.image_share;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    ImageView img1,img2;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);

        checkAndRequestPermissions();

        img1=findViewById(R.id.img1);
        img2=findViewById(R.id.img2);

        Glide.with(this).load("https://homepages.cae.wisc.edu/~ece533/images/mountain.png").into(img1);
        Glide.with(this).load("https://homepages.cae.wisc.edu/~ece533/images/boat.png").into(img2);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage(img1);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage(img2);
            }
        });

    }

    private  boolean checkAndRequestPermissions() {
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storage2= ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void saveImage( ImageView ImageViewName){

        BitmapDrawable drawable = (BitmapDrawable) ImageViewName.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File sdCardDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/MYAPP");

        if(!sdCardDirectory.exists()) {
            sdCardDirectory.mkdirs();
        }

        boolean success = false;

        FileOutputStream outStream=null;
        try {
            outStream = new FileOutputStream(sdCardDirectory+"/"+ImageViewName.getId()+".png");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (success) {
            //  Toast.makeText(getApplicationContext(), "Image saved with success",Toast.LENGTH_LONG).show();
            Log.d("Tag","Image Save Success");

            Uri imgUri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/MYAPP/"+ImageViewName.getId()+".png");
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "My Image4You");
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
            whatsappIntent.setType("image/jpeg");
            whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Use package name which we want to check
            boolean isAppInstalled = appInstalledOrNot("com.whatsapp");

            if(isAppInstalled){
                startActivity(whatsappIntent);
            }else{
                Toast.makeText(this, "Install Whatsapp First", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(),"Error During Image Saving", Toast.LENGTH_LONG).show();
        }

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

}