package com.los.loan.repository;

import com.los.loan.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    List<LoanProduct> findByIsActiveTrue();
    List<LoanProduct> findByProductType(LoanProduct.ProductType productType);
}
