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

  @PostMapping("/save")
  public String saveToday() {
    telegramService.saveTodaySubscribers();
    return "오늘 구독자 수 저장 및 갱신 완료";
  }

  @GetMapping("/history")
  public List<SubscriberHistory> getHistory() {
    return telegramService.getLast10DaysData();
  }
}
