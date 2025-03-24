package org.sight.tel.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.sight.tel.entity.SubscriberHistory;
import org.sight.tel.service.TelegramService;
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
  public String ping() {
    System.out.println("ping 요청 수신");
    return "pong";
  }
}
