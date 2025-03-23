package org.sight.tel.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Data;

@Entity
@Data
@Table(name = "subscribers_history")
public class SubscriberHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String channelName;

  @Column(nullable = false)
  private String channelUrl;

  @Column(name = "date", nullable = false, columnDefinition = "date")
  private LocalDate date;

  @Column(nullable = false)
  private Integer subscriberCount;

  @Column(nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

  // 생성자
  public SubscriberHistory() {}

  public SubscriberHistory(
      String channelName, String channelUrl, LocalDate date, Integer subscriberCount) {
    this.channelName = channelName;
    this.channelUrl = channelUrl;
    this.date = date;
    this.subscriberCount = subscriberCount;
  }

  // Getter & Setter 생략
}
