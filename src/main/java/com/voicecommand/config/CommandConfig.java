package com.voicecommand.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandConfig {
    private static final Logger logger = LoggerFactory.getLogger(CommandConfig.class);
    private Map<String, List<String>> commandMap = new HashMap<>();

    public CommandConfig() {
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("commands.json")) {
            if (inputStream == null) {
                logger.error("commands.json not found in resources folder.");
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            commandMap = mapper.readValue(inputStream, new TypeReference<Map<String, List<String>>>() {});
            logger.info("Commands configuration loaded successfully: {} commands mapped.", commandMap.size());
        } catch (Exception e) {
            logger.error("Failed to load command configuration", e);
        }
    }

    public List<String> getSystemCommand(String intentName) {
        return commandMap.get(intentName);
    }
}
