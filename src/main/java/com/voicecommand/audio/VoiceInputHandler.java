package com.voicecommand.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;

public class VoiceInputHandler {
    private static final Logger logger = LoggerFactory.getLogger(VoiceInputHandler.class);
    private TargetDataLine microphone;
    private final int sampleRate = 16000;

    public void startListening() throws LineUnavailableException {
        // Standard PCM_SIGNED, 16000Hz, 16 bit, mono, 2 bytes/frame, little-endian
        AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Microphone not supported at given audio format 16000Hz, 16-bit, mono.");
        }

        microphone = (TargetDataLine) AudioSystem.getLine(info);
        microphone.open(format);
        microphone.start();
        logger.info("Microphone is open and tracking continuous voice input...");
    }

    public TargetDataLine getMicrophone() {
        return microphone;
    }

    public float getSampleRate() {
        return sampleRate;
    }

    public void stopListening() {
        if (microphone != null) {
            microphone.stop();
            microphone.close();
            logger.info("Microphone closed.");
        }
    }
}
