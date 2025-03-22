package org.sight.tel.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.sight.tel.dto.TelegramChannel;
import org.springframework.stereotype.Service;

@Service
public class TelegramService {

  private final Map<String, String> channels =
      Map.of(
          "번맞뉴", "https://t.me/stock_messenger",
          "급등일보", "https://t.me/FastStockNews"
          /*
          "오를주식", "https://t.me/오를주식",
          "미니서퍼", "https://t.me/미니서퍼",
          "여의도스토리", "https://t.me/여의도스토리",
          "buff", "https://t.me/buff",
          "요약하는고잉", "https://t.me/요약하는고잉",
          "타점읽어주는여자", "https://t.me/타점읽어주는여자"*/
          );

  public List<TelegramChannel> fetchSubscribers() {
    List<TelegramChannel> result = new ArrayList<>();
    channels.forEach(
        (name, url) -> {
          try {
            Document doc = Jsoup.connect(url).get();
            Elements subscriberElement = doc.select("div.tgme_page_extra");
            String subscriberCount = subscriberElement.text();
            result.add(new TelegramChannel(name, url, subscriberCount));
          } catch (IOException e) {
            result.add(new TelegramChannel(name, url, "조회 실패"));
          }
        });
    return result;
  }
}
