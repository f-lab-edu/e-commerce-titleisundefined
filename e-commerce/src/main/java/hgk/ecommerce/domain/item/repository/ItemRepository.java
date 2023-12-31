package hgk.ecommerce.domain.item.repository;

import hgk.ecommerce.domain.item.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Item i where i.id = :itemId")
    Optional<Item> findItemWithLock(@Param("itemId") Long itemId);

    @Query("select i from Item i join fetch i.shop where i.id = :itemId")
    Optional<Item> findItemFetchShop(@Param("itemId") Long itemId);

    Page<Item> findItemsByShopId(Long shopId, Pageable pageable);
}
