package turn.zio.zara.travel_log;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AlbumSelectActivity extends AppCompatActivity {

    GalleryAdapter mAdapter;
    RecyclerView mRecyclerView;

    ArrayList<File> list;
    ArrayList<ImageModel> data = new ArrayList<>();

    private ImageView previewImage;
    private int positions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_select);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/";
        File file = new File(path);
        list = imageReader(file);
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/";
        file = new File(path);
        list = imageReader(file);
        previewImage = (ImageView) findViewById(R.id.previewImage);

        for (int i = 0; i < list.size(); i++) {
            ImageModel imageModel = new ImageModel();
            imageModel.setName("Image " + i);
            imageModel.setUrl(list.get(i) + "");
            data.add(imageModel);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new GalleryAdapter(AlbumSelectActivity.this, data);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {


            @Override
            public void onItemClick(View view, int position) {
                File imgFile = new File(data.get(position).getUrl());
                ExifInterface exif = null;
                Matrix matrix = null;
                positions = position;
                try {
                    exif = new ExifInterface(data.get(position).getUrl());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                    Log.d("or9", orientation + "");
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
        }));

    }

    public void bakcMain(View view) {
        finish();
    }

    public void Album_submit(View view) {
        Intent intent = getIntent();
        intent.putExtra("data", data.get(positions).getUrl());
        setResult(RESULT_OK, intent);
        finish();
    }

    ArrayList<File> imageReader(File root) {

        ArrayList<File> a = new ArrayList<>();

        File[] files = root.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                a.addAll(imageReader(files[i]));
            } else {
                if (files[i].getName().endsWith(".jpg")) {
                    a.add(files[i]);
                }
            }
        }
        return a;
    }
}
