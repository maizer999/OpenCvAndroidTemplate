package com.example.nativeopencvandroidtemplate;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenCVLoader.initDebug();
    }


    Mat img = null;
    public void applyFilter(View v){


        try {
            img = Utils.loadResource(getApplicationContext(), R.drawable.test5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Mat img_result = img.clone();
        Mat img_result2 = img.clone();

        Imgproc.Canny(img, img_result, 80, 90);
        Bitmap img_bitmap = Bitmap.createBitmap(img_result2.cols(), img_result2.rows(),Bitmap.Config.RGB_565);
        Bitmap img_bitmap2 = Bitmap.createBitmap(img_result2.cols(), img_result2.rows(),Bitmap.Config.ALPHA_8);

//        Imgproc.cvtColor(img, new Mat(), Imgproc.COLOR_RGB2BGRA);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc .findContours(img_result, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint2f approxCurve =  new MatOfPoint2f();


        for (MatOfPoint cnt : contours) {

            MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());
            Imgproc.approxPolyDP(curve, approxCurve, 0.02 * Imgproc.arcLength(curve, true), true);
            int numberVertices = (int) approxCurve.total();

            double contourArea = Imgproc.contourArea(cnt);
            Log.d("MAIZER  contourArea  ",""+contourArea);
//            if (Math.abs(contourArea) > 1000) {
//                continue;
//            }

            if (numberVertices >= 4 && numberVertices <= 6) {
                List<Double> cos = new ArrayList<>();
                for (int j = 2; j < numberVertices + 1; j++) {
                    cos.add(angle(approxCurve.toArray()[j % numberVertices], approxCurve.toArray()[j - 2], approxCurve.toArray()[j - 1]));

                }

                Collections.sort(cos);
                double mincos = cos.get(0);
                double maxcos = cos.get(cos.size() - 1);

                if (numberVertices == 4 && mincos >= -0.1 && maxcos <= 0.3) {
                    Log.d("MAIZER  ",""+numberVertices);
                    setLabel(img, "X", cnt);
                    MatOfPoint matOfPoint = cnt;
                    Rect rect = Imgproc.boundingRect(matOfPoint);
                    Imgproc.rectangle(img_result2, rect.tl(), rect.br(), new Scalar(0, 255, 0));


                }

            }

        }




//        Utils.matToBitmap(img_result2, img_bitmap);
//        ImageView imageView = findViewById(R.id.img);
//        imageView.setImageBitmap(img_bitmap);
    }

    private void setLabel(Mat im, String label, MatOfPoint contour) {
        int fontface = Imgproc.FONT_HERSHEY_SIMPLEX;
        double scale = 3;//0.4;
        int thickness = 10;//1;
        int[] baseline = new int[1];
        Size text = Imgproc.getTextSize(label, fontface, scale, thickness, baseline);
        Rect r = Imgproc.boundingRect(contour);
        Point pt = new Point(r.x + ((r.width - text.width) / 2),r.y + ((r.height + text.height) / 2));
        Imgproc.putText(im, label, pt, fontface, scale, new Scalar(255, 0, 0), thickness);
    }

    private static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }


}



