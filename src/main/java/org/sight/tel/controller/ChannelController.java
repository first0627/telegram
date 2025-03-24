package org.sight.tel.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sight.tel.entity.Channel;
import org.sight.tel.service.ChannelService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:5173", "https://tele-front-xi.vercel.app"})
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  @GetMapping
  public List<Channel> getAllChannels() {
    return channelService.getAllChannels();
  }

  @PostMapping
  public Channel addChannel(@RequestBody ChannelRequest request) {
    return channelService.addChannel(request.urlId());
  }

  @PostMapping("/reorder")
  public void reorder(@RequestBody ReorderRequest request) {
    channelService.reorderChannels(request.orderedIds());
  }

  @DeleteMapping("/{id}")
  public void deleteChannel(@PathVariable Long id) {
    channelService.deleteChannel(id);
  }

  public record ChannelRequest(String urlId) {}

  public record ReorderRequest(List<Long> orderedIds) {}
}
