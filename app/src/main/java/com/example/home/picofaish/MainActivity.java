package com.example.home.picofaish;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.io.File;

public class MainActivity extends Activity {
    private static int RESULT_LOAD_IMG = 1;
                String imgDecodableString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY, Default.VERSION);

    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Star t the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }
   /* void public void Backendless.Files.upload( android.graphics.Bitmap bitmap,android.graphics.Bitmap.CompressFormat compressFormat, int quality, String remoteName, String remotePath,
    AsyncCallback<BackendlessFile> responder ) throws Exception
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data


                Uri selectedImage = data.getData();
                final String[] filePathColumn = { MediaStore.Images.Media.DATA };
                File imageFile;

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                Uri contentURI=selectedImage;
                if(cursor==null )
                    imageFile = new File(contentURI.getPath());

                else
                {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    imageFile = new File(cursor.getString(columnIndex));

                }
               // File imageFile = new File( getRealPathFromURI( selectedImage ) );

             /*   Backendless.Files.upload( imageFile, Default.DEFAULT_PATH_ROOT, new AsyncCallback<BackendlessFile>()
                {
                    @Override
                    public void handleResponse( BackendlessFile response )
                    {
                        String photoBrowseUrl = response.getFileURL();
                        Intent intent = new Intent();
                        intent.putExtra( Default.PHOTO_BROWSE_URL, photoBrowseUrl );
                        intent.putExtra(Default.FILE_PATH, filePathColumn);
                        setResult(Default.ADD_NEW_PHOTO_RESULT, intent);
                        //progressDialog.cancel();
                        finish();
                    }

                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        //progressDialog.cancel();
//                        Toast.makeText( MakeChoiceActivity.this, fault.toString(), Toast.LENGTH_SHORT ).show();
                    }
                } );
*/
                // Get the cursor
                // Move to first row

                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));
                final Bitmap viewBitmap = Bitmap.createBitmap(imgView.getWidth(), imgView.getHeight(), Bitmap.Config.ARGB_8888);//i is imageview whch u want to convert in bitmap
                Canvas canvas = new Canvas(viewBitmap);

                imgView.draw(canvas);

Thread thread = new Thread(new Runnable() {
    @Override
    public void run() {

        try
        {
            Backendless.Files.Android.upload(viewBitmap, Bitmap.CompressFormat.PNG, 100, "myphoto.png", "img");


            new AsyncCallback<BackendlessFile>()
            {
                @Override
                public void handleResponse(final BackendlessFile backendlessFile)
                {
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault)
                {
                    Toast.makeText(MainActivity.this, "running", Toast.LENGTH_SHORT).show();
                    //Log.i(TAG, "reply cancelled", exc);

                }
            };
        }catch (Exception e)
        {
            e.printStackTrace();
        }




    }
});

                thread.start();






            }




            else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }



        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();

        }

    }

}