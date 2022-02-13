package com.example.facelibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static com.example.facelibrary.API.Score;


public class FaceLibrary {
    Result result = new Result();
    public Result imageCheck(Bitmap bitmap1, Bitmap bitmap2, Context context){
        String img1Base64String = ImageToBase64String(bitmap1);
        String img2Base64String = ImageToBase64String(bitmap2);
        API api = new API(context);
        api.sendDataToApi(img1Base64String,img2Base64String);

        return result;
    }

    private String ImageToBase64String(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        bitmap.describeContents();
        byte[] imageBytes = baos.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void getResult(){
        result.score = Score;
        result.isTrue = Score >= 0.4;
        result.error = "No Error";
    }
}
