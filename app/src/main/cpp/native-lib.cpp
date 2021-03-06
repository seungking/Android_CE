#include <opencv2/opencv.hpp>
#include <jni.h>

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeLine_imageprocessing1(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing()

    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;



//    cvtColor( img_input, img_output, COLOR_RGB2GRAY);


//    blur( img_output, img_output, Size(2,2) );
//    GaussianBlur(img_output, img_output, Size(2,2), 0);
//    bilateralFilter(img_input,img_output,7,150,150);

//    cvtColor( img_output, img_output, COLOR_RGB2GRAY);

//    Canny( img_output, img_output, th1, th2,3,true);
//    Sobel(img_output, img_output, -1, 0, 1, 3, 2.0, 127.0);
//    Sobel(img_output, img_output, -1, 0, 1, 3, 1.0, 0.0, BORDER_DEFAULT);
//      Scharr(img_output, img_output, );
//      Laplacian(img_output, img_output)
//      Scha
//    for (int x = 0; x < img_output.rows; x++)
//    {
//        for (int y = 0; y < img_output.cols; y++)
//        {
//            if (img_output.at<uchar>(x, y) <20)
//            {
//                img_output.at<uchar>(x,y) = 255;
//            }
//            else
//            {
//                img_output.at<uchar>(x,y) = 0;
//            }
//        }
//    }
///////////////////////////////////////////////////////////////////////////////
    cv::Mat src, src_gray, src_result;
    src = *(Mat *) input_image;
    cv::Mat grad;
    double scale = 7;
//    double scale = th1 * 0.35;
    double delta = 5;
//    double delta = th2;
    int ddepth = CV_16S;

    cv::GaussianBlur( src, src_gray, Size(3,3), 0, 0, BORDER_DEFAULT );

    /// Convert it to gray
    cv::cvtColor( src_gray, src_result, COLOR_RGB2GRAY );

    /// Generate grad_x and grad_y
    cv::Mat grad_x, grad_y;
    cv::Mat abs_grad_x, abs_grad_y;

    /// Gradient X
    cv::Sobel( src_result, grad_x, ddepth, 1, 0, 3, scale, delta, BORDER_DEFAULT );

    cv::convertScaleAbs( grad_x, abs_grad_x );

    /// Gradient Y
    cv::Sobel( src_result, grad_y, ddepth, 0, 1, 3, scale, delta, BORDER_DEFAULT );
    cv::convertScaleAbs( grad_y, abs_grad_y );
    cv::addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad );

    img_output = grad;

    /////////////////////////////////////////////////////////////////////////


}

extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeLine_imagebalckwhite1(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imagebalckwhite()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) input_image;

    for (int x = 0; x < img_output.rows; x++)
    {
        for (int y = 0; y < img_output.cols; y++)
        {
            if (img_output.at<uchar>(x, y) > 50)
            {
                img_output.at<uchar>(x,y) = 0;
            }
            else
            {
                img_output.at<uchar>(x,y) = 255;
            }
        }
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeLine_imageprocessing2(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing2()

    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;

//    cv::GaussianBlur( img_input, img_output, Size(7,7), 0, 0, BORDER_DEFAULT );
//
//    /// Convert it to gray
//    cv::cvtColor( img_output, img_output, COLOR_RGB2GRAY );

    cvtColor( img_input, img_output, COLOR_RGB2GRAY);

    blur( img_output, img_output, Size(1,1) );
    Canny( img_output, img_output, 200, 200,3,false);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeLine_imagebalckwhite2(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imagebalckwhite2()


    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) input_image;

    for (int x = 0; x < img_output.rows; x++)
    {
        for (int y = 0; y < img_output.cols; y++)
        {
            if (img_output.at<uchar>(x, y) > 20)
            {
                img_output.at<uchar>(x,y) = 0;
            }
            else
            {
                img_output.at<uchar>(x,y) = 255;
            }
        }
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeLine_imageprocessing3(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing3()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;

    cv::Mat src, src_gray, src_result;
    src = *(Mat *) input_image;
    cv::Mat grad;
    double scale = 3;
//    double scale = th1 * 0.35;
    double delta = 5;
//    double delta = th2;
    int ddepth = CV_16S;

    cv::GaussianBlur( src, src_gray, Size(5,5), 0, 0, BORDER_DEFAULT );

    /// Convert it to gray
    cv::cvtColor( src_gray, src_result, COLOR_RGB2GRAY );

    /// Generate grad_x and grad_y
    cv::Mat grad_x, grad_y;
    cv::Mat abs_grad_x, abs_grad_y;

    /// Gradient X
    cv::Sobel( src_result, grad_x, ddepth, 1, 0, 3, scale, delta, BORDER_DEFAULT );

    cv::convertScaleAbs( grad_x, abs_grad_x );

    /// Gradient Y
    cv::Sobel( src_result, grad_y, ddepth, 0, 1, 3, scale, delta, BORDER_DEFAULT );
    cv::convertScaleAbs( grad_y, abs_grad_y );
    cv::addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad );

    img_output = grad;

    /////////////////////////////////////////////////////////////////////////

}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeLine_imagebalckwhite3(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imagebalckwhite3()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) input_image;

    for (int x = 0; x < img_output.rows; x++)
    {
        for (int y = 0; y < img_output.cols; y++)
        {
            if (img_output.at<uchar>(x, y) > 50)
            {
                img_output.at<uchar>(x,y) = 0;
            }
            else
            {
                img_output.at<uchar>(x,y) = 255;
            }
        }
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeLine_imageprocessing4(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing()

    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;

//    cv::GaussianBlur( img_input, img_output, Size(7,7), 0, 0, BORDER_DEFAULT );
//
//    /// Convert it to gray
//    cv::cvtColor( img_output, img_output, COLOR_RGB2GRAY );

    cvtColor( img_input, img_output, COLOR_RGB2GRAY);

    blur( img_output, img_output, Size(1,1) );
    Canny( img_output, img_output, 57, 41, 3, false);
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeLine_imagebalckwhite4(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imagebalckwhite4()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) input_image;

    for (int x = 0; x < img_output.rows; x++)
    {
        for (int y = 0; y < img_output.cols; y++)
        {
            if (img_output.at<uchar>(x, y) > 5)
            {
                img_output.at<uchar>(x,y) = 0;
            }
            else
            {
                img_output.at<uchar>(x,y) = 255;
            }
        }
    }
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeColor_00024MainActivity_imageprocessing(JNIEnv *env, jobject thiz,
                                                                jlong input_image,
                                                                jlong output_image, jint th1,
                                                                jint th2) {
    // TODO: implement imageprocessing()

    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;

//    cv::GaussianBlur( img_input, img_output, Size(7,7), 0, 0, BORDER_DEFAULT );
//
//    /// Convert it to gray
//    cv::cvtColor( img_output, img_output, COLOR_RGB2GRAY );

    cvtColor( img_input, img_output, COLOR_RGB2GRAY);

    blur( img_output, img_output, Size(5,5) );
    Canny( img_output, img_output, th1, th2);
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_activities_MakeColor_imageprocessing(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;

//    cv::GaussianBlur( img_input, img_output, Size(7,7), 0, 0, BORDER_DEFAULT );
//
//    /// Convert it to gray
//    cv::cvtColor( img_output, img_output, COLOR_RGB2GRAY );

    cvtColor( img_input, img_output, COLOR_RGB2GRAY);

    blur( img_output, img_output, Size(1,1) );
    Canny( img_output, img_output, th1, th2, 3, false);
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imageprocessing1(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing1()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;



//    cvtColor( img_input, img_output, COLOR_RGB2GRAY);


//    blur( img_output, img_output, Size(2,2) );
//    GaussianBlur(img_output, img_output, Size(2,2), 0);
//    bilateralFilter(img_input,img_output,7,150,150);

//    cvtColor( img_output, img_output, COLOR_RGB2GRAY);

//    Canny( img_output, img_output, th1, th2,3,true);
//    Sobel(img_output, img_output, -1, 0, 1, 3, 2.0, 127.0);
//    Sobel(img_output, img_output, -1, 0, 1, 3, 1.0, 0.0, BORDER_DEFAULT);
//      Scharr(img_output, img_output, );
//      Laplacian(img_output, img_output)
//      Scha
//    for (int x = 0; x < img_output.rows; x++)
//    {
//        for (int y = 0; y < img_output.cols; y++)
//        {
//            if (img_output.at<uchar>(x, y) <20)
//            {
//                img_output.at<uchar>(x,y) = 255;
//            }
//            else
//            {
//                img_output.at<uchar>(x,y) = 0;
//            }
//        }
//    }
///////////////////////////////////////////////////////////////////////////////
    cv::Mat src, src_gray, src_result;
    src = *(Mat *) input_image;
    cv::Mat grad;
    double scale = 7;
//    double scale = th1 * 0.35;
    double delta = 5;
//    double delta = th2;
    int ddepth = CV_16S;

    cv::GaussianBlur( src, src_gray, Size(3,3), 0, 0, BORDER_DEFAULT );

    /// Convert it to gray
    cv::cvtColor( src_gray, src_result, COLOR_RGB2GRAY );

    /// Generate grad_x and grad_y
    cv::Mat grad_x, grad_y;
    cv::Mat abs_grad_x, abs_grad_y;

    /// Gradient X
    cv::Sobel( src_result, grad_x, ddepth, 1, 0, 3, scale, delta, BORDER_DEFAULT );

    cv::convertScaleAbs( grad_x, abs_grad_x );

    /// Gradient Y
    cv::Sobel( src_result, grad_y, ddepth, 0, 1, 3, scale, delta, BORDER_DEFAULT );
    cv::convertScaleAbs( grad_y, abs_grad_y );
    cv::addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad );

    img_output = grad;

    /////////////////////////////////////////////////////////////////////////
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imagebalckwhite1(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imagebalckwhite1()
    // TODO: implement imagebalckwhite()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) input_image;

    for (int x = 0; x < img_output.rows; x++)
    {
        for (int y = 0; y < img_output.cols; y++)
        {
            if (img_output.at<uchar>(x, y) > 50)
            {
                img_output.at<uchar>(x,y) = 0;
            }
            else
            {
                img_output.at<uchar>(x,y) = 255;
            }
        }
    }
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imageprocessing2(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing2()
    // TODO: implement imageprocessing2()

    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;

//    cv::GaussianBlur( img_input, img_output, Size(7,7), 0, 0, BORDER_DEFAULT );
//
//    /// Convert it to gray
//    cv::cvtColor( img_output, img_output, COLOR_RGB2GRAY );

    cvtColor( img_input, img_output, COLOR_RGB2GRAY);

    blur( img_output, img_output, Size(1,1) );
    Canny( img_output, img_output, 200, 200,3,false);
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imagebalckwhite2(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imagebalckwhite2()
    // TODO: implement imagebalckwhite2()


    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) input_image;

    for (int x = 0; x < img_output.rows; x++)
    {
        for (int y = 0; y < img_output.cols; y++)
        {
            if (img_output.at<uchar>(x, y) > 20)
            {
                img_output.at<uchar>(x,y) = 0;
            }
            else
            {
                img_output.at<uchar>(x,y) = 255;
            }
        }
    }
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imageprocessing3(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing3()
    // TODO: implement imageprocessing3()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;

    cv::Mat src, src_gray, src_result;
    src = *(Mat *) input_image;
    cv::Mat grad;
    double scale = 3;
//    double scale = th1 * 0.35;
    double delta = 5;
//    double delta = th2;
    int ddepth = CV_16S;

    cv::GaussianBlur( src, src_gray, Size(5,5), 0, 0, BORDER_DEFAULT );

    /// Convert it to gray
    cv::cvtColor( src_gray, src_result, COLOR_RGB2GRAY );

    /// Generate grad_x and grad_y
    cv::Mat grad_x, grad_y;
    cv::Mat abs_grad_x, abs_grad_y;

    /// Gradient X
    cv::Sobel( src_result, grad_x, ddepth, 1, 0, 3, scale, delta, BORDER_DEFAULT );

    cv::convertScaleAbs( grad_x, abs_grad_x );

    /// Gradient Y
    cv::Sobel( src_result, grad_y, ddepth, 0, 1, 3, scale, delta, BORDER_DEFAULT );
    cv::convertScaleAbs( grad_y, abs_grad_y );
    cv::addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad );

    img_output = grad;

    /////////////////////////////////////////////////////////////////////////

}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imagebalckwhite3(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imagebalckwhite3()
    // TODO: implement imagebalckwhite3()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) input_image;

    for (int x = 0; x < img_output.rows; x++)
    {
        for (int y = 0; y < img_output.cols; y++)
        {
            if (img_output.at<uchar>(x, y) > 50)
            {
                img_output.at<uchar>(x,y) = 0;
            }
            else
            {
                img_output.at<uchar>(x,y) = 255;
            }
        }
    }
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imageprocessing4(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing4()
    // TODO: implement imageprocessing()

    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;

//    cv::GaussianBlur( img_input, img_output, Size(7,7), 0, 0, BORDER_DEFAULT );
//
//    /// Convert it to gray
//    cv::cvtColor( img_output, img_output, COLOR_RGB2GRAY );

    cvtColor( img_input, img_output, COLOR_RGB2GRAY);

    blur( img_output, img_output, Size(1,1) );
    Canny( img_output, img_output, 57, 41, 3, false);
}extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imagebalckwhite4(JNIEnv *env, jobject thiz, jlong input_image,
                                              jlong output_image, jint th1, jint th2) {
    // TODO: implement imagebalckwhite4()
    // TODO: implement imagebalckwhite4()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) input_image;

    for (int x = 0; x < img_output.rows; x++)
    {
        for (int y = 0; y < img_output.cols; y++)
        {
            if (img_output.at<uchar>(x, y) > 5)
            {
                img_output.at<uchar>(x,y) = 0;
            }
            else
            {
                img_output.at<uchar>(x,y) = 255;
            }
        }
    }
}