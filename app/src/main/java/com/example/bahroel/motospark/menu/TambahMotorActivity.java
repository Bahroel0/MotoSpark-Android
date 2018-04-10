package com.example.bahroel.motospark.menu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bahroel.motospark.MainActivity;
import com.example.bahroel.motospark.R;
import com.example.bahroel.motospark.helper.SQLiteHandler;
import com.example.bahroel.motospark.helper.SessionManager;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.bahroel.motospark.api.url.SET_POSISI_MOTOR_URL;
import static com.example.bahroel.motospark.api.url.UPLOAD_URL;

public class TambahMotorActivity extends AppCompatActivity {
    private ImageView takePicture;
    private ImageView imageView;
    private Button btn_tambahkan;
    private SQLiteHandler db;
    private String id_user;
    private EditText nama_motor;
    private EditText id_plat;
    private Button btn_connect;
    private Bitmap bitmap;
    private SessionManager session;

    private static final String TAG = TambahMotorActivity.class.getSimpleName();

    CameraPhoto cameraPhoto;
    // Activity request codes
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    final int CAMERA_REQUEST = 13323;


    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 123;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Foto Motor";

    private Uri fileUri; // file url to store image/video
    String photoPath;
    Boolean kondisi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_motor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Tambahkan Motor");

        cameraPhoto = new CameraPhoto(getApplicationContext());
        takePicture = (ImageView) findViewById(R.id.btnCamera);
        imageView   = (ImageView) findViewById(R.id.foto_motor);
        btn_tambahkan = (Button) findViewById(R.id.btn_tambahkan);
        nama_motor = (EditText) findViewById(R.id.input_nama_motor);
        id_plat = (EditText) findViewById(R.id.input_nopol);
        btn_connect = (Button) findViewById(R.id.btn_connect);

        btn_connect.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String platMotor = id_plat.getText().toString().trim();
               setPosisiMotor(platMotor, "-7.2767636","112.7927149","Politeknik Elektronika Negeri Surabaya");
               // move to home fragment if upload is success

//               setPosisiMotor(idPlat, "3","4","Hall D4 PENS");
               startActivity(new Intent(TambahMotorActivity.this, MainActivity.class));
           }
       });
        /*
		 * Capture image button click event
		 */
        btn_tambahkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kondisi = true;
                if(kondisi == true){
                    uploadMultipart();
//                    String platMotor = id_plat.getText().toString().trim();
//                    Log.i(TAG, "idPlat :" + platMotor);

                }else{
                    Toast.makeText(getApplicationContext(), "Koneksikan dengan Raspi", Toast.LENGTH_SHORT).show();
                }

            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void setPosisiMotor(final String platMotor, final String lat, final String longi, final String posisi){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

//        progressDialog.setMessage("Sinkronisasi Posisi Motor");
//        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, SET_POSISI_MOTOR_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                progressDialog.hide();

                try {
                    JSONObject jObj = new JSONObject(response);
                    Integer error = jObj.getInt("success");
                    Log.i(TAG, "lat : " + longi);
                    // Check for error node in json
                    if (error == 0) {
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    } else if(error == 1){
                        // user successfully register
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        kondisi = true;
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                Toast.makeText(getApplicationContext(), "Register Error", Toast.LENGTH_SHORT).show();

                progressDialog.hide();
            }
        }) {

            @Override
            protected Map<String,String> getParams() {
                // Posting parameters to set posisi url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_plat", platMotor);
                params.put("lat", lat);
                params.put("longi", longi);
                params.put("nama_posisi", posisi);

                return params;
            }

        };

        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void selectImage(){
        final CharSequence[] items = { "Kamera", "Galery", "Batalkan" };
        AlertDialog.Builder builder = new AlertDialog.Builder(TambahMotorActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Kamera")) {
                    try {
                        startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                        cameraPhoto.addToGallery();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Ada error ketika mengambil Foto", Toast.LENGTH_SHORT).show();
                    }
                } else if (items[item].equals("Galery")) {
                   // capture picture
                    showFileChooser();
                } else if (items[item].equals("Batalkan")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void startActivityForResult(Intent intent) {

    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == CAMERA_REQUEST){
                photoPath = cameraPhoto.getPhotoPath();
                Bitmap bitmap = null;
                try {
                    bitmap = ImageLoader.init().from(photoPath).requestSize(512,512).getBitmap();
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 600,400, true));
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Ada error ketika meload Foto", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            photoPath = getPath(fileUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 600,400, true));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadMultipart() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        id_user = user.get("id_user");

        //getting name for the image
        String namaMotor = nama_motor.getText().toString().trim();
        String idPlat = id_plat.getText().toString().trim();
        String status = "0";


        //getting the actual path of the image
        String path = photoPath;
        //Uploading code
        try {

            //Dismissing the progress dialog
            loading.dismiss();

            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(getApplicationContext(), uploadId, UPLOAD_URL)
                    .addFileToUpload(path, "imageFile") //Adding file
                    .addParameter("id_plat", idPlat) //Adding text parameter to the request
                    .addParameter("id_user", id_user) //Adding text parameter to the request
                    .addParameter("nama_motor", namaMotor) //Adding text parameter to the request
                    .addParameter("status", status) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
             Toast.makeText(getApplicationContext(), "Tekan tombol connect", Toast.LENGTH_SHORT).show();


        } catch (Exception exc) {
            //Dismissing the progress dialog
            loading.dismiss();
           // Toast.makeText(getApplicationContext(), "Gagal Upload ke server", Toast.LENGTH_SHORT).show();
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getApplicationContext(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getApplicationContext(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(TambahMotorActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
