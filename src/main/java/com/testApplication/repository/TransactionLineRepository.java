package com.testApplication.repository;import com.testApplication.model.TransactionLine;import com.testApplication.model.Transaction;import com.testApplication.model.Account;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;import java.util.List;@Repository
public interface TransactionLineRepository extends JpaRepository<TransactionLine, Long> {
    List<TransactionLine> findByTransaction(Transaction transaction);
    List<TransactionLine> findByAccount(Account account);
}