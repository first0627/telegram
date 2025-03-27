package org.sight.tel.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "subscribers_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 기본 생성자
public class SubscriberHistory {

  @Column(nullable = false)
  private final LocalDateTime createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

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

  public SubscriberHistory(
      String channelName, String channelUrl, LocalDate date, Integer subscriberCount) {
    this.channelName = channelName;
    this.channelUrl = channelUrl;
    this.date = date;
    this.subscriberCount = subscriberCount;
  }

  public void updateSubscriberCount(int newCount) {
    this.subscriberCount = newCount;
  }
}
