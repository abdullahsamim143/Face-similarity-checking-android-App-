package com.example.demoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facelibrary.FaceLibrary;
import com.example.facelibrary.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    public Context context = MainActivity.this;
    Button selectImg1Btn, selectImg2Btn, getResultBtn;
    TextView resultTxt;
    ImageView image1, image2;
    Boolean btn1True = false, btn2True = false;
    Bitmap btm1, btm2;
    Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectImg1Btn = (Button) findViewById(R.id.img_first_btn);
        selectImg2Btn = (Button) findViewById(R.id.img_second_btn);
        getResultBtn  = (Button) findViewById(R.id.get_result_btn);
        resultTxt = (TextView) findViewById(R.id.result);
        image1 = (ImageView) findViewById(R.id.img_first);
        image2 = (ImageView) findViewById(R.id.img_second);

        selectImg1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn2True = false;
                btn1True = true;
                chooseImage();
            }
        });

        selectImg2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn2True = true;
                btn1True = false;
                chooseImage();
            }
        });

        getResultBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                FaceLibrary faceLibrary = new FaceLibrary();
                if (btm1 != null && btm2 != null){
                   result = faceLibrary.imageCheck(btm1,btm2, context);
                   ProgressDialog progress = new ProgressDialog(MainActivity.this);
                   progress.setTitle(getResources().getString(R.string.loading));
                   progress.setMessage(getResources().getString(R.string.msg));
                   progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                   progress.show();

                   Handler handler = new Handler();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           progress.dismiss();
                           faceLibrary.getResult();
                           resultTxt.setText(getResources().getString(R.string.result_is) + result.score);
                           MSG(result.toString(), getResources().getString(R.string.result_is));
                       }
                   }, 5000);

                }else
                   Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_image_first), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                if (btn1True){
                    image1.setImageBitmap(bitmap);
                    btm1 = bitmap;
                }
                else if (btn2True){
                    image2.setImageBitmap(bitmap);
                    btm2 = bitmap;
                }
                
                saveToInternalStorage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = null;
        if (btn1True)
             mypath = new File(directory, "img1.jpg");
        else if (btn2True)
             mypath = new File(directory, "img2.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
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
        return directory.getAbsolutePath();
    }

    public void MSG(String message, String title) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    
}