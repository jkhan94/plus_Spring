package com.sparta.easyspring.auth.repository;

import com.sparta.easyspring.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {


}
