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
import hgk.ecommerce.domain.review.dto.ReviewResponse;
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
import java.util.List;

import static hgk.ecommerce.domain.item.dto.Category.ELECTRONIC;
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
    Order orderA;
    Order orderB;
    OrderItem orderItemA_1;
    OrderItem orderItemA_2;
    OrderItem orderItemB_1;
    OrderItem orderItemB_2;

    @BeforeEach
    void beforeEach() {
        userA = createUser("TEST-USER-A", "TEST-PASSWORD");
        userB = createUser("TEST-USER-B", "TEST-PASSWORD");
        ItemSave itemSave = new ItemSave("test-item", 10000, 10000, ELECTRONIC, null);
        ShopSave shopSave = new ShopSave("TEST-SHOP");
        Owner owner = Owner.createOwner(new OwnerSign("test-abcd", "1234567"));
        ownerRepository.save(owner);
        Shop shop = shopRepository.save(Shop.createShop(shopSave, owner));
        itemA = itemRepository.save(Item.createItem(itemSave, shop));
        itemB = itemRepository.save(Item.createItem(itemSave, shop));
        itemC = itemRepository.save(Item.createItem(itemSave, shop));
        orderA = orderRepository.save(Order.createOrder(userA));
        orderItemA_1 = orderItemRepository.save(OrderItem.createOrderItem(orderA, itemA, 1));
        orderItemA_2 = orderItemRepository.save(OrderItem.createOrderItem(orderA, itemB, 1));

        orderB = orderRepository.save(Order.createOrder(userB));
        orderItemB_1 = orderItemRepository.save(OrderItem.createOrderItem(orderB, itemA, 1));
        orderItemB_2 = orderItemRepository.save(OrderItem.createOrderItem(orderB, itemB, 1));

        Review review = Review.createReview(userA, itemA, orderItemA_1, new ReviewSave("test-1", new BigDecimal("4.5")));
        reviewRepository.save(review);

        em.flush();
        em.clear();
//        reviewRepository.save(Review.createReview(userA, orderItem, new ReviewSave("TEST", new BigDecimal("4.5"))));

    }

    //region 리뷰조회

    @Test
    void 리뷰_조회() {
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, 5).size()).isEqualTo(1);
    }

    @Test
    void 내_리뷰_조회() {
        assertThat(reviewService.getReviewsByUser(userA, 1, 5).size()).isEqualTo(1);
    }

    @Test
    void 삭제된_리뷰_제외_조회_테스트() {
        List<ReviewResponse> reviewResponses = reviewService.getReviewsByItemId(itemA.getId(), 1, 5);
        assertThat(reviewResponses.size()).isEqualTo(1);

        em.flush();
        em.clear();

        reviewService.deleteReview(userA, reviewResponses.get(0).getReviewId());
        em.flush();
        em.clear();

        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, 5).size()).isEqualTo(0);
    }

    //endregion

    //region 리뷰등록

    @Test
    void 리뷰_정상_등록() {
        List<ReviewResponse> reviewsByItemId = reviewService.getReviewsByItemId(itemA.getId(), 1, 5);
        assertThat(reviewsByItemId.size()).isEqualTo(1);
        reviewService.enrollReview(userB, orderItemB_1.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
        em.flush();
        em.clear();
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, 5).size()).isEqualTo(reviewsByItemId.size() + 1);
    }

    @Test
    void 리뷰_중복_등록() {
        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, 5).size()).isEqualTo(1);

        assertThatThrownBy(() ->
                reviewService.enrollReview(userA, orderItemA_1.getId(), new ReviewSave("test-1234", new BigDecimal("4.5"))))
                .isInstanceOf(AlreadyExistException.class);
    }

    @Test
    void 타인_리뷰_등록() {
        assertThatThrownBy(() -> {
            reviewService.enrollReview(userB, orderItemA_1.getId(), new ReviewSave("test-1234", new BigDecimal("4.5")));
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
        List<ReviewResponse> reviewResponses = reviewService.getReviewsByItemId(itemA.getId(), 1, 5);
        assertThat(reviewResponses.size()).isEqualTo(1);
        em.flush();
        em.clear();
        reviewService.deleteReview(userA, reviewResponses.get(0).getReviewId());

        assertThat(reviewService.getReviewsByItemId(itemA.getId(), 1, 5).size()).isEqualTo(0);
    }

    @Test
    void 타인_리뷰_삭제() {
        List<ReviewResponse> reviewResponses = reviewService.getReviewsByItemId(itemA.getId(), 1, 5);
        assertThat(reviewResponses.size()).isEqualTo(1);
        assertThatThrownBy(() -> reviewService.deleteReview(userB, reviewResponses.get(0).getReviewId()))
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
        List<ReviewResponse> reviewResponses = reviewService.getReviewsByItemId(itemA.getId(), 1, 5);
        assertThat(reviewResponses.size()).isEqualTo(1);

        em.flush();
        em.clear();
        ReviewEdit reviewEdit = new ReviewEdit("change-1234", new BigDecimal("5.0"));
        Long reviewId = reviewResponses.get(0).getReviewId();
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
        ItemSave itemSave = new ItemSave(itemName, 100, 1000, ELECTRONIC, shop.getId());
        Item item = Item.createItem(itemSave, shop);
        return itemRepository.save(item);
    }

    //endregion
}