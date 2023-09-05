DROP TABLE IF EXISTS users cascade ;
DROP TABLE IF EXISTS owners cascade ;
DROP TABLE IF EXISTS shop cascade ;
DROP TABLE IF EXISTS items cascade ;
DROP TABLE IF EXISTS cart cascade ;
DROP TABLE IF EXISTS cart_item cascade ;
DROP TABLE IF EXISTS orders cascade ;
DROP TABLE IF EXISTS order_item cascade ;
DROP TABLE IF EXISTS payment cascade ;
DROP TABLE IF EXISTS review cascade ;


CREATE TABLE users
(
    user_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    status   VARCHAR(10) NOT NULL,
    address  VARCHAR(50) NOT NULL,
    create_date timestamp(6),
    modify_date timestamp(6)
);

CREATE TABLE owners
(
    owner_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    create_date timestamp(6),
    modify_date timestamp(6)
);

CREATE TABLE shop
(
    shop_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL UNIQUE,
    owner_id BIGINT       NOT NULL,
    create_date timestamp(6),
    modify_date timestamp(6),
    FOREIGN KEY (owner_id) REFERENCES owners (owner_id)
);

CREATE TABLE items
(
    item_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(50)  NOT NULL,
    stock    INT          NOT NULL,
    price    INT          NOT NULL,
    status   VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    shop_id  BIGINT       NOT NULL,
    create_date timestamp(6),
    modify_date timestamp(6),
    FOREIGN KEY (shop_id) REFERENCES shop (shop_id)
);

