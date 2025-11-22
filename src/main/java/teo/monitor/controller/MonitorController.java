package teo.monitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import teo.monitor.service.KafkaMessageService;

@Controller
public class MonitorController {

    private final KafkaMessageService messageService;

    public MonitorController(KafkaMessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("lastMessage", messageService.getLastMessage());
        return "index"; // Thymeleaf template: index.html
    }

    @GetMapping("/stream")
    @ResponseBody
    public SseEmitter streamMessages() {
        return messageService.registerEmitter();
    }
}
