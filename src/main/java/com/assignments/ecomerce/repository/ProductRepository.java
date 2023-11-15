package com.assignments.ecomerce.repository;

import com.assignments.ecomerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    @Query("SELECT od.product FROM OrderDetail od GROUP BY od.product.id ORDER BY SUM(od.quantity) DESC")
    List<Product> findTop10ByQuantitySold();

    @Query(value = "SELECT COUNT(*) FROM product", nativeQuery = true)
    int countProducts();

    @Query("SELECT SUM(od.quantity * od.unitPrice) FROM OrderDetail od")
    Double getTotalRevenue();

    @Query("SELECT p from Product p where status = 1")
    Page<Product> pageProduct(Pageable pageable);

    @Query("SELECT p from Product p where CONCAT(p.name,p.price,p.quantity,p.description,p.color) like %?1%")
    List<Product> searchProducts(String keyword);

    @Query("select p from Product p inner join Category c ON c.id = p.category.id" +
            " where p.category.name = ?1")
    List<Product> findAllByCategory(String category);
}
