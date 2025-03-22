package org.sight.tel.controller;

import java.util.List;
import org.sight.tel.dto.TelegramChannel;
import org.sight.tel.service.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

  private final TelegramService telegramService;

  public TelegramController(TelegramService telegramService) {
    this.telegramService = telegramService;
  }

  @GetMapping("/subscribers")
  public ResponseEntity<List<TelegramChannel>> getSubscribers() {
    List<TelegramChannel> data = telegramService.fetchSubscribers();
    return ResponseEntity.ok(data);
  }
}
