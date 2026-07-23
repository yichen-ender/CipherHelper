package com.hexihe.cipherhelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ScanActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private SurfaceView surfaceView;
    private ScanOverlayView scanOverlay;
    private Button btnBack;
    private Camera camera;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isDecoding = false;
    private int previewWidth;
    private int previewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        scanOverlay = (ScanOverlayView) findViewById(R.id.scan_overlay);
        btnBack = (Button) findViewById(R.id.btn_back);

        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
            Camera.Parameters params = camera.getParameters();
            Camera.Size size = params.getPreviewSize();
            if (size == null) {
                size = params.getSupportedPreviewSizes().get(0);
                params.setPreviewSize(size.width, size.height);
            }
            previewWidth = size.width;
            previewHeight = size.height;
            camera.setParameters(params);
            camera.startPreview();
        } catch (Exception e) {
            Toast.makeText(this, R.string.error_camera, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (isDecoding) return;
        isDecoding = true;
        final byte[] frameData = data.clone();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                decodeFrame(frameData);
            }
        }, 300);
    }

    private void decodeFrame(byte[] data) {
        try {
            Bitmap bitmap = BarcodeDecoder.yuvToBitmap(data, previewWidth, previewHeight);
            if (bitmap != null) {
                Rect frame = scanOverlay.getFrameRect();
                if (frame != null && frame.width() > 0 && frame.height() > 0) {
                    int sx = frame.left * previewWidth / surfaceView.getWidth();
                    int sy = frame.top * previewHeight / surfaceView.getHeight();
                    int sw = frame.width() * previewWidth / surfaceView.getWidth();
                    int sh = frame.height() * previewHeight / surfaceView.getHeight();
                    sx = Math.max(0, sx);
                    sy = Math.max(0, sy);
                    sw = Math.min(sw, previewWidth - sx);
                    sh = Math.min(sh, previewHeight - sy);
                    if (sw > 0 && sh > 0) {
                        Bitmap cropped = Bitmap.createBitmap(bitmap, sx, sy, sw, sh);
                        String result = BarcodeDecoder.decode(cropped);
                        cropped.recycle();
                        if (result != null) {
                            Intent intent = new Intent();
                            intent.putExtra("result", result);
                            setResult(RESULT_OK, intent);
                            finish();
                            isDecoding = false;
                            bitmap.recycle();
                            return;
                        }
                    }
                }
                bitmap.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isDecoding = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
