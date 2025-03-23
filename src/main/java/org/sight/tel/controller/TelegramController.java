package org.sight.tel.controller;

import java.util.List;
import org.sight.tel.entity.SubscriberHistory;
import org.sight.tel.service.TelegramService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:5173", "https://tele-front-xi.vercel.app"})
@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

  private final TelegramService telegramService;

  public TelegramController(TelegramService telegramService) {
    this.telegramService = telegramService;
  }

  // 수동 저장 API
  @PostMapping("/save")
  public String saveToday() {
    telegramService.saveMissingTodaySubscribers();
    return "오늘 구독자 수 저장 완료";
  }

  // 10일치 이력 조회 API
  @GetMapping("/history")
  public List<SubscriberHistory> getHistory() {
    return telegramService.getLast10DaysData();
  }
}
