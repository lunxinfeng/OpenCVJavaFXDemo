package com.lxf;

import com.lxf.app.App;
import javafx.application.Application;
import org.opencv.core.Core;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        OpenCVNativeLoader loader = new OpenCVNativeLoader();
//        loader.init();
        Application.launch(App.class,args);
    }
}
