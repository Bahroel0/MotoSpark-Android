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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bahroel.motospark.MainActivity;
import com.example.bahroel.motospark.R;
import com.example.bahroel.motospark.helper.SQLiteHandler;
import com.example.bahroel.motospark.helper.SessionManager;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.ALL;
import static com.example.bahroel.motospark.api.url.EDIT_MOTOR_URL;

public class EditMotorActivity extends AppCompatActivity {
    private ImageView takePicture;
    private ImageView imageView;
    private Button btn_tambahkan;
    private SQLiteHandler db;
    private String id_user;
    private EditText nama_motor;
    private TextView id_plat;
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
    String dataFotoMotor;
    String dataNamaMotor;
    String dataNopol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_motor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Edit Motor");

        cameraPhoto = new CameraPhoto(getApplicationContext());
        takePicture = (ImageView) findViewById(R.id.btnCameraEdit);
        imageView   = (ImageView) findViewById(R.id.foto_motor_edit);
        imageView.getLayoutParams().width = 600;
        imageView.getLayoutParams().height = 400;
        btn_tambahkan = (Button) findViewById(R.id.btn_connect_edit);
        nama_motor = (EditText) findViewById(R.id.input_nama_motor_edit);
        id_plat = (TextView) findViewById(R.id.input_nopol_edit);

        Intent intent = getIntent();
        Bundle data = intent.getExtras();

        if(data != null){
            dataNamaMotor = (String) data.get("nama_motor");
            dataNopol = (String) data.get("id_plat");
            dataFotoMotor = (String) data.get("foto");
        }

        nama_motor.setText(dataNamaMotor);
        id_plat.setText(dataNopol);
        Glide.with(this).load(dataFotoMotor )
                .override(50,50)
                .thumbnail(0.5f)
                .diskCacheStrategy(ALL)
                .crossFade()
                .into(imageView);

        takePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btn_tambahkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadMultipart();
            }
        });
    }

    private void selectImage(){
        final CharSequence[] items = { "Kamera", "Galery", "Batalkan" };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditMotorActivity.this);
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

    public void uploadMultipart() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        //getting name for the image
        String namaMotor = nama_motor.getText().toString().trim();
        String status = "0";


        //getting the actual path of the image
        String path = photoPath;
        //Uploading code
        try {

            //Dismissing the progress dialog
            loading.dismiss();

            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(getApplicationContext(), uploadId, EDIT_MOTOR_URL + dataNopol)
                    .addFileToUpload(path, "imageFile") //Adding file
                    .addParameter("nama_motor", namaMotor) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

            startActivity(new Intent(EditMotorActivity.this, MainActivity.class));



        } catch (Exception exc) {
            //Dismissing the progress dialog
            loading.dismiss();
            // Toast.makeText(getApplicationContext(), "Gagal Upload ke server", Toast.LENGTH_SHORT).show();
        }
    }
    private void startActivityForResult(Intent intent) {

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
        Intent intent = new Intent(EditMotorActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
