package hgk.ecommerce.domain.review.service;

import hgk.ecommerce.domain.cart.dto.request.CartItemSaveDto;
import hgk.ecommerce.domain.cart.service.CartService;
import hgk.ecommerce.domain.item.Item;
import hgk.ecommerce.domain.item.dto.enums.Category;
import hgk.ecommerce.domain.item.dto.request.ItemSaveDto;
import hgk.ecommerce.domain.item.dto.response.ItemInfo;
import hgk.ecommerce.domain.item.repository.ItemRepository;
import hgk.ecommerce.domain.order.dto.response.OrderDetail;
import hgk.ecommerce.domain.order.dto.response.OrderItemResponse;
import hgk.ecommerce.domain.order.service.OrderService;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.dto.request.OwnerSignUpDto;
import hgk.ecommerce.domain.owner.repository.OwnerRepository;
import hgk.ecommerce.domain.payment.service.PaymentService;
import hgk.ecommerce.domain.review.Review;
import hgk.ecommerce.domain.review.dto.enums.ReviewStatus;
import hgk.ecommerce.domain.review.dto.request.ReviewEditDto;
import hgk.ecommerce.domain.review.dto.request.ReviewSaveDto;
import hgk.ecommerce.domain.review.dto.response.ReviewInfo;
import hgk.ecommerce.domain.review.repository.ReviewRepository;
import hgk.ecommerce.domain.shop.Shop;
import hgk.ecommerce.domain.shop.dto.request.ShopSaveDto;
import hgk.ecommerce.domain.shop.repository.ShopRepository;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.dto.request.UserSignUpDto;
import hgk.ecommerce.domain.user.repository.UserRepository;
import hgk.ecommerce.global.storage.ImageFile;
import hgk.ecommerce.global.storage.repository.ImageFileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ReviewServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    OwnerRepository ownerRepository;
    @Autowired
    ShopRepository shopRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ImageFileRepository imageFileRepository;
    @Autowired
    OrderService orderService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    CartService cartService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    ReviewRepository reviewRepository;
    @PersistenceContext
    EntityManager em;



    @Test
    void 리뷰등록() {
        Owner owner = createOwner("enroll-test-owner", "test-password");
        Shop shop = createShop(owner, "enroll-test-shop");
        Item item = creatItem(shop, "enroll-test-item");
        User user = createUser("enroll-test-user", "test-password");
        paymentService.increasePoint(user.getId(), 10000);

        CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(item.getId(), 1);
        cartService.addCartItem(user.getId(), cartItemSaveDto);
        Long orderId = orderService.order(user.getId());
        OrderDetail orderDetail = orderService.getOrderDetail(user.getId(), orderId);

        List<OrderItemResponse> orderItems = orderDetail.getOrderItems();
        OrderItemResponse orderItemResponse = orderItems.get(0);

        ReviewSaveDto reviewSaveDto = new ReviewSaveDto("test-content", new BigDecimal("4.5"));
        Long reviewId = reviewService.enrollReview(user.getId(), reviewSaveDto, orderItemResponse.getOrderItemId());

        ReviewInfo reviewInfo = reviewService.getItemReviews(item.getId(), 1, 1).get(0);

        assertThat(reviewInfo.getReviewId()).isEqualTo(reviewId);
        assertThat(reviewInfo.getScore()).isEqualTo(reviewSaveDto.getScore());
        assertThat(reviewInfo.getContent()).isEqualTo(reviewSaveDto.getContent());

    }

    @Test
    void 리뷰수정() {
        Owner owner = createOwner("edit-test-owner", "test-password");
        Shop shop = createShop(owner, "edit-test-shop");
        Item item = creatItem(shop, "edit-test-item");
        User user = createUser("edit-test-user", "test-password");
        paymentService.increasePoint(user.getId(), 10000);

        CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(item.getId(), 1);
        cartService.addCartItem(user.getId(), cartItemSaveDto);
        Long orderId = orderService.order(user.getId());
        OrderDetail orderDetail = orderService.getOrderDetail(user.getId(), orderId);

        List<OrderItemResponse> orderItems = orderDetail.getOrderItems();
        OrderItemResponse orderItemResponse = orderItems.get(0);

        ReviewSaveDto reviewSaveDto = new ReviewSaveDto("test-content", new BigDecimal("4.5"));
        Long reviewId = reviewService.enrollReview(user.getId(), reviewSaveDto, orderItemResponse.getOrderItemId());

        ReviewEditDto reviewEditDto = new ReviewEditDto("change-content", new BigDecimal("4.0"));
        reviewService.editReview(user.getId(), reviewId, reviewEditDto);


        ReviewInfo reviewInfo = reviewService.getItemReviews(item.getId(), 1, 1).get(0);

        assertThat(reviewInfo.getReviewId()).isEqualTo(reviewId);
        assertThat(reviewInfo.getScore()).isEqualTo(reviewEditDto.getScore());
        assertThat(reviewInfo.getContent()).isEqualTo(reviewEditDto.getContent());
    }

    @Test
    void 리뷰조회() {
        Owner owner = createOwner("select-test-owner", "test-password");
        Shop shop = createShop(owner, "select-test-shop");
        Item item = creatItem(shop, "select-test-item");

        int loopCount = 9;
        List<User> users = new ArrayList<>();

        for (int i = 0; i < loopCount; i++) {
            User user = createUser("select-test-user-" + i, "test-password-" + i);
            paymentService.increasePoint(user.getId(), 10000);
            users.add(user);
            CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(item.getId(), 1);
            cartService.addCartItem(user.getId(), cartItemSaveDto);
            Long orderId = orderService.order(user.getId());
            Long orderItemId = orderService.getOrderDetail(user.getId(), orderId).getOrderItems().get(0).getOrderItemId();

            ReviewSaveDto reviewSaveDto = new ReviewSaveDto("test-content-" + i, new BigDecimal("4.5"));
            reviewService.enrollReview(user.getId(), reviewSaveDto, orderItemId);
        }


        for (int i = 0; i < loopCount; i++) {
            User user = users.get(i);
            List<ReviewInfo> userReviews = reviewService.getUserReviews(user.getId(), 1, 5);
            assertThat(userReviews.size()).isEqualTo(1);
            ReviewInfo reviewInfo = userReviews.get(0);
            assertThat(reviewInfo.getReviewId()).isNotNull();
            assertThat(reviewInfo.getScore()).isEqualTo(new BigDecimal("4.5"));
            assertThat(reviewInfo.getContent()).isEqualTo("test-content-" + i);
        }
    }

    @Test
    void 리뷰삭제() {
        Owner owner = createOwner("delete-test-owner", "test-password");
        Shop shop = createShop(owner, "delete-test-shop");
        Item item = creatItem(shop, "delete-test-item");
        User user = createUser("delete-test-user", "test-password");
        paymentService.increasePoint(user.getId(), 10000);

        CartItemSaveDto cartItemSaveDto = new CartItemSaveDto(item.getId(), 1);
        cartService.addCartItem(user.getId(), cartItemSaveDto);
        Long orderId = orderService.order(user.getId());
        OrderDetail orderDetail = orderService.getOrderDetail(user.getId(), orderId);

        List<OrderItemResponse> orderItems = orderDetail.getOrderItems();
        OrderItemResponse orderItemResponse = orderItems.get(0);

        ReviewSaveDto reviewSaveDto = new ReviewSaveDto("test-content", new BigDecimal("4.5"));
        Long reviewId = reviewService.enrollReview(user.getId(), reviewSaveDto, orderItemResponse.getOrderItemId());

        assertThat(reviewService.getItemReviews(item.getId(), 1, 5).size()).isEqualTo(1);

        reviewService.deleteReview(user.getId(), reviewId);

        Review review = reviewRepository.findById(reviewId).get();
        assertThat(review.getStatus()).isEqualTo(ReviewStatus.DELETED);
        assertThat(reviewService.getItemReviews(item.getId(), 1, 5).size()).isEqualTo(0);
    }

    private User createUser(String loginId, String password) {
        UserSignUpDto signUpDto = UserSignUpDto.builder()
                .loginId(loginId)
                .password(password)
                .nickname(loginId + "nick")
                .address(loginId + "address")
                .build();

        return userRepository.save(User.createUser(signUpDto));
    }

    private Owner createOwner(String loginId, String password) {
        OwnerSignUpDto ownerSign = new OwnerSignUpDto(loginId, password);
        return ownerRepository.save(Owner.createOwner(ownerSign));
    }

    private Shop createShop(Owner owner, String shopName) {
        ShopSaveDto shopSave = new ShopSaveDto(shopName);
        return shopRepository.save(Shop.createShop(shopSave, owner));
    }

    private Item creatItem(Shop shop, String itemName) {
        ItemSaveDto itemSave = ItemSaveDto.builder()
                .itemName(itemName)
                .price(1)
                .shopId(shop.getId())
                .stock(100000)
                .category(Category.TEST)
                .build();
        ImageFile imageFile = ImageFile.createImageFile(itemName, UUID.randomUUID().toString());
        imageFileRepository.save(imageFile);
        Item item = Item.createItem(itemSave, shop, imageFile);

        return itemRepository.save(item);
    }

}