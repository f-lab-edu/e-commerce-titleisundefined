package hgk.ecommerce.domain.shop.repository;

import hgk.ecommerce.domain.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Page<Shop> findShopsByOwnerId(Long ownerId, Pageable pageable);

    boolean existsShopByName(String shopName);
}
