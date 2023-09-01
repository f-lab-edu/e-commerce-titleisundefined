package hgk.ecommerce.domain.shop.repository;

import hgk.ecommerce.domain.shop.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    boolean existsShopByName(String shopName);

    Page<Shop> findShopsByOwnerId(Long ownerId, Pageable pageable);
}
