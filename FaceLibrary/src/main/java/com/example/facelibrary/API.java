package com.example.facelibrary;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class API {
        Context context;
    private static final String URL ="https://face.yoonik.me/v1.1/yoonik/face/verify_images";
    Result result = new Result();
    public static double Score;
    public API(Context context1) {
        this.context = context1;
    }

    public void sendDataToApi(String firstImage, String secondImage){
        JSONObject postData = new JSONObject();
        try {
            postData.put("first_image", firstImage);
            postData.put("second_image", secondImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("JSON", String.valueOf(response));
                    Score = -1;
                    Score = response.getDouble("score");
                    FaceLibrary f = new FaceLibrary();
                    f.getResult();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result.error = error.toString()+"_Error";
                VolleyLog.d( ((Activity)context).getResources().getString(R.string.error),  ((Activity)context).getResources().getString(R.string.error)+" : " + error.getMessage());
                if (error instanceof NetworkError) {
                } else if (error instanceof ServerError) {
                } else if (error instanceof AuthFailureError) {
                } else if (error instanceof ParseError) {
                } else if (error instanceof NoConnectionError) {
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(context,
                            "Oops. Timeout error!",
                            Toast.LENGTH_LONG).show();
                }
            }
        })
        {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("x-api-key","OqezCcOWPIMtbuZ2afkmg4puDiWxqZEISazT2JjG8dyGNUebtCMvEC6BnfOz");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        queue.add(request);
    }

}
