package org.sight.tel.repository;

import org.sight.tel.entity.SubscriberHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriberHistoryRepository extends JpaRepository<SubscriberHistory, Long> {
  List<SubscriberHistory> findByDateBetween(LocalDate start, LocalDate end);
}
