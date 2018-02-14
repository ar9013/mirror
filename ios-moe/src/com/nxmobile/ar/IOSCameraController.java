package com.nxmobile.ar;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.logging.FileHandler;

/**
 * Created by luokangyu on 14/02/2018.
 */

public class IOSCameraController implements CameraControl {
    @Override
    public void prepareCamera() {

    }

    @Override
    public void startPreview() {

    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void takePicture() {

    }

    @Override
    public byte[] getPictureData() {
        return new byte[0];
    }

    @Override
    public void startPreviewAsync() {

    }

    @Override
    public void stopPreviewAsync() {

    }

    @Override
    public void preparePreviewAsync() {

    }

    @Override
    public byte[] takePictureAsync(long timeout) {
        return new byte[0];
    }

    @Override
    public void saveAsJpeg(FileHandle jpegfile, Pixmap cameraPixmap) {

    }



    @Override
    public boolean isReady() {
        return false;
    }
}
