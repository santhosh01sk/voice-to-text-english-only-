# Java Voice Command Recognition System

## Project Overview
This is a Java-based Voice Command Recognition System that accepts continuous voice input from the microphone, converts speech to text using **Vosk**, processes the text with **Apache OpenNLP** to detect user intent, and executes corresponding system commands.

## Architecture & Modules
1. **`VoiceInputHandler`**: Audio capture from microphone using standard Java Sound API `javax.sound.sampled`.
2. **`SpeechToTextService`**: Local, offline speech-to-text using Vosk.
3. **`NLPProcessor`**: Uses OpenNLP for tokenization and keyword extraction to identify user intention.
4. **`CommandInterpreter`**: Maps identified intents from NLP string rules to system commands.
5. **`CommandExecutor`**: Executes system processes using `ProcessBuilder`.
6. **`MainApplication`**: Ties all modules together for a continuous listening application loop.

## Setup Instructions

### Prerequisites
- **Java 17+** Installed and configured.
- **Apache Maven** installed (if running from the command line).
- **Microphone** correctly set up on your machine.

### Acquiring NLP and Speech Models
This project requires a pre-trained offline Speech Recognition Model (Vosk).
1. Go to the [Vosk Models Page](https://alphacephei.com/vosk/models).
2. Download `vosk-model-small-en-us-0.15` (Lightweight English model, ~40MB).
3. Extract the downloaded `.zip` file.
4. Move the extracted folder to `src/main/resources/models/vosk-model-small-en-us-0.15` relative to the project directory.

*(Note: The `MainApplication.java` explicitly expects the model to be located at this path. You can edit the path if needed).*

### Configuration
You can easily add new commands or change existing ones in the `src/main/resources/commands.json` file.
```json
{
  "OPEN_BROWSER": ["cmd", "/c", "start", "msedge"],
  "OPEN_CALCULATOR": ["calc.exe"],
  "SHUTDOWN_SYSTEM": ["shutdown", "-s", "-t", "0"],
  "OPEN_NOTEPAD": ["notepad.exe"]
}
```

### Build and Run
1. Navigate to the project root directory.
2. Compile and package the project:
   ```bash
   mvn clean package
   ```
3. Run the compiled application through your IDE or using the Maven Exec plugin:
   ```bash
   mvn exec:java -Dexec.mainClass="com.voicecommand.MainApplication"
   ```

## Example Usage
- **User Says:** *"Please open the browser"*
- **System Translates:** Intent `OPEN_BROWSER` -> Edge/Chrome opens.
- **User Says:** *"Can you start calculator"*
- **System Translates:** Intent `OPEN_CALCULATOR` -> Calculator opens.
- **User Says:** *"Do a random thing"*
- **System Translates:** Intent `UNKNOWN` -> Console prints `"Command not recognized"`.
