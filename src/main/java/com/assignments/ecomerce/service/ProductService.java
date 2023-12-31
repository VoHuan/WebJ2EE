package com.assignments.ecomerce.service;

import com.assignments.ecomerce.model.Product;
import com.assignments.ecomerce.repository.ProductRepository;
import com.assignments.ecomerce.utilities.ImageUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ImageUpload imageUpload;

    public Product findById(Integer id) {
        return productRepository.findById(id).get();
    }

    public List<Product> getTopSellingProducts() {
        return productRepository.findTop10ByQuantitySold();
    }

    public List<Product> findAllByCategory(String category) {
        return transfer(productRepository.findAllByCategory(category));
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<Product> productList = transfer(products);
        return productList;
    }

    public Product save(MultipartFile photo, Product product) {
        try {
            Product newProduct = new Product();
            Path uploadPath = Paths.get("src","main", "resources", "static", "img");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String photoFileName = StringUtils.cleanPath(photo.getOriginalFilename() != null ? photo.getOriginalFilename() : "unknown_photo_file");
            Path photoTargetPath = uploadPath.resolve(photoFileName);
            Files.copy(photo.getInputStream(), photoTargetPath, StandardCopyOption.REPLACE_EXISTING);

            newProduct.setImage(photoFileName);
            newProduct.setName(product.getName());
            newProduct.setPrice(product.getPrice());
            newProduct.setDescription(product.getDescription());
            newProduct.setCategory(product.getCategory());
            newProduct.setColor(product.getColor());
            newProduct.setQuantity(product.getQuantity());
            newProduct.setStatus(1);
            productRepository.save(newProduct);
            return newProduct;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Product getById(Integer id) {
        Product product = productRepository.getById(id);
        Product newProduct = new Product();
        newProduct.setId(product.getId());
        newProduct.setName(product.getName());
        newProduct.setDescription(product.getDescription());
        newProduct.setCategory(product.getCategory());
        newProduct.setPrice(product.getPrice());
        newProduct.setQuantity(product.getQuantity());
        newProduct.setImage(product.getImage());
        return product;
    }

    public Product getProductById(Integer id) {
        Optional<Product> optionalOrder = productRepository.findById(id);
        return optionalOrder.orElse(null);
    }

    public Product update(MultipartFile photo, Product product) {
        Product productUpdate = null;
        try {
            Path uploadPath = Paths.get( "src", "main", "resources", "static", "img");
            String photoFileName = StringUtils.cleanPath(photo.getOriginalFilename() != null ? photo.getOriginalFilename() : "unknown_photo_file");
            Path photoTargetPath = uploadPath.resolve(photoFileName);
            Files.copy(photo.getInputStream(), photoTargetPath, StandardCopyOption.REPLACE_EXISTING);
            productUpdate = productRepository.getById(product.getId());
            String productImage = productUpdate.getImage();

            if (photo == null || photo.isEmpty()) {
                productUpdate.setImage(productImage);
            } else {
                productUpdate.setImage(photoFileName);
            }

            productUpdate.setId(productUpdate.getId());
            productUpdate.setName(product.getName());
            productUpdate.setPrice(product.getPrice());
            productUpdate.setDescription(product.getDescription());
            productUpdate.setCategory(product.getCategory());
            productUpdate.setQuantity(product.getQuantity());
            productUpdate.setColor(product.getColor());
            productRepository.save(productUpdate);
            return productUpdate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countProducts() {
        return productRepository.countProducts();
    }

    public Double getTotalRevenue() {
        return productRepository.getTotalRevenue();
    }

    public Page<Product> pageProducts(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo, 5);
        return productRepository.pageProduct(pageable);
    }

    public Page<Product> searchProducts(int pageNo, String keyword) {
        Pageable pageable = PageRequest.of(pageNo, 5);
        List<Product> products = transfer(productRepository.searchProducts(keyword));
        Page<Product> productPages = toPage(products, pageable);
        return productPages;
    }

    private Page toPage(List<Product> list, Pageable pageable) {
        if (pageable.getOffset() >= list.size()) {
            return Page.empty();
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = ((pageable.getOffset() + pageable.getPageSize()) > list.size())
                ? list.size() : (int) (pageable.getOffset() + pageable.getPageSize());
        List subList = list.subList(startIndex, endIndex);
        return new PageImpl(subList, pageable, list.size());
    }

    public List<Product> transfer(List<Product> products) {
        List<Product> productList = new ArrayList<>();
        for (Product product : products) {
            Product newProduct = new Product();
            newProduct.setId(product.getId());
            newProduct.setName(product.getName());
            newProduct.setDescription(product.getDescription());
            newProduct.setCategory(product.getCategory());
            newProduct.setPrice(product.getPrice());
            newProduct.setQuantity(product.getQuantity());
            newProduct.setImage(product.getImage());
            productList.add(newProduct);
        }
        return productList;
    }

    public void deleteById(Integer id) {
        Product product = productRepository.getById(id);
        product.setStatus(0);
        productRepository.save(product);
    }

    public void enableById(Integer id) {
        Product product = productRepository.getById(id);
        productRepository.save(product);
    }
}
