package com.voicecommand.command;

import com.voicecommand.config.CommandConfig;
import com.voicecommand.model.CommandIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommandInterpreter {
    private static final Logger logger = LoggerFactory.getLogger(CommandInterpreter.class);
    private CommandConfig config;

    public CommandInterpreter(CommandConfig config) {
        this.config = config;
    }

    public List<String> getCommandForIntent(CommandIntent intent) {
        if (intent == CommandIntent.UNKNOWN) {
            System.out.println("Command not recognized");
            return null;
        }

        List<String> commandArgs = config.getSystemCommand(intent.name());
        if (commandArgs == null || commandArgs.isEmpty()) {
            logger.warn("No command mapping found for intent: {}", intent);
            return null;
        }

        return commandArgs;
    }
}
