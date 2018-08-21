package turn.zio.zara.travel_log;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class TravelCameraActivity extends AppCompatActivity {

    ImageView iv2;
    OrientationEventListener orientEventListener;

    private CameraSurfaceView cameraSurfaceView;
    private FrameLayout frameLayout;

    String arr;

    private ImageView previewImage;

    String action;
    int degrees = 90;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_camera);

        previewImage = (ImageView) findViewById(R.id.previewImage) ;
;

        cameraSurfaceView = new CameraSurfaceView(getApplicationContext(), degrees);
        orientEventListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_NORMAL) {

            @Override
            public void onOrientationChanged(int arg0) {
                if (arg0 >= 315 || arg0 <= 45) {
                    degrees = 90;
                } else if (arg0 >= 46 && arg0 <= 135) {
                    degrees = 180;
                } else if (arg0 >= 136 && arg0 <= 225) {
                    degrees = 270;
                } else if (arg0 >= 225 && arg0 <= 314) {
                    degrees = 0;
                }
            }
        };

        if (orientEventListener.canDetectOrientation()) {
            orientEventListener.enable();
        }

        frameLayout = (FrameLayout) findViewById(R.id.travel_camera_frame);
        frameLayout.addView(cameraSurfaceView);

        Intent i = getIntent();
        // File f = (File)i.getExtras().getParcelable("img") ;
    }

    public void memory(View view) {
        Intent intent = new Intent(getApplicationContext(), AlbumSelectActivity.class);
        startActivityForResult(intent, 1);
    }

    public void autoFocus(View view) {
        cameraSurfaceView.camera.autoFocus(mAutoFocus);
    }

    Camera.AutoFocusCallback mAutoFocus = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                Toast.makeText(getApplicationContext(), "Auto Focus Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Auto Focus Failed", Toast.LENGTH_SHORT).show();
            }

        }
    };


    public void takephoto(View view) {
        cameraSurfaceView.Cameradisplay(degrees);
        cameraSurfaceView.takePhoto(takePhoto);
    }

    Camera.PictureCallback takePhoto = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            String path;
            String pathState = Environment.getExternalStorageState();

            if (pathState.equals(Environment.MEDIA_MOUNTED)) {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/";
                File file = new File(path);

                if (!file.exists())
                    file.mkdirs();
            } else {
                Toast.makeText(getApplicationContext(), "sd card 인식 실패", Toast.LENGTH_SHORT).show();
                return;
            }
            String file_name = System.currentTimeMillis() + "_Travel_log";
            String path_root = path + file_name + ".jpg";

            File file = new File(path_root);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(data);
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.parse("file://" + path_root);
            intent.setData(uri);
            sendBroadcast(intent);

            Log.d("path_root", path_root);

            //Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length) ;
            //saveBitmapToJpeg(bitmap,System.currentTimeMillis()+"_Travel_log");

            //String image = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap , "","") ;

            Toast.makeText(getApplicationContext(), "찍은 사진이 저장되었습니다", Toast.LENGTH_LONG).show();

            camera.startPreview();

            intent = new Intent();
            intent.putExtra("filepath", path_root);
            intent.putExtra("file_name", file_name);
            intent.putExtra("degrees", degrees + "");
            setResult(RESULT_OK, intent);

        }

    };

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) // 액티비티가 정상적으로 종료되었을 경우
        {
            if (requestCode == 1) // requestCode==1 로 호출한 경우에만 처리.
            {
                arr = data.getExtras().getString("data");

                File imgFile = new File(arr);
                ExifInterface exif = null;
                Matrix matrix = null;
                Log.d("그거", arr);
                try {
                    exif = new ExifInterface(arr);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                    Log.d("or9", "우아아아");
                    matrix = new Matrix();
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                int width = myBitmap.getWidth();
                int height = myBitmap.getHeight();
                Bitmap b2 = Bitmap.createBitmap(myBitmap, 0, 0, width, height, matrix, true);

                previewImage.setVisibility(View.VISIBLE);
                previewImage.setImageBitmap(b2);
                previewImage.setScaleType(ImageView.ScaleType.FIT_XY);

            }
        }
    }
    @Override
    protected void onDestroy() {
        Log.d("OOMTEST", "onDestroy");
        if(iv2 != null ) {
            recycleBitmap(iv2);
        }
        if(previewImage != null) {
            recycleBitmap(previewImage);
        }

        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }

    private static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
    }

}

