package teo.monitor.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class KafkaMessageService {

    private final AtomicReference<String> lastMessage = new AtomicReference<>("No messages yet");
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "topic1", groupId = "consumer-group-1")
    public void listen(String message) {
        System.out.println("Received: " + message);
        lastMessage.set(message);

        // Notify all connected SSE clients
        emitters.forEach(emitter -> {
            try {
                emitter.send(message);
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        });
    }

    public String getLastMessage() {
        return lastMessage.get();
    }

    public SseEmitter registerEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }
}
