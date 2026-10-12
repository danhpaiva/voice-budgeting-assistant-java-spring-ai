package one.digitalinnovation.budgeting.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller para processamento de voz com IA.
 * 
 * RESPONSABILIDADES:
 * 1. Receber áudio em formato multipart/form-data
 * 2. Transcrever áudio para texto usando Whisper (OpenAI)
 * 3. Chamar ChatModel com prompts
 * 4. Gerar áudio de resposta com TTS (Text-to-Speech)
 * 5. Retornar áudio ou resposta em JSON
 * 
 * ETAPA 8 - IMPLEMENTAÇÃO SIMPLIFICADA:
 * Por enquanto, focamos em:
 * - /ai/chat → Chat endpoint com ChatModel
 * - Placeholder para /ai/voice, /ai/transcribe, /ai/speak
 * 
 * @author Voice Budgeting Team
 * @version 1.0
 */
@RestController
@RequestMapping("/ai")
@Tag(name = "AI Voice", description = "Voice processing with AI chatbot")
public class AiVoiceController {
    
    /**
     * ChatModel (optional - injetado apenas se disponível).
     * Responsável por comunicar com OpenAI.
     * 
     * Utiliza Optional para evitar falha de inicialização se a API key não estiver configurada.
     */
    private final Optional<ChatModel> chatModel;
    
    /**
     * Modelo de voz TTS a usar.
     * Configurável via application.properties (default: tts-1)
     */
    @Value("${spring.ai.openai.audio.speech.model:tts-1}")
    private String ttsModel;
    
    /**
     * Voz do TTS (alloy, echo, fable, onyx, nova, shimmer).
     * Configurável via application.properties (default: alloy)
     */
    @Value("${spring.ai.openai.audio.speech.voice:alloy}")
    private String ttsVoice;
    
    /**
     * Chave da API OpenAI.
     * Necessária para fazer requests diretos à OpenAI API (Whisper, TTS).
     */
    @Value("${spring.ai.openai.api-key:}")
    private String openAiApiKey;
    
    /**
     * Constructor Injection com Optional para ChatModel.
     * 
     * @param chatModel ChatModel optional (pode não estar disponível se API key não estiver configurada)
     */
    public AiVoiceController(@Autowired(required = false) ChatModel chatModel) {
        this.chatModel = Optional.ofNullable(chatModel);
    }
    
    /**
     * ENDPOINT 1: Processa áudio completo (Transcribe → Chat → TTS).
     * 
     * PLACEHOLDER para Etapa 8.
     * 
     * @param audioFile Arquivo de áudio (multipart/form-data)
     * @return MP3 binário da resposta de voz (audio/mpeg)
     * 
     * @throws IOException Se houver erro ao processar áudio
     * @throws IllegalArgumentException Se arquivo estiver vazio ou inválido
     */
    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Process voice command (Placeholder)", description = "Upload audio → Transcribe → Chat → TTS response")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "MP3 audio response"),
        @ApiResponse(responseCode = "400", description = "Invalid audio file"),
        @ApiResponse(responseCode = "501", description = "Not implemented yet")
    })
    public ResponseEntity<?> processVoice(
            @Parameter(description = "Audio file (MP3, WAV, M4A, etc)", required = true)
            @RequestParam("audio") MultipartFile audioFile) throws IOException {
        
        if (audioFile.isEmpty()) {
            throw new IllegalArgumentException("Audio file is empty");
        }
        
        // TODO: Implementar transcrição, chat, e TTS completo
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Voice endpoint placeholder - Etapa 8 em desenvolvimento");
        response.put("status", "not_implemented");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT 2: Apenas transcreve áudio (sem chat/TTS).
     * 
     * PLACEHOLDER para Etapa 8.
     * 
     * @param audioFile Arquivo de áudio
     * @return JSON com transcrição
     * @throws IOException Se houver erro ao processar áudio
     */
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Transcribe audio only (Placeholder)", description = "Upload audio and get text transcript")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transcription result"),
        @ApiResponse(responseCode = "400", description = "Invalid audio file"),
        @ApiResponse(responseCode = "501", description = "Not implemented yet")
    })
    public ResponseEntity<Map<String, Object>> transcribeOnly(
            @Parameter(description = "Audio file to transcribe", required = true)
            @RequestParam("audio") MultipartFile audioFile) throws IOException {
        
        if (audioFile.isEmpty()) {
            throw new IllegalArgumentException("Audio file is empty");
        }
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("transcript", "[Transcrição placeholder - Etapa 8 em desenvolvimento]");
        response.put("status", "not_implemented");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT 3: Envia texto ao ChatModel.
     * 
     * ✅ ENDPOINT FUNCIONAL - Usa ChatModel se disponível
     * 
     * EXEMPLO DE USO:
     * curl -X POST \
     *   -H "Content-Type: application/json" \
     *   -d '{"text": "Qual é meu saldo?"}' \
     *   http://localhost:8080/ai/chat
     * 
     * RESPOSTA: {"response": "Seu saldo é...","status": "success"}
     * 
     * @param request JSON com campo "text"
     * @return JSON com resposta da IA
     */
    @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Chat with AI", description = "Send text message and get AI response")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "AI response"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "503", description = "ChatModel not configured (missing OPENAI_API_KEY)")
    })
    public ResponseEntity<Map<String, Object>> chat(
            @RequestBody ChatRequest request) {
        
        if (request.getText() == null || request.getText().isEmpty()) {
            throw new IllegalArgumentException("Text field is required and cannot be empty");
        }
        
        // Se ChatModel não estiver disponível, retornar erro informativo
        if (chatModel.isEmpty()) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("error", "ChatModel not configured");
            result.put("message", "Please set OPENAI_API_KEY environment variable to use AI chat");
            result.put("status", "unavailable");
            return ResponseEntity.status(503).body(result);
        }
        
        // Chamar ChatModel
        var response = chatModel.get().call(new Prompt(request.getText()));
        String aiResponse = response.getResult().getOutput().getText();
        
        // Retornar JSON
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("response", aiResponse);
        result.put("status", "success");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * ENDPOINT 4: Transcreve áudio e retorna como JSON (alternativa a /transcribe).
     * 
     * PLACEHOLDER para Etapa 8.
     * 
     * @param request JSON com campo "text" para TTS
     * @return MP3 binário da fala
     */
    @PostMapping(value = "/speak", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Text to speech (Placeholder)", description = "Convert text to MP3 audio")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "MP3 audio"),
        @ApiResponse(responseCode = "400", description = "Invalid text"),
        @ApiResponse(responseCode = "501", description = "Not implemented yet")
    })
    public ResponseEntity<?> speak(
            @RequestBody ChatRequest request) {
        
        if (request.getText() == null || request.getText().isEmpty()) {
            throw new IllegalArgumentException("Text field is required and cannot be empty");
        }
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "TTS endpoint placeholder - Etapa 8 em desenvolvimento");
        response.put("status", "not_implemented");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Classe interna para desserializar requests JSON.
     * 
     * EXEMPLO DE JSON:
     * {
     *   "text": "Qual é meu saldo?"
     * }
     */
    public static class ChatRequest {
        private String text;
        
        public ChatRequest() {}
        
        public ChatRequest(String text) {
            this.text = text;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
    }
}
