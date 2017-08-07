package com.byteshaft.teranect.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.byteshaft.teranect.JobDetailsActivity;
import com.byteshaft.teranect.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRcodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    private ZXingScannerView mScannerView;
    private ImageButton selectImageButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        selectImageButton = (ImageButton) findViewById(R.id.select_image_button);
        backButton = (ImageButton) findViewById(R.id.back_button);
        mScannerView = (ZXingScannerView) findViewById(R.id.scanner_qr);
        mScannerView.startCamera();
        selectImageButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.startCamera();
        mScannerView.setResultHandler(this);

    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("Tag", rawResult.getText()); // Prints scan results
        Toast.makeText(getApplicationContext(), rawResult.getText(),Toast.LENGTH_SHORT).show();
        Log.v("Tag", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
        Intent intent = new Intent(QRcodeActivity.this, JobDetailsActivity.class);
        intent.putExtra("id", rawResult.getText());
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_image_button:
                pickImage();
                break;
            case R.id.back_button:
                onBackPressed();
                break;

        }
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
//        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
    }

    public static String decodeWithZxing(Bitmap bitmap) {
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        Map<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        multiFormatReader.setHints(hints);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        Result rawResult = null;
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);

        if (source != null) {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(binaryBitmap);
                System.out.println(rawResult);
            } catch (ReaderException re) {
                re.printStackTrace();
            } finally {
                multiFormatReader.reset();
            }
        }
        return rawResult != null ? rawResult.getText() : null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image
                Bitmap qrCodeImage = extras.getParcelable("data");
                decodeWithZxing(qrCodeImage);
            }
        }
    }
}
