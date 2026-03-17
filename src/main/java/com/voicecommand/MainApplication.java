package com.voicecommand;

import com.voicecommand.audio.VoiceInputHandler;
import com.voicecommand.command.CommandExecutor;
import com.voicecommand.command.CommandInterpreter;
import com.voicecommand.config.CommandConfig;
import com.voicecommand.model.CommandIntent;
import com.voicecommand.nlp.NLPProcessor;
import com.voicecommand.speech.SpeechToTextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MainApplication {
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Java Voice Command Recognition System...");

        // Ensure you download the Vosk model from https://alphacephei.com/vosk/models
        // and extract it into a directory. Provide the path below.
        String voskModelPath = "src/main/resources/models/vosk-model-small-en-us-0.15";

        try {
            CommandConfig config = new CommandConfig();
            VoiceInputHandler voiceHandler = new VoiceInputHandler();
            NLPProcessor nlpProcessor = new NLPProcessor();
            CommandInterpreter commandInterpreter = new CommandInterpreter(config);
            CommandExecutor commandExecutor = new CommandExecutor();
            
            // Initialization can fail if the model path is incorrect.
            SpeechToTextService sttService = new SpeechToTextService(voskModelPath);

            voiceHandler.startListening();

            logger.info("System is ready. Speak your voice commands. Press Ctrl+C to exit.");
            System.out.println("--- VOICE COMMAND SYSTEM STARTED ---");

            while (true) {
                // 1. Capture and convert speech to text
                String recognizedText = sttService.recognizeSpeech(voiceHandler.getMicrophone());
                
                if (recognizedText != null && !recognizedText.trim().isEmpty()) {
                    System.out.println("\nRecognized Speech: " + recognizedText);

                    // 2. Process text with NLP
                    CommandIntent intent = nlpProcessor.processText(recognizedText);
                    logger.debug("Detected Intent: {}", intent);

                    // 3. Map Intent to System Commands
                    List<String> commandArgs = commandInterpreter.getCommandForIntent(intent);

                    // 4. Execute the command
                    commandExecutor.executeCommand(commandArgs);
                }
            }
        } catch (Exception e) {
            logger.error("An error occurred in the MainApplication: ", e);
        }
    }
}
