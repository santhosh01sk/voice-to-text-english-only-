package com.voicecommand.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class CommandExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CommandExecutor.class);

    public void executeCommand(List<String> commandArgs) {
        if (commandArgs == null || commandArgs.isEmpty()) {
            return;
        }

        try {
            logger.info("Executing system command: {}", String.join(" ", commandArgs));
            ProcessBuilder processBuilder = new ProcessBuilder(commandArgs);
            processBuilder.start();
            logger.info("Command executed successfully.");
        } catch (IOException e) {
            logger.error("Failed to execute command: {}", commandArgs, e);
        }
    }
}
