package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {

    // @Query("FROM Account WHERE username= :username")
    Account findAccountByUsername(String username);

    Account findAccountByUsernameAndPassword(String username, String password);


}
