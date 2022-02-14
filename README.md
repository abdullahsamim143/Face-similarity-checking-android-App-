
# Face similarity checking android App

This is demo App for face similarity checking.


## Screenshots

![1](https://user-images.githubusercontent.com/54929555/153857967-cdd2e7e8-c3ff-4624-8295-6189cf7626c5.png)



## Demo

Video is here 

https://user-images.githubusercontent.com/54929555/153858088-9b4a36a6-a642-4f51-b0b0-a49676571857.mp4



## Gradle

Add volley implementation

```bash
 implementation 'com.android.volley:volley:1.2.0'
```

Add GSON implementation

```bash
 implementation 'com.google.code.gson:gson:2.8.9'
```


## MainActivity

```bash
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
```

## API

```bash
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
```


## FaceLibrary

```bash
 
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
```


## Splash Screen 

```bash
 public class SplashScreen extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(SPLASH_DISPLAY_LENGTH);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();

    }
}
```


## Result class

```bash
 public class Result {
    public String error;
    public Double score;
    public Boolean isTrue;

    @Override
    public String toString() {
        return "Similarity score: "+ score + "\n" + "Result: "+ isTrue + "\n" + "Error: "+ error;
    }

}

```
 


