package com.voicecommand;

import com.voicecommand.ui.VoiceCommandUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApplication {
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Java Voice Command Recognition System (GUI)...");

        // Set system look and feel for a better OS-native UI appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warn("Could not set system look and feel");
        }

        SwingUtilities.invokeLater(() -> {
            VoiceCommandUI ui = new VoiceCommandUI();
            ui.setVisible(true);
        });
    }
}
