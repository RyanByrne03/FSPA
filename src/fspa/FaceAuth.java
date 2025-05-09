/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fspa;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

/**
 *
 * @author RyanByrne
 */
public class FaceAuth {

    private static final String FACE_DATA_DIR = "facedata/";
    private static final String CASCADE_PATH = "src/fspa/haarcascade_frontalface_default.xml";

    public static void enrollUser() {
        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.out.println("Cannot access the webcam.");
            return;
        }

        CascadeClassifier faceDetector = new CascadeClassifier(CASCADE_PATH);
        Mat frame = new Mat();
        int imageCount = 0;

        new File(FACE_DATA_DIR).mkdirs();
        System.out.println("Look at the camera to enroll your face...");

        while (imageCount < 30) {
            if (!camera.read(frame)) {
                continue;
            }

            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(frame, faces);

            for (int i = 0; i < faces.size(); i++) {
                Rect rect = faces.get(i);
                Mat face = new Mat(frame, rect);
                opencv_imgproc.resize(face, face, new Size(200, 200));
                opencv_imgproc.cvtColor(face, face, opencv_imgproc.COLOR_BGR2GRAY);
                opencv_imgproc.equalizeHist(face, face);

                String filename = FACE_DATA_DIR + "face" + imageCount + ".png";
                opencv_imgcodecs.imwrite(filename, face);
                System.out.println("Captured " + filename);
                imageCount++;

                if (imageCount >= 30) {
                    break;
                }
            }
        }

        camera.release();
        System.out.println("Enrollment complete.");
    }

public static boolean authenticateUser() {
    File folder = new File("facedata");
    File[] files = folder.listFiles((dir, name) -> name.endsWith(".png"));
    if (files == null || files.length == 0) {
        System.out.println("No face data found.");
        return false;
    }

    List<Mat> images = new ArrayList<>();
    List<Integer> labels = new ArrayList<>();

    for (File file : files) {
        Mat img = opencv_imgcodecs.imread(file.getAbsolutePath(), opencv_imgcodecs.IMREAD_GRAYSCALE);
        if (!img.empty()) {
            images.add(img);
            labels.add(1); // Single user = label 1
        }
    }

    // Prepare training data
    int[] labelArray = labels.stream().mapToInt(i -> i).toArray();
    IntPointer labelPointer = new IntPointer(labelArray);
    Mat labelMat = new Mat(labels.size(), 1, opencv_core.CV_32SC1, labelPointer);
    MatVector imageVector = new MatVector(images.toArray(new Mat[0]));

    LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
    recognizer.train(imageVector, labelMat);

    VideoCapture camera = new VideoCapture(0);
    if (!camera.isOpened()) {
        System.out.println("Cannot access camera.");
        return false;
    }

    CascadeClassifier faceDetector = new CascadeClassifier("src/fspa/haarcascade_frontalface_default.xml");
    Mat frame = new Mat();

    long start = System.currentTimeMillis();
    System.out.println("Authenticating...");

    while (System.currentTimeMillis() - start < 5000) {
        if (!camera.read(frame)) continue;

        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(frame, faces);

        for (int i = 0; i < faces.size(); i++) {
            Rect rect = faces.get(i);
            Mat face = new Mat(frame, rect);
            opencv_imgproc.resize(face, face, new Size(200, 200));
            opencv_imgproc.cvtColor(face, face, opencv_imgproc.COLOR_BGR2GRAY);
            opencv_imgproc.equalizeHist(face, face);

            IntPointer predictedLabel = new IntPointer(1);
            DoublePointer confidence = new DoublePointer(1);
            recognizer.predict(face, predictedLabel, confidence);

            System.out.printf("Predicted label: %d, confidence: %.2f%n", predictedLabel.get(0), confidence.get(0));

            if (predictedLabel.get(0) == 1 && confidence.get(0) < 45.0) {
                camera.release();
                System.out.println("Face recognized.");
                return true;
            }
        }
    }

    camera.release();
    System.out.println("Face not recognized.");
    return false;
}
}
