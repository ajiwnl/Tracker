package com.araneta.mood_tracker;

import android.content.Context;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteEmotionDetector {

    private Interpreter interpreter;

    public TFLiteEmotionDetector(Context context) throws IOException {
        interpreter = new Interpreter(loadModelFile(context, "bert_go_emotion_modelv3.tflite"));
    }

    // Load the model file from assets
    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        FileInputStream inputStream = new FileInputStream(context.getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelPath).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelPath).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Run inference (pass in the input as ByteBuffer)
    public float[] classifyEmotion(ByteBuffer inputBuffer) {
        float[][] output = new float[1][28]; // Output is 28 emotion classes
        interpreter.run(inputBuffer, output);
        return output[0]; // Return the output probabilities
    }

    public void close() {
        interpreter.close(); // Release resources when done, for push
    }
}

