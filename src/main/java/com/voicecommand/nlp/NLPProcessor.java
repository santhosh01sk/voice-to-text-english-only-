package com.voicecommand.nlp;

import com.voicecommand.model.CommandIntent;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class NLPProcessor {
    private static final Logger logger = LoggerFactory.getLogger(NLPProcessor.class);
    private SimpleTokenizer tokenizer;

    public NLPProcessor() {
        // Initialize simple tokenizer from OpenNLP
        tokenizer = SimpleTokenizer.INSTANCE;
    }

    public CommandIntent processText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return CommandIntent.UNKNOWN;
        }

        // Tokenization using OpenNLP
        String[] tokensArray = tokenizer.tokenize(text.toLowerCase());
        List<String> tokens = Arrays.asList(tokensArray);
        logger.debug("Tokenized input: {}", tokens);

        // Intent detection / Keyword extraction
        if (tokens.contains("shutdown") || (tokens.contains("turn") && tokens.contains("off"))) {
            return CommandIntent.SHUTDOWN_SYSTEM;
        }
        
        if (tokens.contains("open") || tokens.contains("start") || tokens.contains("launch") || tokens.contains("run")) {
            if (tokens.contains("browser") || tokens.contains("chrome") || tokens.contains("edge")) {
                return CommandIntent.OPEN_BROWSER;
            } else if (tokens.contains("calculator") || tokens.contains("calc")) {
                return CommandIntent.OPEN_CALCULATOR;
            } else if (tokens.contains("notepad") || tokens.contains("notes")) {
                return CommandIntent.OPEN_NOTEPAD;
            }
        }
        
        return CommandIntent.UNKNOWN;
    }
}
