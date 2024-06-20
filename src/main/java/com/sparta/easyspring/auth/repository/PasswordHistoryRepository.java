package com.sparta.easyspring.auth.repository;

import com.sparta.easyspring.auth.entity.PasswordHistory;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory,Long> {

    ArrayList<PasswordHistory> findByUserId(Long id);
}
