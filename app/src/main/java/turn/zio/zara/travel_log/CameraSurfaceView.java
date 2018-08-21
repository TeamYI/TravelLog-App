package turn.zio.zara.travel_log;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by 하루마다 on 2017-04-29.
 */

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera camera = null;
    int degrees = 90;
    int m_resWidth;
    int m_resHeight;

    public CameraSurfaceView(Context context) {
        super(context);

        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public CameraSurfaceView(Context context, int degrees) {
        super(context);

        this.degrees = degrees;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void Cameradisplay(int degrees) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            camera.setDisplayOrientation(90);
            parameters.setRotation(degrees);
            m_resWidth = camera.getParameters().getPictureSize().width;
            m_resHeight = camera.getParameters().getPictureSize().height;
            Log.d("width", m_resWidth + "");
            Log.d("height", m_resHeight + "");
            parameters.setPictureSize(m_resWidth, m_resHeight);
            camera.setParameters(parameters);
            camera.setPreviewDisplay(mHolder);
        } catch (Exception e) {
            Log.e("CameraSurfaceView", "Failed to set camera preview.", e);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Cameradisplay(degrees);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public boolean takePhoto(Camera.PictureCallback handler) {
        if (camera != null) {
            camera.takePicture(null, null, handler);
            return true;

        } else {
            return false;
        }
    }


}
