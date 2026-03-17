package com.voicecommand.ui;

import com.voicecommand.audio.VoiceInputHandler;
import com.voicecommand.command.CommandExecutor;
import com.voicecommand.command.CommandInterpreter;
import com.voicecommand.config.CommandConfig;
import com.voicecommand.model.CommandIntent;
import com.voicecommand.nlp.NLPProcessor;
import com.voicecommand.speech.SpeechToTextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VoiceCommandUI extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(VoiceCommandUI.class);

    private JToggleButton recordButton;
    private JTextArea logArea;
    private JLabel statusLabel;

    private CommandConfig config;
    private VoiceInputHandler voiceHandler;
    private NLPProcessor nlpProcessor;
    private CommandInterpreter commandInterpreter;
    private CommandExecutor commandExecutor;
    private SpeechToTextService sttService;

    private Thread listeningThread;
    private volatile boolean isListening = false;

    public VoiceCommandUI() {
        super("Voice Command Recognition System - GUI");
        initSystem();
        initUI();
    }

    private void initSystem() {
        String voskModelPath = "src/main/resources/models/vosk-model-small-en-us-0.15";
        try {
            config = new CommandConfig();
            voiceHandler = new VoiceInputHandler();
            nlpProcessor = new NLPProcessor();
            commandInterpreter = new CommandInterpreter(config);
            commandExecutor = new CommandExecutor();
            sttService = new SpeechToTextService(voskModelPath);
        } catch (Exception e) {
            logger.error("Failed to initialize system modules.", e);
            JOptionPane.showMessageDialog(this, "Failed to load Vosk model or config. Check logs.", "Initialization Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initUI() {
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        recordButton = new JToggleButton("Start Recording");
        recordButton.setFont(new Font("Arial", Font.BOLD, 18));
        recordButton.setFocusPainted(false);
        recordButton.setBackground(new Color(220, 53, 69)); // Bootstrap Danger Red
        recordButton.setForeground(Color.WHITE);

        recordButton.addActionListener(e -> toggleRecording());

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(logArea);

        statusLabel = new JLabel("Status: Idle");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));

        add(recordButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void toggleRecording() {
        if (recordButton.isSelected()) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        try {
            voiceHandler.startListening();
            isListening = true;
            
            recordButton.setText("Stop Recording");
            recordButton.setBackground(new Color(40, 167, 69)); // Bootstrap Success Green
            statusLabel.setText("Status: Listening...");
            appendLog("Started listening...");

            listeningThread = new Thread(this::listenLoop, "ListeningThread");
            listeningThread.start();
        } catch (Exception e) {
            logger.error("Error starting microphone", e);
            appendLog("Error starting microphone: " + e.getMessage());
            recordButton.setSelected(false);
        }
    }

    private void stopRecording() {
        isListening = false;
        voiceHandler.stopListening();
        
        recordButton.setText("Start Recording");
        recordButton.setBackground(new Color(220, 53, 69));
        statusLabel.setText("Status: Idle");
        appendLog("Stopped listening.");
    }

    private void listenLoop() {
        while (isListening) {
            try {
                String recognizedText = sttService.recognizeSpeech(voiceHandler.getMicrophone());
                
                if (isListening && recognizedText != null && !recognizedText.trim().isEmpty()) {
                    SwingUtilities.invokeLater(() -> appendLog("Recognized: \"" + recognizedText + "\""));
                    if(recognizedText.equals("stop recording")){
                        stopRecording();
                    }

                    CommandIntent intent = nlpProcessor.processText(recognizedText);
                    logger.debug("Detected Intent: {}", intent);

                    if (intent == CommandIntent.UNKNOWN) {
                        SwingUtilities.invokeLater(() -> appendLog("Command not recognized."));
                    } 
            
                    else {
                        List<String> commandArgs = commandInterpreter.getCommandForIntent(intent);
                        if (commandArgs != null) {
                            SwingUtilities.invokeLater(() -> appendLog("Executing intent: " + intent));
                            commandExecutor.executeCommand(commandArgs);
                        }
                    }
                }
            } catch (Exception e) {
                if (isListening) {
                    logger.error("Error during speech recognition", e);
                    SwingUtilities.invokeLater(() -> appendLog("Error during recognition: " + e.getMessage()));
                    break;
                }
            }
        }
    }

    private void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}
