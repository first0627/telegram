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
  public List<ChannelDto> getAllChannels() {
    return channelRepository.findAllByOrderByChannelOrderAsc().stream()
        .map(c -> new ChannelDto(c.getId(), c.getName(), c.getUrlId(), c.getChannelOrder()))
        .toList();
  }

  @Transactional
  public Channel addChannel(String urlId) {
    try {
      Document doc = Jsoup.connect("https://t.me/" + urlId).get();
      String name =
          Optional.ofNullable(doc.selectFirst("div.tgme_page_title > span"))
              .map(Element::text)
              .orElseThrow(() -> new ChannelException("채널 이름 태그를 찾을 수 없습니다."));

      int nextOrder = channelRepository.findMaxChannelOrder().orElse(0) + 1;
      Channel channel = Channel.createChannel(name, urlId, nextOrder);

      Channel saved = channelRepository.save(channel);
      log.info("채널 등록 완료: {} (urlId: {}, order: {})", name, urlId, saved.getChannelOrder());
      return saved;

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

    List<Channel> toSave = new ArrayList<>();
    for (int i = 0; i < orderedIds.size(); i++) {
      Long id = orderedIds.get(i);
      Channel channel = channelMap.get(id);
      if (channel == null) {
        log.warn("채널 ID {} 를 찾을 수 없습니다. 건너뜀", id);
        continue;
      }
      channel.changeChannelOrder(i + 1);
      toSave.add(channel);
    }

    channelRepository.saveAll(toSave);
    log.info("채널 순서 재정렬 완료");
  }

  @Transactional
  public void deleteChannel(Long id) {
    Channel channel =
        channelRepository
            .findById(id)
            .orElseThrow(() -> new ChannelException("채널을 찾을 수 없습니다. id=" + id));

    channelRepository.delete(channel);
    log.info("채널 삭제 완료, ID: {}", id);
  }

  public record ChannelDto(Long id, String name, String urlId, int channelOrder) {}
}
