package com.lxf.ui;

import com.lxf.util.ImageToMatUtils;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeController {
    protected SimpleStringProperty imageSrcProperty = new SimpleStringProperty();
    protected SimpleDoubleProperty imageSrcRatioProperty = new SimpleDoubleProperty();
    protected SimpleObjectProperty<Image> targetImageProperty = new SimpleObjectProperty<>();
    protected SimpleObjectProperty<Mat> thresholdMatProperty = new SimpleObjectProperty<>();
    protected SimpleObjectProperty<Mat> cannyMatProperty = new SimpleObjectProperty<>();

    private final List<String> images = new ArrayList<>();
    private Image imageSrc;

    public HomeController() {
        String[] images = new String[]{
                "/tx.png",
                "/tx2.png",
                "/chess1.jpg",
        };
        this.images.addAll(Arrays.asList(images));

        imageSrcProperty.addListener((observable, oldValue, newValue) -> {
            thresholdMatProperty.set(null);
            cannyMatProperty.set(null);

            if (newValue != null) {
                imageSrc = new Image(newValue);
                imageSrcRatioProperty.set(imageSrc.getWidth() / imageSrc.getHeight());
            } else
                imageSrc = null;
        });
    }

    protected void loadImage() {
        imageSrcProperty.setValue(images.get(0));
    }

    protected void nextImage() {
        int index = images.indexOf(imageSrcProperty.getValue());
        int nextIndex = index + 1;
        if (nextIndex >= images.size())
            nextIndex = 0;
        imageSrcProperty.set(images.get(nextIndex));
    }

    protected void gray() {
        Mat src = ImageToMatUtils.image2Mat(imageSrc);
        Mat dst = toGray(src);

        showDst(dst);
    }

    private Mat toGray(Mat src) {
        Mat temp = new Mat();
        Mat dst = new Mat();
        Imgproc.cvtColor(src, temp, Imgproc.COLOR_BGRA2BGR);
        Imgproc.cvtColor(temp, dst, Imgproc.COLOR_BGR2GRAY);
        return dst;
    }

    protected void threshold(double thresh) {
        Mat src = ImageToMatUtils.image2Mat(imageSrc);
        Mat gray = toGray(src);
        Mat dst = new Mat();
        Imgproc.threshold(gray, dst, thresh, 255, Imgproc.THRESH_BINARY);

        showDst(dst);
        thresholdMatProperty.set(dst);
    }

    protected void canny(double threshold1, double threshold2) {
        Mat src = ImageToMatUtils.image2Mat(imageSrc);
        Mat dst = new Mat();
        Imgproc.Canny(src, dst, threshold1, threshold2);

        showDst(dst);
        cannyMatProperty.set(dst);
    }

    protected void contourAfterThreshold() {
        Mat src = thresholdMatProperty.get();
        List<MatOfPoint> contourList = new ArrayList<>();
        Imgproc.findContours(src, contourList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat contours = new Mat();
        contours.create(src.rows(), src.cols(), CvType.CV_8UC3);
        for (int i = 0; i < contourList.size(); i++) {
            MatOfPoint mp = contourList.get(i);
            Rect rect = Imgproc.boundingRect(mp);
            double ratio = rect.width / (double) rect.height;

            if (ratio > 0.95 && ratio < 1.05 && rect.width > 200) {
                Imgproc.drawContours(contours, contourList, i, new Scalar(255, 255, 255), 1);
            }
        }

        showDst(contours);
    }

    protected void contourAfterCanny() {
        Mat src = cannyMatProperty.get();
        List<MatOfPoint> contourList = new ArrayList<>();
        Imgproc.findContours(src, contourList, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat contours = new Mat();
        contours.create(src.rows(), src.cols(), CvType.CV_8UC3);
        for (int i = 0; i < contourList.size(); i++) {
            MatOfPoint mp = contourList.get(i);
            Rect rect = Imgproc.boundingRect(mp);
            double ratio = rect.width / (double) rect.height;

            if (ratio > 0.95 && ratio < 1.05 && rect.width > 200) {
                Imgproc.drawContours(contours, contourList, i, new Scalar(255, 255, 255), 1);
            }
        }

        showDst(contours);
    }

    protected void rotate() {
        Mat src = ImageToMatUtils.image2Mat(imageSrc);

        double width = src.cols();
        double height = src.rows();

        //angle 旋转角度， scale 缩放倍率
        Mat rotationMatrix2D = Imgproc.getRotationMatrix2D(new Point(width / 2, height / 2), 5.0, 1);

        Mat dst = new Mat();
        Imgproc.warpAffine(src, dst, rotationMatrix2D, new Size(width, height));
        showDst(dst);
    }

    protected void transformBy3Point() {
        Mat src = ImageToMatUtils.image2Mat(imageSrc);

        double width = src.cols();
        double height = src.rows();

        Point[] array1 = new Point[3];
        array1[0] = new Point(42, 127);//
        array1[1] = new Point(42 + 395, 127);
        array1[2] = new Point(42, 127 + 395);

        Point[] array2 = new Point[3];
//        array2[0] = new Point(42, 127);
//        array2[1] = new Point(42 + 395, 127);
//        array2[2] = new Point(42 + 20, 127 + 395 - 20);

        array2[0] = new Point(42 + 50, 127);
        array2[1] = new Point(42 + 395 - 50, 127);
        array2[2] = new Point(42, 127 + 395);

        MatOfPoint2f srcPoint = new MatOfPoint2f(array1);
        MatOfPoint2f dstPoint = new MatOfPoint2f(array2);

        //保持原平行线不变，变换后依然平行
        Mat affineTransform = Imgproc.getAffineTransform(srcPoint, dstPoint);
        Mat dst = new Mat();
        Imgproc.warpAffine(src, dst, affineTransform, new Size(width, height));
        showDst(dst);
    }

    protected void transformBy4Point() {
        Mat src = ImageToMatUtils.image2Mat(imageSrc);

        double width = src.cols();
        double height = src.rows();

        Point[] array1 = new Point[4];
        array1[0] = new Point(42, 127);//
        array1[1] = new Point(42 + 395, 127);
        array1[2] = new Point(42, 127 + 395);
        array1[3] = new Point(42 + 395, 127 + 395);

        Point[] array2 = new Point[4];
        array2[0] = new Point(42 + 50, 127);
        array2[1] = new Point(42 + 395 - 50, 127);
        array2[2] = new Point(42, 127 + 395);
        array2[3] = new Point(42 + 395, 127 + 395);

        Mat srcPoint = new MatOfPoint2f(array1);
        Mat dstPoint = new MatOfPoint2f(array2);

        //直线仍然保持直线
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(srcPoint, dstPoint);
        Mat dst = new Mat();
        Imgproc.warpPerspective(src, dst, perspectiveTransform, new Size(width, height));

        showDst(dst);
    }

    protected void adaptiveThreshold() {
        Mat src = ImageToMatUtils.image2Mat(imageSrc);
        Mat gray = toGray(src);
        Mat dst = new Mat();
        Imgproc.adaptiveThreshold(
                gray,
                dst,
                255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY,
                11,//必须为奇数
                3
        );

        showDst(dst);
    }

    protected void lines() {
        Mat src = cannyMatProperty.get();
        Mat lines = new Mat();
        Imgproc.HoughLinesP(
                src,
                lines,
                1,//像素为单位
                Math.PI / 180,//弧度为单位
                200,
                20,// 最小行长。小于此长度的线段将被拒绝
                5//线段之间允许将它们视为一条线的最大间隙
        );
        System.out.println("霍夫直线：" + lines);


        //在图像上绘制直线
        Mat hfLines = new Mat();
        hfLines.create(src.rows(), src.cols(), CvType.CV_8UC1);

        for (int i = 0; i < lines.rows(); i++) {
            double[] points = lines.get(i, 0);
            double x1, y1, x2, y2;

            x1 = points[0];
            y1 = points[1];
            x2 = points[2];
            y2 = points[3];

            Point pt1 = new Point(x1, y1);
            Point pt2 = new Point(x2, y2);

            //在一副图像上绘制直线
            Imgproc.line(hfLines, pt1, pt2, new Scalar(255, 0, 0), 1);
        }
        showDst(hfLines);
    }

    private void showDst(Mat dst) {
        Image image = ImageToMatUtils.mat2Image(dst);
        targetImageProperty.set(image);
    }
}
