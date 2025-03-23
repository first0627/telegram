// 서버단 코드
// SubscriberHistoryResponse DTO 추가

package org.sight.tel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriberHistoryResponse {
  private String channelName;
  private String channelUrl;
  private String date; // yyyy-MM-dd
  private int subscriberCount;
  private int diff; // 전날 대비 증감
  private double diffRate; // 전날 대비 증감률
}
