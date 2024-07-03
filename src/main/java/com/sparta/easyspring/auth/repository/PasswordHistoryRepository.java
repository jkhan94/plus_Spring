package com.sparta.easyspring.auth.repository;

import com.sparta.easyspring.auth.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    ArrayList<PasswordHistory> findByUserId(Long id);
}
