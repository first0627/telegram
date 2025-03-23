package org.sight.tel.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sight.tel.entity.Channel;
import org.sight.tel.entity.SubscriberHistory;
import org.sight.tel.repository.ChannelRepository;
import org.sight.tel.repository.SubscriberHistoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TelegramService {

  private final SubscriberHistoryRepository repository;
  private final ChannelRepository channelRepository;

  public TelegramService(
      SubscriberHistoryRepository repository, ChannelRepository channelRepository) {
    this.repository = repository;
    this.channelRepository = channelRepository;
  }

  // 채널 등록 API에서 호출
  public void addChannel(String telegramId) {
    String url = "https://t.me/" + telegramId;
    channelRepository.save(new Channel(url, telegramId));
  }

  // 채널 삭제 API에서 호출
  public void deleteChannel(Long id) {
    channelRepository.deleteById(id);
  }

  // 채널에서 실시간으로 채널명, 구독자 수 가져와서 DB 저장
  public void saveTodaySubscribers() {
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    List<Channel> channels = channelRepository.findAll();

    for (Channel channel : channels) {
      String url = channel.getUrl();
      try {
        Document doc = Jsoup.connect(url).get();
        Elements subscriberElement = doc.select("div.tgme_page_extra");
        Elements titleElement = doc.select("div.tgme_page_title > span");

        if (subscriberElement.isEmpty() || titleElement.isEmpty()) {
          System.out.println("[" + url + "] 정보 없음, 스킵");
          continue;
        }

        String subscriberText = subscriberElement.text();
        String channelName = titleElement.text(); // 실제 채널명 가져오기

        int subscriberCount = Integer.parseInt(subscriberText.replaceAll("[^0-9]", ""));

        SubscriberHistory existing =
            repository.findByChannelNameAndDate(channelName, today).orElse(null);

        if (existing != null) {
          if (existing.getSubscriberCount() != subscriberCount) {
            existing.setSubscriberCount(subscriberCount);
            repository.save(existing);
            System.out.println("[" + channelName + "] 구독자 업데이트: " + subscriberCount);
          }
        } else {
          SubscriberHistory history =
              new SubscriberHistory(channelName, url, today, subscriberCount);
          repository.save(history);
          System.out.println("[" + channelName + "] 신규 저장: " + subscriberCount);
        }

      } catch (IOException e) {
        System.out.println("[" + url + "] 크롤링 실패: " + e.getMessage());
      }
    }
  }

  // 메인 메서드
  public List<SubscriberHistory> getLast10DaysData() {
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
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