CREATE TABLE cart
(
    cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    create_date timestamp(6),
    modify_date timestamp(6),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE cart_item
(
    cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL,
    cart_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    create_date timestamp(6),
    modify_date timestamp(6),
    FOREIGN KEY (cart_id) REFERENCES cart (cart_id),
    FOREIGN KEY (item_id) REFERENCES items (item_id)
);

CREATE TABLE orders
(
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_status varchar(20) NOT NULL ,
    user_id BIGINT NOT NULL ,
    create_date timestamp(6),
    modify_date timestamp(6),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE order_item
(
    order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_price INT NOT NULL,
    quantity INT NOT NULL,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    create_date timestamp(6),
    modify_date timestamp(6),
    FOREIGN KEY (order_id) REFERENCES orders (order_id),
    FOREIGN KEY (item_id) REFERENCES items (item_id)
);

CREATE TABLE payment
(
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    point INT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE review
(
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content varchar(200) NOT NULL,
    score numeric(2, 1) NOT NULL,
    review_status varchar(20) NOT NULL,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    order_item_id BIGINT NOT NULL,
    create_date timestamp(6),
    modify_date timestamp(6),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (item_id) REFERENCES items (item_id),
    FOREIGN KEY (order_item_id) REFERENCES order_item (order_item_id)
);


INSERT INTO users (login_id, password, nickname, status, address, create_date, modify_date)
VALUES ('john_doe', 'password123', 'John Doe', 'ACTIVE', '123 Main St', now(), now()),
       ('jane_smith', 'pass456', 'Jane Smith', 'ACTIVE', '456 Elm St', now(), now()),
       ('bob_jones', 'securepass', 'Bob Jones', 'ACTIVE', '789 Oak St', now(), now()),
       ('alice_green', 'green123', 'Alice Green', 'ACTIVE', '101 Maple Ave', now(), now()),
       ('david_wilson', 'davepass', 'David Wilson', 'ACTIVE', '567 Pine Rd', now(), now()),
       ('susan_parker', 'susieq', 'Susan Parker', 'ACTIVE', '222 Cedar Ln', now(), now()),
       ('michael_lee', 'mike123', 'Michael Lee', 'ACTIVE', '333 Redwood Dr', now(), now()),
       ('emily_adams', 'emilypass', 'Emily Adams', 'ACTIVE', '444 Birch Rd', now(), now()),
       ('william_white', 'willpass', 'William White', 'ACTIVE', '777 Willow St', now(), now()),
       ('linda_smith', 'lindapass', 'Linda Smith', 'ACTIVE', '888 Spruce Ln', now(), now());

INSERT INTO owners (login_id, password, create_date, modify_date)
VALUES ('john_owner', 'password123', now(), now()),
       ('jane_owner', 'pass456', now(), now()),
       ('bob_owner', 'securepass', now(), now()),
       ('alice_owner', 'alicepass', now(), now()),
       ('david_owner', 'davepass', now(), now()),
       ('susan_owner', 'susieq', now(), now()),
       ('michael_owner', 'mike123', now(), now()),
       ('emily_owner', 'emilypass', now(), now()),
       ('william_owner', 'willpass', now(), now()),
       ('linda_owner', 'lindapass', now(), now());

INSERT INTO shop (name, owner_id, create_date, modify_date)
VALUES ('Electronics Store', 1, now(), now()),
       ('Fashion Boutique', 2, now(), now()),
       ('Home Decor Shop', 3, now(), now()),
       ('Bookstore', 4, now(), now()),
       ('Grocery Store', 5, now(), now()),
       ('Sports Equipment Store', 6, now(), now()),
       ('Jewelry Shop', 7, now(), now()),
       ('Pet Store', 8, now(), now()),
       ('Furniture Store', 9, now(), now()),
       ('Coffee Shop', 10, now(), now());

INSERT INTO items (name, stock, price, status, category, shop_id, create_date, modify_date)
VALUES ('iPhone 13 Pro', 50, 1200000, 'ACTIVE', 'ELECTRONIC', 1, now(), now()),
       ('MacBOOK Air', 30, 1500000, 'ACTIVE', 'ELECTRONIC', 1, now(), now()),
       ('나이키 운동화', 50, 150000, 'ACTIVE', 'CLOTHES', 1, now(), now()),
       ('H&M 셔츠', 30, 30000, 'ACTIVE', 'CLOTHES', 1, now(), now()),
       ('레고 테크닉 차량', 30, 80000, 'ACTIVE', 'ETC', 1, now(), now()),
       ('톰브라운 수트', 10, 500000, 'ACTIVE', 'CLOTHES', 1, now(), now()),
       ('Samsung Galaxy S21', 45, 1000000, 'ACTIVE', 'ELECTRONIC', 2, now(), now()),
       ('Nike Air Max', 75, 200000, 'ACTIVE', 'CLOTHES', 2, now(), now()),
       ('Adidas Ultraboost', 60, 180000, 'ACTIVE', 'CLOTHES', 2, now(), now()),
       ('한우 스테이크', 15, 40000, 'ACTIVE', 'FOOD', 2, now(), now()),
       ('초콜릿 케이크', 25, 25000, 'ACTIVE', 'FOOD', 2, now(), now()),
       ('샐러드 바 세트', 15, 15000, 'ACTIVE', 'FOOD', 2, now(), now()),
       ('헬스 클럽 멤버십', 100, 60000, 'ACTIVE', 'ETC', 2, now(), now()),
       ('Levis Jeans', 40, 80000, 'ACTIVE', 'CLOTHES', 3, now(), now()),
       ('피자 마르게리타', 20, 25000, 'ACTIVE', 'FOOD', 3, now(), now()),
       ('스파게티 볼로네제', 35, 18000, 'ACTIVE', 'FOOD', 3, now(), now()),
       ('신문사의 그림자', 60, 18000, 'ACTIVE', 'BOOK', 3, now(), now()),
       ('죄와 벌', 35, 22000, 'ACTIVE', 'BOOK', 3, now(), now()),
       ('러브앤리스트 크리스마스 선물 세트', 50, 50000, 'ACTIVE', 'ETC', 3, now(), now()),
       ('해리 포터 컬렉션', 40, 120000, 'ACTIVE', 'BOOK', 3, now(), now()),
       ('반지의 제왕 DVD 세트', 30, 80000, 'ACTIVE', 'ETC', 3, now(), now()),
       ('스포츠 자전거', 20, 300000, 'ACTIVE', 'ETC', 4, now(), now()),
       ('로봇 청소기', 25, 200000, 'ACTIVE', 'ELECTRONIC', 4, now(), now()),
       ('카페 아메리카노', 100, 5000, 'ACTIVE', 'FOOD', 4, now(), now()),
       ('Harry Potter and the Sorcerers Stone', 80, 15000, 'ACTIVE', 'BOOK', 4, now(), now()),
       ('The Great Gatsby', 65, 12000, 'ACTIVE', 'BOOK', 4, now(), now()),
       ('4K 스마트 TV', 10, 1800000, 'ACTIVE', 'ELECTRONIC', 4, now(), now()),
       ('맥북 프로 16인치', 20, 2500000, 'ACTIVE', 'ELECTRONIC', 4, now(), now()),
       ('Programming in Python', 90, 25000, 'ACTIVE', 'BOOK', 5, now(), now()),
       ('LG OLED TV', 20, 2500000, 'ACTIVE', 'ELECTRONIC', 5, now(), now()),
       ('컴퓨터 게임 콘솔', 30, 400000, 'ACTIVE', 'ETC', 5, now(), now()),
       ('정장 세트', 25, 100000, 'ACTIVE', 'CLOTHES', 5, now(), now()),
       ('수영복', 30, 40000, 'ACTIVE', 'CLOTHES', 5, now(), now()),
       ('키보드와 마우스 세트', 40, 30000, 'ACTIVE', 'ETC', 5, now(), now()),
       ('Canon EOS 5D Mark IV', 15, 3000000, 'ACTIVE', 'ELECTRONIC', 6, now(), now()),
       ('Fila Disruptor', 55, 120000, 'ACTIVE', 'CLOTHES', 6, now(), now()),
       ('스마트 워치', 50, 350000, 'ACTIVE', 'ELECTRONIC', 6, now(), now()),
       ('매트리스와 침대 세트', 15, 800000, 'ACTIVE', 'ETC', 6, now(), now()),
       ('비밀의 정원 화분', 20, 45000, 'ACTIVE', 'ETC', 7, now(), now()),
       ('Calvin Klein T-Shirt', 70, 50000, 'ACTIVE', 'CLOTHES', 7, now(), now()),
       ('레고 스타워즈 세트', 25, 80000, 'ACTIVE', 'ETC', 7, now(), now()),
       ('미니 냉장고', 10, 600000, 'ACTIVE', 'ETC', 7, now(), now()),
       ('치킨 버거', 30, 10000, 'ACTIVE', 'FOOD', 8, now(), now()),
       ('카페 라떼', 60, 5500, 'ACTIVE', 'FOOD', 8, now(), now()),
       ('스피커 시스템', 10, 350000, 'ACTIVE', 'ETC', 8, now(), now()),
       ('피아노 연주서', 40, 25000, 'ACTIVE', 'ETC', 8, now(), now()),
       ('신경과학 입문', 30, 35000, 'ACTIVE', 'BOOK', 8, now(), now()),
       ('포터블 그릴', 15, 80000, 'ACTIVE', 'ETC', 9, now(), now()),
       ('미니 스피커', 50, 25000, 'ACTIVE', 'ETC', 9, now(), now()),
       ('스티븐 킹 소설 모음', 40, 18000, 'ACTIVE', 'BOOK', 9, now(), now()),
       ('Python 프로그래밍 입문', 75, 25000, 'ACTIVE', 'BOOK', 9, now(), now()),
       ('화분과 다육식물 세트', 25, 35000, 'ACTIVE', 'ETC', 9, now(), now()),
       ('갤럭시 탭 S7', 10, 800000, 'ACTIVE', 'ELECTRONIC', 10, now(), now()),
       ('아이폰 12 미니', 45, 1000000, 'ACTIVE', 'ELECTRONIC', 10, now(), now()),
       ('스타워즈 컬렉션', 20, 45000, 'ACTIVE', 'ETC', 10, now(), now()),
       ('아이패드 프로', 25, 1200000, 'ACTIVE', 'ELECTRONIC', 10, now(), now());
