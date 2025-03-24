// ChannelController.java
package org.sight.tel.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.sight.tel.entity.Channel;
import org.sight.tel.repository.ChannelRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

  private final ChannelRepository channelRepository;

  public ChannelController(ChannelRepository channelRepository) {
    this.channelRepository = channelRepository;
  }

  @GetMapping
  public List<Channel> getAllChannels() {
    return channelRepository.findAllByOrderByChannelOrderAsc();
  }

  @PostMapping
  public Channel addChannel(@RequestBody ChannelRequest request) {
    String urlId = request.urlId();
    String channelUrl = "https://t.me/" + urlId;

    try {
      Document doc = Jsoup.connect(channelUrl).get();
      String name = Objects.requireNonNull(doc.selectFirst("div.tgme_page_title > span"))
              .text();

      Channel channel = new Channel(name, urlId);

      // 💡 현재 채널 중 가장 높은 order 값 가져와서 +1 처리
      Integer maxOrder =
          channelRepository.findAll().stream()
              .map(Channel::getChannelOrder)
              .max(Integer::compareTo)
              .orElse(0);
      channel.setChannelOrder(maxOrder + 1);

      return channelRepository.save(channel);

    } catch (Exception e) {
      throw new RuntimeException("채널 이름 크롤링 실패: " + e.getMessage());
    }
  }

  @PostMapping("/reorder")
  public void reorder(@RequestBody ReorderRequest request) {
    List<Long> orderedIds = request.orderedIds();
    List<Channel> channelsToSave = new ArrayList<>();
    for (int i = 0; i < orderedIds.size(); i++) {
      Long id = orderedIds.get(i);
      Channel channel =
          channelRepository
              .findById(id)
              .orElseThrow(() -> new RuntimeException("채널을 찾을 수 없음: " + id));
      channel.setChannelOrder(i + 1);
      channelsToSave.add(channel);
    }
    channelRepository.saveAll(channelsToSave);
  }

  @DeleteMapping("/{id}")
  public void deleteChannel(@PathVariable Long id) {
    channelRepository.deleteById(id);
  }

  public record ChannelRequest(String urlId) {}

  public record ReorderRequest(List<Long> orderedIds) {}
}
