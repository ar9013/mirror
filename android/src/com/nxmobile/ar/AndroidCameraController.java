package com.nxmobile.ar;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.badlogic.gdx.graphics.Pixmap;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


import com.badlogic.gdx.files.FileHandle;


/**
 * Created by luokangyu on 13/02/2018.
 * 實作 CameraControl 定義的方法，使 AndroidCameraController 物件，有CameraControl 的功能
 */

public class AndroidCameraController implements CameraControl , Camera.PictureCallback,Camera.AutoFocusCallback,Camera.PreviewCallback{

    private static final String TAG = "AndroidCameraController";

    private static final int ONE_SECOND_IN_MILI = 1000;
    private final AndroidLauncher activity;
    private CameraSurface cameraSurface;
    private byte[] pictureData;
    private Context context;

    public AndroidCameraController(AndroidLauncher activity){
        this.activity = activity;

    }

    @Override
    public void prepareCamera() {
        // 建立 cameraSurface
        if(cameraSurface == null){
            cameraSurface = new CameraSurface(activity);
        }

        activity.addContentView(cameraSurface,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void startPreview() {
        // 做 camera surface 及 camera 的判斷
        Log.d(TAG,"startPreview");
        if(cameraSurface != null && cameraSurface.getCamera() !=null){
            cameraSurface.getCamera().startPreview();
        }

    }

    @Override
    public void stopPreview() {

        if(cameraSurface != null){
            ViewParent parentView = cameraSurface.getParent();
            if (parentView instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) parentView;
                viewGroup.removeView(cameraSurface);
            }
            if (cameraSurface.getCamera() != null) {
                cameraSurface.getCamera().stopPreview();
            }
        }
        activity.restoreFixedSize();

    }

   public void setCameraParametersForPicture(Camera camera){
       // Before we take the picture - we make sure all camera parameters are
       // as we like them
       // Use max resolution and auto focus
       Camera.Parameters p = camera.getParameters();
       List<Camera.Size> supportedSizes = p.getSupportedPictureSizes();
       int maxSupportedWidth = -1;
       int maxSupportedHeight = -1;
       for (Camera.Size size : supportedSizes) {
           if (size.width > maxSupportedWidth) {
               maxSupportedWidth = size.width;
               maxSupportedHeight = size.height;
           }
       }
       p.setPictureSize(maxSupportedWidth, maxSupportedHeight);
       p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
       camera.setParameters(p);
   }



    @Override
    public void takePicture() {

        setCameraParametersForPicture(cameraSurface.getCamera());
        cameraSurface.getCamera().autoFocus(this);

    }

    @Override
    public byte[] getPictureData() {
        return pictureData;
    }

    @Override
    public void startPreviewAsync() {
        Runnable r = new Runnable() {
            public void run() {
                startPreview();
            }
        };
        activity.post(r);
    }

    @Override
    public void stopPreviewAsync() {
        Runnable r = new Runnable() {
            public void run() {
                stopPreview();
            }
        };
        activity.post(r);

    }

    @Override
    public void prepareCameraAsync() {
        Runnable r = new Runnable() {
            public void run() {
                prepareCamera();
            }
        };
        activity.post(r);
    }




    @Override
    public byte[] takePictureAsync(long timeout) {
        timeout *= ONE_SECOND_IN_MILI;

        Runnable r = new Runnable() {
            public void run() {
                takePicture();
            }
        };
        activity.post(r);
        while (pictureData == null && timeout > 0) {
            try {
                Thread.sleep(ONE_SECOND_IN_MILI);
                timeout -= ONE_SECOND_IN_MILI;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (pictureData == null) {
            cameraSurface.getCamera().cancelAutoFocus();
        }
        return pictureData;
    }

    @Override
    public void saveAsJpeg(FileHandle jpgfile, Pixmap pixmap) {
        FileOutputStream fos;
        int x = 0, y = 0;
        int xl = 0, yl = 0;
        try {
            Bitmap bmp = Bitmap.createBitmap(pixmap.getWidth(),
                    pixmap.getHeight(), Bitmap.Config.ARGB_8888);
            // we need to switch between LibGDX RGBA format to Android ARGB
            // format
            for (x = 0, xl = pixmap.getWidth(); x < xl; x++) {
                for (y = 0, yl = pixmap.getHeight(); y < yl; y++) {
                    int color = pixmap.getPixel(x, y);
                    // RGBA => ARGB
                    int RGB = color >> 8;
                    int A = (color & 0x000000ff) << 24;
                    int ARGB = A | RGB;
                    bmp.setPixel(x, y, ARGB);
                }
            }

            fos = new FileOutputStream(jpgfile.file());
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isReady() {
        if (cameraSurface != null && cameraSurface.getCamera() != null) {
            return true;
        }
        return false;
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
        }
    };

    Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera arg1) {

            pictureData = data;

        };
    };




    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        // Focus process finished, we now have focus (or not)
        if (success) {
            if (camera != null) {

                camera.takePicture(shutterCallback, null, jpegPictureCallback);

                camera.startPreview();

            }
        }

    }

    @Override
    public void onPictureTaken(byte[] pictureData, Camera camera) {
        this.pictureData = pictureData;
    }


    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

    }
}
