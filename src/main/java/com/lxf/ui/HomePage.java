package com.lxf.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class HomePage extends Pane {
    private final HomeController controller;

    private final Button btnLoadImage = new Button("加载图片");
    private final Button btnGray = new Button("灰化");
    private final Button btnThresholdGlobal = new Button("全局阈值化");
    private final Slider sliderThreshold = new Slider();
    private final Label labelThreshold = new Label();
    private final Button btnContourAfterThreshold = new Button("轮廓（基于二值化）");
    private final Button btnCanny = new Button("边缘检测");
    private final Slider sliderCannyThreshold1 = new Slider();
    private final Slider sliderCannyThreshold2 = new Slider();
    private final Button btnContourAfterCanny = new Button("轮廓（基于边缘检测）");
    private final Button btnRotate = new Button("旋转");
    private final Button btnTransformBy3Point = new Button("3点变换（仿射变换）");
    private final Button btnTransformBy4Point = new Button("4点变换（透视变换）");
    private final Button btnThresholdAuto = new Button("自适应阈值化");
    private final Button btnLines = new Button("霍夫直线");
    private final Button btnYoloData = new Button("YoloData");
    private final ImageView imageSrc = new ImageView();
    private final ImageView imageDst = new ImageView();

    public HomePage(HomeController controller) {
        this.controller = controller;

        getChildren().addAll(
                btnLoadImage,
                btnGray,
                btnThresholdGlobal,
                sliderThreshold,
                labelThreshold,
                btnContourAfterThreshold,
                btnCanny,
                sliderCannyThreshold1,
                sliderCannyThreshold2,
                btnContourAfterCanny,
                btnRotate,
                btnTransformBy3Point,
                btnTransformBy4Point,
                btnThresholdAuto,
                btnLines,
                btnYoloData,
                imageSrc,
                imageDst
        );

        data();
        event();
        ui();
    }

    private void data() {
        controller.imageSrcProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                imageSrc.setImage(new Image(newValue));
        });
        controller.targetImageProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                imageDst.setImage(newValue);
        });
        controller.imageSrcRatioProperty.addListener((observable, oldValue, newValue) -> {
            imageSrc.fitWidthProperty().bind(imageSrc.fitHeightProperty().multiply(newValue.doubleValue()));//比例800*480
        });

        sliderThreshold.setMax(255.0);
        sliderThreshold.setValue(80.0);
        labelThreshold.textProperty().bind(sliderThreshold.valueProperty().asString());

        sliderCannyThreshold1.setMax(255.0);
        sliderCannyThreshold1.setValue(60.0);
        sliderCannyThreshold2.setMax(255.0);
        sliderCannyThreshold2.setValue(180.0);
    }

    private void event() {
        btnLoadImage.setOnAction(event -> controller.loadImage());
        imageSrc.setOnMouseClicked(event -> {
            if (event.isAltDown()) {
                double imageWidth = imageSrc.getImage().getWidth();
                double imageHeight = imageSrc.getImage().getHeight();

                double imageViewWidth = imageSrc.getFitWidth();
                double imageViewHeight = imageSrc.getFitHeight();

                System.out.println((event.getSceneX() - imageSrc.getLayoutX()) * imageWidth / imageViewWidth);
                System.out.println((event.getSceneY() - imageSrc.getLayoutY()) * imageHeight / imageViewHeight);
            } else
                controller.nextImage();
        });
        btnGray.setOnAction(event -> controller.gray());
        btnThresholdGlobal.setOnAction(event -> controller.threshold(sliderThreshold.getValue()));
        sliderThreshold.valueProperty().addListener((observable, oldValue, newValue) -> btnThresholdGlobal.fire());
        btnContourAfterThreshold.setOnAction(event -> controller.contourAfterThreshold());
        btnCanny.setOnAction(event -> controller.canny(sliderCannyThreshold1.getValue(), sliderCannyThreshold2.getValue()));
        sliderCannyThreshold1.valueProperty().addListener((observable, oldValue, newValue) -> btnCanny.fire());
        sliderCannyThreshold2.valueProperty().addListener((observable, oldValue, newValue) -> btnCanny.fire());
        btnContourAfterCanny.setOnAction(event -> controller.contourAfterCanny());
        btnRotate.setOnAction(event -> controller.rotate());
        btnTransformBy3Point.setOnAction(event -> controller.transformBy3Point());
        btnTransformBy4Point.setOnAction(event -> controller.transformBy4Point());
        btnThresholdAuto.setOnAction(event -> controller.adaptiveThreshold());
        btnLines.setOnAction(event -> controller.lines());
        btnYoloData.setOnAction(event -> controller.getBoardGrid());
    }

    private void ui() {

        btnGray.disableProperty().bind(controller.imageSrcProperty.isNull());
        btnThresholdGlobal.disableProperty().bind(controller.imageSrcProperty.isNull());
        sliderThreshold.disableProperty().bind(controller.imageSrcProperty.isNull());
        btnContourAfterThreshold.disableProperty().bind(controller.thresholdMatProperty.isNull());
        btnCanny.disableProperty().bind(controller.imageSrcProperty.isNull());
        btnContourAfterCanny.disableProperty().bind(controller.cannyMatProperty.isNull());
        btnRotate.disableProperty().bind(controller.imageSrcProperty.isNull());
        btnTransformBy3Point.disableProperty().bind(controller.imageSrcProperty.isNull());
        btnTransformBy4Point.disableProperty().bind(controller.imageSrcProperty.isNull());
        btnThresholdAuto.disableProperty().bind(controller.imageSrcProperty.isNull());
        btnLines.disableProperty().bind(controller.cannyMatProperty.isNull());
        btnYoloData.disableProperty().bind(controller.imageSrcProperty.isNull());

        btnGray.layoutYProperty().bind(btnLoadImage.layoutYProperty().add(btnLoadImage.heightProperty()).add(10));

        btnThresholdGlobal.layoutYProperty().bind(btnGray.layoutYProperty().add(btnGray.heightProperty()).add(10));
        sliderThreshold.layoutXProperty().bind(btnThresholdGlobal.layoutXProperty().add(btnThresholdGlobal.widthProperty()).add(10));
        sliderThreshold.layoutYProperty().bind(btnThresholdGlobal.layoutYProperty().add(btnThresholdGlobal.heightProperty().subtract(sliderThreshold.heightProperty()).divide(2)));
        labelThreshold.layoutXProperty().bind(sliderThreshold.layoutXProperty().add(sliderThreshold.widthProperty().add(10)));
        labelThreshold.layoutYProperty().bind(sliderThreshold.layoutYProperty().add(sliderThreshold.heightProperty().subtract(labelThreshold.heightProperty()).divide(2)));

        btnContourAfterThreshold.layoutYProperty().bind(btnThresholdGlobal.layoutYProperty().add(btnThresholdGlobal.heightProperty()).add(10));

        btnCanny.layoutYProperty().bind(btnContourAfterThreshold.layoutYProperty().add(btnContourAfterThreshold.heightProperty()).add(10));
        sliderCannyThreshold1.layoutXProperty().bind(btnCanny.layoutXProperty().add(btnCanny.widthProperty()).add(10));
        sliderCannyThreshold1.layoutYProperty().bind(btnCanny.layoutYProperty().add(btnCanny.heightProperty().subtract(sliderCannyThreshold1.heightProperty()).divide(2)));
        sliderCannyThreshold2.layoutXProperty().bind(sliderCannyThreshold1.layoutXProperty().add(sliderCannyThreshold1.widthProperty()).add(10));
        sliderCannyThreshold2.layoutYProperty().bind(sliderCannyThreshold1.layoutYProperty());

        btnContourAfterCanny.layoutYProperty().bind(btnCanny.layoutYProperty().add(btnCanny.heightProperty()).add(10));

        btnRotate.layoutYProperty().bind(btnContourAfterCanny.layoutYProperty().add(btnCanny.heightProperty()).add(10));

        btnTransformBy3Point.layoutYProperty().bind(btnRotate.layoutYProperty().add(btnCanny.heightProperty()).add(10));
        btnTransformBy4Point.layoutYProperty().bind(btnTransformBy3Point.layoutYProperty().add(btnCanny.heightProperty()).add(10));
        btnThresholdAuto.layoutYProperty().bind(btnTransformBy4Point.layoutYProperty().add(btnCanny.heightProperty()).add(10));
        btnLines.layoutYProperty().bind(btnThresholdAuto.layoutYProperty().add(btnCanny.heightProperty()).add(10));
        btnYoloData.layoutYProperty().bind(btnLines.layoutYProperty().add(btnCanny.heightProperty()).add(10));

        imageSrc.fitHeightProperty().bind(this.heightProperty().multiply(0.8));
//        imageSrc.fitWidthProperty().bind(imageSrc.fitHeightProperty().multiply(480).divide(800));//比例800*480
        imageSrc.layoutXProperty().bind(this.widthProperty().subtract(imageSrc.fitWidthProperty()));//靠右
        imageSrc.layoutYProperty().bind(this.heightProperty().subtract(imageSrc.fitHeightProperty()).divide(2));//居中

        imageDst.fitWidthProperty().bind(imageSrc.fitWidthProperty());//大小同imageSrc
        imageDst.fitHeightProperty().bind(imageSrc.fitHeightProperty());
        imageDst.layoutXProperty().bind(imageSrc.layoutXProperty().subtract(imageDst.fitWidthProperty()));//在imageSrc左边
        imageDst.layoutYProperty().bind(imageSrc.layoutYProperty());//与imageSrc等高
    }
}
