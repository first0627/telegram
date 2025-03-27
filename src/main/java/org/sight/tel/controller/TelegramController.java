package org.sight.tel.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sight.tel.entity.SubscriberHistory;
import org.sight.tel.service.TelegramService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:5173", "https://tele-front-xi.vercel.app"})
@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class TelegramController {

  private final TelegramService telegramService;

  @PostMapping("/save")
  public String saveToday() {
    telegramService.saveTodaySubscribers();
    return "오늘 구독자 수 저장 및 갱신 완료";
  }

  @GetMapping("/history")
  public List<SubscriberHistory> getHistory() {
    return telegramService.getLast10DaysData();
  }

  @GetMapping("/ping")
  public ResponseEntity<String> ping(HttpServletRequest request) {
    System.out.println("ping 요청 수신: " + request.getHeader("User-Agent"));
    String htmlResponse = "<html><body><h1>Pong</h1><p>Time: " + LocalDateTime.now() + "</p></body></html>";
    return ResponseEntity
            .ok()
            .contentType(MediaType.TEXT_HTML)
            .body(htmlResponse);
  }
}
