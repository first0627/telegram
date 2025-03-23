package org.sight.tel.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.sight.tel.entity.SubscriberHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberHistoryRepository extends JpaRepository<SubscriberHistory, Long> {
  List<SubscriberHistory> findByDateBetween(LocalDate start, LocalDate end);
  List<SubscriberHistory> findByDate(LocalDate date);
  Optional<SubscriberHistory> findByChannelNameAndDate(String channelName, LocalDate date);
}
