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

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

  private final ChannelRepository channelRepository;
  private final SubscriberHistoryRepository repository;

  public void saveTodaySubscribers() {
    log.info("오늘자 구독자 저장 작업 시작");
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    List<Channel> channels = channelRepository.findAll();

    for (Channel channel : channels) {
      String url = channel.getChannelUrl();
      try {
        Document doc = Jsoup.connect(url).get();
        Elements subscriberElement = doc.select("div.tgme_page_extra");

        if (subscriberElement.isEmpty() || subscriberElement.text().isBlank()) {
          log.warn("[{}] 구독자 정보 없음, 건너뜀", channel.getName());
          continue;
        }

        int subscriberCount = Integer.parseInt(subscriberElement.text().replaceAll("[^0-9]", ""));
        SubscriberHistory existing =
            repository.findByChannelNameAndDate(channel.getName(), today).orElse(null);

        if (existing != null) {
          if (existing.getSubscriberCount() != subscriberCount) {
            existing.setSubscriberCount(subscriberCount);
            repository.save(existing);
            log.info("[{}] 구독자 업데이트 완료: {}", channel.getName(), subscriberCount);
          } else {
            log.info("[{}] 구독자 수 동일({}), 업데이트 스킵", channel.getName(), subscriberCount);
          }
        } else {
          SubscriberHistory history =
              new SubscriberHistory(channel.getName(), url, today, subscriberCount);
          repository.save(history);
          log.info("[{}] 신규 구독자 저장 완료: {}", channel.getName(), subscriberCount);
        }
      } catch (Exception e) {
        log.error("[{}] 크롤링 실패: {}", channel.getName(), e.getMessage(), e);
        throw new ChannelException("[" + channel.getName() + "] 크롤링 중 오류 발생: " + e.getMessage());
      }
    }
    log.info("오늘자 구독자 저장 작업 완료");
  }

  public List<SubscriberHistory> getLast10DaysData() {
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    LocalDate tenDaysAgo = today.minusDays(11);

    List<Channel> orderedChannels = channelRepository.findAllByOrderByChannelOrderAsc();

    return orderedChannels.stream()
        .flatMap(
            channel ->
                repository
                    .findByChannelNameAndDateBetween(channel.getName(), tenDaysAgo, today)
                    .stream())
        .toList();
  }

  @Scheduled(cron = "0 */10 * * * *")
  public void autoSave() {
    saveTodaySubscribers();
    log.info("스케줄러가 오늘 구독자 수 저장 완료!");
  }
}
