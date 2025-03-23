package org.sight.tel.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
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

  // 오늘 데이터가 없는 채널만 저장
  public void saveMissingTodaySubscribers() {
    LocalDate today = LocalDate.now();

    // 오늘 이미 저장된 채널명 가져오기
    List<SubscriberHistory> todayData = repository.findByDate(today);
    Set<String> existingChannels =
        todayData.stream().map(SubscriberHistory::getChannelName).collect(Collectors.toSet());

    channels.forEach(
        (name, url) -> {
          if (!existingChannels.contains(name)) {
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
              SubscriberHistory history = new SubscriberHistory(name, url, today, subscriberCount);
              repository.save(history);
              System.out.println("[" + name + "] 구독자 저장 완료: " + subscriberCount);
            } catch (IOException e) {
              System.out.println("[" + name + "] 크롤링 실패: " + e.getMessage());
            } catch (NumberFormatException e) {
              System.out.println("[" + name + "] 숫자 파싱 실패: " + e.getMessage());
            }
          }
        });
  }

  // 메인 메서드
  public List<SubscriberHistory> getLast10DaysData() {
    LocalDate today = LocalDate.now();
    LocalDate tenDaysAgo = today.minusDays(11);

    // 오늘 데이터 부족할 때만 동작
    List<SubscriberHistory> todayData = repository.findByDate(today);
    if (todayData.size() < channels.size()) {
      System.out.println(
          "오늘 데이터 부족 (" + todayData.size() + "/" + channels.size() + ") → 부족한 채널만 저장");
      saveMissingTodaySubscribers();
    }

    // 10일치 데이터 반환
    return repository.findByDateBetween(tenDaysAgo, today);
  }

  // 매일 자동 저장
  @Scheduled(cron = "0 0 9 * * *")
  public void autoSave() {
    saveMissingTodaySubscribers();
    System.out.println("스케줄러가 오늘 구독자 수 저장 완료!");
  }
}
