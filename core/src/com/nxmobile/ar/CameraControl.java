package com.nxmobile.ar;

import com.badlogic.gdx.graphics.Pixmap;

import com.badlogic.gdx.files.FileHandle;


/**
 * Created by luokangyu on 13/02/2018.
 * 控制 Android 前鏡頭
 */

public interface CameraControl
{
    void prepareCamera();
    void startPreview();
    void stopPreview();
    void takePicture();
    byte[] getPictureData();

    // Asynchronous interface - need when called from a non platform thread (GDXOpenGl thread)
    void startPreviewAsync();
    void stopPreviewAsync();
    void prepareCameraAsync();


    byte[] takePictureAsync(long timeout);
    void saveAsJpeg(FileHandle jpegfile, Pixmap cameraPixmap);
    boolean isReady();



}
