package com.voicecommand.speech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.TargetDataLine;
import java.io.IOException;

public class SpeechToTextService {
    private static final Logger logger = LoggerFactory.getLogger(SpeechToTextService.class);
    private Model model;
    
    public SpeechToTextService(String modelPath) {
        try {
            // Load Vosk offline model
            logger.info("Loading Vosk model from directory: {}...", modelPath);
            this.model = new Model(modelPath);
            logger.info("Vosk model loaded successfully.");
        } catch (Exception e) {
            logger.error("Failed to load Vosk model. Ensure the model exists at '{}'.", modelPath, e);
            throw new RuntimeException("Model load failed", e);
        }
    }
    
    public String recognizeSpeech(TargetDataLine microphone) throws IOException {
        try (Recognizer recognizer = new Recognizer(model, 16000)) {
            int bytesRead;
            byte[] b = new byte[4096];
            
            logger.debug("Listening for speech chunk...");
            
            // Continuous listening loop chunk by chunk
            while ((bytesRead = microphone.read(b, 0, b.length)) > 0) {
                if (recognizer.acceptWaveForm(b, bytesRead)) {
                    String result = recognizer.getResult();
                    String text = extractTextFromResult(result);
                    if (!text.isEmpty()) {
                        return text;
                    }
                }
            }
        }
        return "";
    }
    
    private String extractTextFromResult(String jsonResult) {
        // Simple JSON parsing to get "text" field
        // Result format: { "text" : "some recognized speech" }
        String match = "\"text\" : \"";
        int start = jsonResult.indexOf(match);
        if (start != -1) {
            start += match.length();
            int end = jsonResult.indexOf("\"", start);
            if (end != -1) {
                return jsonResult.substring(start, end);
            }
        }
        return "";
    }
}
