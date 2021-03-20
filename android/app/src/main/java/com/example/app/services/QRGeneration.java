package com.example.app.services;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class QRGeneration {

    public static Bitmap GetBitMap(String buildingName){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try{
            BitMatrix bitMatrix = qrCodeWriter.encode(buildingName, BarcodeFormat.QR_CODE, 200,200);
            Bitmap bitmap = Bitmap.createBitmap(200,200, Bitmap.Config.RGB_565);
            for(int i =0;i<200;i++){//go through all 200x200 pixels
                for(int y =0;y<200;y++){
                    bitmap.setPixel(i,y,bitMatrix.get(i,y)? Color.BLACK : Color.WHITE);
                }
            }
           return bitmap;
        } catch(Exception e){
            Log.d("Error returning BitMap: ",e.toString());
        }
        return null;

    }
    public static InputStream BitMapToInputStream(Bitmap buildinBitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        buildinBitmap.compress(Bitmap.CompressFormat.PNG,60 , outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return inputStream;
    }
}
