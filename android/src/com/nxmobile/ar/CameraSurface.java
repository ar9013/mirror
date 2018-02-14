package com.nxmobile.ar;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by luokangyu on 13/02/2018.
 */

class CameraSurface extends SurfaceView implements SurfaceHolder.Callback{

    private Camera camera = null;


    public CameraSurface(Context context) {
        super(context);

        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        camera.setDisplayOrientation(90);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        Camera.Parameters params = camera.getParameters();
        camera.setParameters(params);

        try{
            camera.setPreviewDisplay(holder);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public Camera getCamera() {
        return camera;
    }
}
