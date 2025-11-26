package com.los.loan.repository;

import com.los.loan.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByLoanId(Long loanId);
    Optional<Approval> findByLoanIdAndApprovalLevel(Long loanId, Approval.ApprovalLevel level);
}
