package org.sight.tel.service;

import java.io.IOException;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.sight.tel.entity.Channel;
import org.sight.tel.exception.ChannelException;
import org.sight.tel.repository.ChannelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelService {

  private final ChannelRepository channelRepository;

  @Transactional(readOnly = true)
  public List<Channel> getAllChannels() {
    List<Channel> channels = channelRepository.findAllByOrderByChannelOrderAsc();
    log.info("모든 채널 조회 완료, 총 {}개 채널", channels.size());
    return channels;
  }

  @Transactional
  public Channel addChannel(String urlId) {
    String channelUrl = "https://t.me/" + urlId;

    try {
      Document doc = Jsoup.connect(channelUrl).get();
      String name =
          Optional.ofNullable(doc.selectFirst("div.tgme_page_title > span"))
              .map(Element::text)
              .orElseThrow(() -> new ChannelException("채널 이름 태그를 찾을 수 없습니다."));

      Channel channel = new Channel(name, urlId);

      Integer maxOrder = channelRepository.findMaxChannelOrder().orElse(0);
      channel.changeChannelOrder(maxOrder + 1); // setter 대신 의미 있는 메서드 사용

      Channel savedChannel = channelRepository.save(channel);
      log.info("채널 등록 완료: {} (urlId: {}, order: {})", name, urlId, savedChannel.getChannelOrder());

      return savedChannel;

    } catch (IOException e) {
      log.error("크롤링 실패 [{}]: {}", urlId, e.getMessage());
      throw new ChannelException("크롤링 실패: " + e.getMessage());
    }
  }

  @Transactional
  public void reorderChannels(List<Long> orderedIds) {
    log.info("채널 순서 재정렬 시작, 총 {}개", orderedIds.size());

    List<Channel> channels = channelRepository.findAllById(orderedIds);
    Map<Long, Channel> channelMap = new HashMap<>();
    for (Channel channel : channels) {
      channelMap.put(channel.getId(), channel);
    }

    List<Channel> channelsToSave = new ArrayList<>();
    for (int i = 0; i < orderedIds.size(); i++) {
      Long id = orderedIds.get(i);
      Channel channel = channelMap.get(id);
      if (channel == null) {
        log.warn("채널 ID {} 를 찾을 수 없습니다. 건너뜀", id);
        continue;
      }
      channel.changeChannelOrder(i + 1); // setter 대신 의미 있는 메서드
      channelsToSave.add(channel);
    }

    channelRepository.saveAll(channelsToSave);
    log.info("채널 순서 재정렬 완료");
  }

  @Transactional
  public void deleteChannel(Long id) {
    channelRepository.deleteById(id);
    log.info("채널 삭제 완료, ID: {}", id);
  }
}
