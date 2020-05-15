#include <opencv2/opencv.hpp>
#include <jni.h>

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imageprocessing(JNIEnv *env, jobject thiz, jlong input_image,
                                             jlong output_image, jint th1, jint th2) {
    // TODO: implement imageprocessing()

    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) output_image;



    cvtColor( img_input, img_output, COLOR_RGB2GRAY);


    blur( img_output, img_output, Size(2,2) );

    Canny( img_output, img_output, th1, th2,3,true);

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

}

extern "C"
JNIEXPORT void JNICALL
Java_com_imageliner_MakeLine_imagebalckwhite(JNIEnv *env, jobject thiz, jlong input_image,
                                             jlong output_image, jint th1, jint th2) {
    // TODO: implement imagebalckwhite()
    Mat &img_input = *(Mat *) input_image;

    Mat &img_output = *(Mat *) input_image;

    for (int x = 0; x < img_output.rows; x++)
    {
        for (int y = 0; y < img_output.cols; y++)
        {
            if (img_output.at<uchar>(x, y) > 10)
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