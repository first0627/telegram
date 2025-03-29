package org.sight.tel.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sight.tel.entity.Channel;
import org.sight.tel.entity.SubscriberHistory;
import org.sight.tel.exception.ChannelException;
import org.sight.tel.repository.ChannelRepository;
import org.sight.tel.repository.SubscriberHistoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

  private final ChannelRepository channelRepository;
  private final SubscriberHistoryRepository repository;

  public void saveTodaySubscribers() {
    log.info("📦 오늘자 구독자 저장 작업 시작");

    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    List<Channel> channels = channelRepository.findAll();

    for (Channel channel : channels) {
      try {
        int count = extractSubscriberCount(channel.getChannelUrl());
        processSubscriber(channel, today, count);
      } catch (Exception e) {
        log.error("❌ [{}] 크롤링 실패: {}", channel.getName(), e.getMessage(), e);
        throw new ChannelException("[" + channel.getName() + "] 크롤링 오류: " + e.getMessage());
      }
    }

    log.info("✅ 오늘자 구독자 저장 작업 완료");
  }

  private int extractSubscriberCount(String url) throws Exception {
    Document doc = Jsoup.connect(url).get();
    Elements element = doc.select("div.tgme_page_extra");

    if (element.isEmpty() || element.text().isBlank()) {
      throw new IllegalStateException("구독자 정보를 찾을 수 없습니다.");
    }

    return Integer.parseInt(element.text().replaceAll("[^0-9]", ""));
  }

  private void processSubscriber(Channel channel, LocalDate date, int count) {
    SubscriberHistory existing = repository.findByChannelAndDate(channel, date).orElse(null);

    if (existing != null) {
      if (!existing.getSubscriberCount().equals(count)) {
        existing.updateSubscriberCount(count);
        repository.save(existing);
        log.info("🔄 [{}] 구독자 수 업데이트: {}", channel.getName(), count);
      } else {
        log.info("⏭️ [{}] 동일 구독자 수({}), 업데이트 생략", channel.getName(), count);
      }
    } else {
      SubscriberHistory history = SubscriberHistory.create(channel, date, count);
      repository.save(history);
      log.info("🆕 [{}] 신규 구독자 저장 완료: {}", channel.getName(), count);
    }
  }

  @Transactional(readOnly = true)
  public List<SubscriberHistoryDto> getLast10DaysData() {
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    LocalDate tenDaysAgo = today.minusDays(11);

    return repository.findSortedHistoryBetween(tenDaysAgo, today).stream()
        .map(
            h ->
                new SubscriberHistoryDto(
                    h.getChannel().getName(),
                    h.getChannel().getChannelUrl(),
                    h.getDate(),
                    h.getSubscriberCount()))
        .toList();
  }

  @Scheduled(cron = "0 */10 * * * *")
  public void autoSave() {
    saveTodaySubscribers();
    log.info("⏰ 스케줄러가 오늘 구독자 수 저장 완료!");
  }

  public record SubscriberHistoryDto(
      String channelName, String channelUrl, LocalDate date, int subscriberCount) {}
}
