package com.example.jakub.numberrecognizer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class ShowCamera extends SurfaceView implements SurfaceHolder.Callback {

//    Camera camera;
    SurfaceHolder holder;
    Context context;
    CameraSource cameraSource;
    TextRecognizer textRecognizer;

    /*ShowCamera(Context context){
        super(context);
        this.context = context;
    }*/

    ShowCamera(final Context context/*, Camera camera*/) {
        super(context);
        this.context = context;
//        this.camera = camera;
        this.holder = getHolder();
        holder.addCallback(this);

        textRecognizer = createCameraSource();



    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        Camera.Parameters params = camera.getParameters();

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 101);
        }
/*
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            params.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);
        } else {
            params.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            params.setRotation(0);
        }

        camera.setParameters(params);
*/
        try {
            //camera.setPreviewDisplay(holder);
            cameraSource.start(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public TextRecognizer createCameraSource(){
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();



        if(textRecognizer.isOperational()) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            cameraSource = new CameraSource.Builder(context, textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(30.0f)
                    .build();
        } else {
            Toast.makeText(context,"Detector dependencies not loaded yet.", Toast.LENGTH_LONG).show();
            Log.w("ShowCamera", "Detector dependencies not loaded yet.");
        }
        return textRecognizer;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final TextView textView = findViewById(R.id.recognized);
                final SparseArray<TextBlock> items = detections.getDetectedItems();

                if(textView == null){
                    Toast.makeText(context,"null textview", Toast.LENGTH_SHORT).show();
                }

                if(items.size() > 0) {

                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i = 0; i < items.size(); i++){
                                TextBlock block = items.valueAt(i);
                                stringBuilder.append(block);
                            }
                            textView.setText(stringBuilder);
                        }
                    });
                }
            }
        });


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraSource.stop();
    }


}
