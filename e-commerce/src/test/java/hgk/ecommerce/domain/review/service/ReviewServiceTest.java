package hgk.ecommerce.domain.review.service;

import hgk.ecommerce.domain.cart.dto.CartItemSave;
import hgk.ecommerce.domain.cart.service.CartService;
import hgk.ecommerce.domain.common.exception.AuthenticationException;
import hgk.ecommerce.domain.common.exception.NoResourceException;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.ItemSave;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.order.Order;
import hgk.ecommerce.domain.order.OrderItem;
import hgk.ecommerce.domain.order.repository.OrderItemRepository;
import hgk.ecommerce.domain.order.repository.OrderRepository;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.OwnerSign;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.payment.repository.PaymentRepository;
import hgk.ecommerce.domain.review.Review;
import hgk.ecommerce.domain.review.dto.ReviewEdit;
import hgk.ecommerce.domain.review.dto.ReviewSave;
import hgk.ecommerce.domain.review.repository.ReviewRepository;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSave;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.exceptions.AlreadyExistException;
import hgk.ecommerce.domain.user.dto.request.UserSign;
import hgk.ecommerce.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static hgk.ecommerce.domain.item.dto.Category.ALBUM;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    //region CONSTRUCTOR
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CartService cartService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ReviewService reviewService;
    @PersistenceContext
    EntityManager em;
    //endregion

    User userA;
    User userB;
    Item itemA;
    Item itemB;
    Item itemC;
    Order order;
    OrderItem orderItemA;
    OrderItem orderItemB;
    int reviewCount = 40;

    @BeforeEach
    void beforeEach() {
        userA = createUser("TEST-USER-A", "TEST-PASSWORD");
        userB = createUser("TEST-USER-B", "TEST-PASSWORD");
        ItemSave itemSave = new ItemSave("test-item", 10000, 10000, ALBUM, null);
        itemA = itemRepository.save(Item.createItem(itemSave, null));
        itemB = itemRepository.save(Item.createItem(itemSave, null));
        itemC = itemRepository.save(Item.createItem(itemSave, null));
        order = orderRepository.save(Order.createOrder(userA));
        orderItemA = orderItemRepository.save(OrderItem.createOrderItem(order, itemA, 1));
        orderItemB = orderItemRepository.save(OrderItem.createOrderItem(order, itemB, 1));

        for (int i = 0; i < reviewCount; i++) {
            Review review = Review.createReview(null, itemA, null, new ReviewSave("test-1", new BigDecimal("4.5")));
            reviewRepository.save(review);
        }

        em.flush();
        em.clear();
//        reviewRepository.save(Review.createReview(userA, orderItem, new ReviewSave("TEST", new BigDecimal("4.5"))));

    }

    //region 리뷰조회

    @Test
    void 리뷰_조회() {
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, 5).size()).isEqualTo(5);
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount).size()).isEqualTo(reviewCount);
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount);
    }

    @Test
    void 내_리뷰_조회() {
        reviewService.enrollReview(userA, orderItemA.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
        assertThat(reviewService.getReviewsByUser(userA, 1, 5).size()).isEqualTo(1);

        reviewService.enrollReview(userA, orderItemB.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
        assertThat(reviewService.getReviewsByUser(userA, 1, 2).size()).isEqualTo(2);
    }

    @Test
    void 삭제된_리뷰_제외_조회_테스트() {
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount);

        Long reviewId = reviewService.enrollReview(userA, orderItemA.getId(), new ReviewSave("test", new BigDecimal("4.5")));
        em.flush();
        em.clear();

        reviewService.deleteReview(userA, reviewId);
        em.flush();
        em.clear();

        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount);
    }

    //endregion

    //region 리뷰등록

    @Test
    void 리뷰_정상_등록() {
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount);
        reviewService.enrollReview(userA, orderItemA.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
        em.flush();
        em.clear();
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount + 1);
    }

    @Test
    void 리뷰_중복_등록() {
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount);
        reviewService.enrollReview(userA, orderItemA.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
        em.flush();
        em.clear();
        assertThatThrownBy(() ->
                reviewService.enrollReview(userA, orderItemA.getId(), new ReviewSave("test-1234", new BigDecimal("4.5"))))
                .isInstanceOf(AlreadyExistException.class);
    }

    @Test
    void 타인_리뷰_등록() {
        assertThatThrownBy(() -> {
            reviewService.enrollReview(userB, orderItemA.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
        }).isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 주문_내역_없는_리뷰_등록() {
        assertThatThrownBy(() -> {
            reviewService.enrollReview(userB, 100L, new ReviewSave("test-1234", new BigDecimal("4.5")));
        }).isInstanceOf(AuthenticationException.class);
    }

    //endregion

    //region 리뷰삭제

    @Test
    void 리뷰_정상_삭제() {
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount);
        Long reviewId = reviewService.enrollReview(userA, orderItemA.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
        em.flush();
        em.clear();
        reviewService.deleteReview(userA, reviewId);

        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount);
    }

    @Test
    void 타인_리뷰_삭제() {
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount);
        Long reviewId = reviewService.enrollReview(userA, orderItemA.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
        em.flush();
        em.clear();
        assertThatThrownBy(() -> reviewService.deleteReview(userB, reviewId))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void 존재하지_않는_리뷰삭제() {
        assertThatThrownBy(() -> reviewService.deleteReview(userB, 100L))
                .isInstanceOf(NoResourceException.class);
    }

    //endregion

    //region 리뷰 수정
    @Test
    void 리뷰_정상_수정() {
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, reviewCount + 1).size()).isEqualTo(reviewCount);
        Long reviewId = reviewService.enrollReview(userA, orderItemA.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
        em.flush();
        em.clear();
        ReviewEdit reviewEdit = new ReviewEdit("change-1234", new BigDecimal("5.0"));
        reviewService.editeReview(userA, reviewId, reviewEdit);
        em.flush();
        em.clear();

        Review review = reviewRepository.findById(reviewId).orElseThrow();

        assertThat(review.getScore().intValue()).isEqualTo(reviewEdit.getScore().intValue());
        assertThat(review.getContent()).isEqualTo(reviewEdit.getContent());
    }

    //endregion

    //region PRIVATE METHOD

    private void addCart(User user, Item item, int quantity) {
        CartItemSave cartItemSave = new CartItemSave(item.getId(), quantity);
        cartService.addToCart(user, cartItemSave);
    }

    private User createUser(String loginId, String password) {
        UserSign userSign = new UserSign(loginId, password, loginId, "address");
        User user = User.createUser(userSign);
        return userRepository.save(user);
    }

    private Owner createOwner(String loginId, String password) {
        OwnerSign ownerSign = new OwnerSign(loginId, password);
        Owner owner = Owner.createOwner(ownerSign);
        return ownerRepository.save(owner);
    }

    private Shop createShop(Owner owner, String shopName) {
        ShopSave shopSave = new ShopSave(shopName);
        Shop shop = Shop.createShop(shopSave, owner);
        return shopRepository.save(shop);
    }

    private Item createItem(Shop shop, String itemName) {
        ItemSave itemSave = new ItemSave(itemName, 100, 1000, ALBUM, shop.getId());
        Item item = Item.createItem(itemSave, shop);
        return itemRepository.save(item);
    }

    //endregion
}