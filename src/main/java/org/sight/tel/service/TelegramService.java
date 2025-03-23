package org.sight.tel.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sight.tel.entity.SubscriberHistory;
import org.sight.tel.repository.SubscriberHistoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TelegramService {

  private final Map<String, String> channels =
      Map.of(
          "번맞뉴", "https://t.me/stock_messenger",
          "급등일보", "https://t.me/FastStockNews",
          "오를주식", "https://t.me/GoUpstock",
          "미니서퍼", "https://t.me/moneythemestock",
          "여의도스토리", "https://t.me/YeouidoStory2",
          "buff", "https://t.me/bufkr",
          "요약하는고잉", "https://t.me/one_going",
          "타점읽어주는여자", "https://t.me/tazastock");

  private final SubscriberHistoryRepository repository;

  public TelegramService(SubscriberHistoryRepository repository) {
    this.repository = repository;
  }

  public void saveTodaySubscribers() {
    LocalDate today = LocalDate.now();

    channels.forEach(
        (name, url) -> {
          try {
            Document doc = Jsoup.connect(url).get();
            Elements subscriberElement = doc.select("div.tgme_page_extra");

            if (subscriberElement.isEmpty()) {
              System.out.println("[" + name + "] 구독자 정보 없음, 건너뜀");
              return;
            }

            String subscriberText = subscriberElement.text();
            if (subscriberText.isBlank()) {
              System.out.println("[" + name + "] 구독자 텍스트가 비어있음, 건너뜀");
              return;
            }

            int subscriberCount = Integer.parseInt(subscriberText.replaceAll("[^0-9]", ""));

            // DB 조회
            SubscriberHistory existing =
                repository.findByChannelNameAndDate(name, today).orElse(null);

            if (existing != null) {
              // 값이 같으면 update 스킵
              if (existing.getSubscriberCount() != subscriberCount) {
                existing.setSubscriberCount(subscriberCount);
                repository.save(existing);
                System.out.println("[" + name + "] 구독자 업데이트 완료: " + subscriberCount);
              } else {
                System.out.println("[" + name + "] 구독자 수 동일(" + subscriberCount + "), 업데이트 스킵");
              }
            } else {
              SubscriberHistory history = new SubscriberHistory(name, url, today, subscriberCount);
              repository.save(history);
              System.out.println("[" + name + "] 신규 구독자 저장 완료: " + subscriberCount);
            }
          } catch (IOException e) {
            System.out.println("[" + name + "] 크롤링 실패: " + e.getMessage());
          } catch (NumberFormatException e) {
            System.out.println("[" + name + "] 숫자 파싱 실패: " + e.getMessage());
          }
        });
  }

  // 메인 메서드
  public List<SubscriberHistory> getLast10DaysData() {
    LocalDate today = LocalDate.now();
    LocalDate tenDaysAgo = today.minusDays(11);

    // 조건부 save 로직 제거하고, 무조건 조회만!
    return repository.findByDateBetween(tenDaysAgo, today);
  }

  // 매일 자동 저장
  @Scheduled(cron = "0 59 14 * * *")
  public void autoSave() {
    saveTodaySubscribers();
    System.out.println("스케줄러가 오늘 구독자 수 저장 완료!");
  }
}
