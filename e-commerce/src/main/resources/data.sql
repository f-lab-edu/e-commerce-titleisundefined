SET foreign_key_checks = 0;
DROP TABLE IF EXISTS users cascade;
DROP TABLE IF EXISTS owners cascade;
DROP TABLE IF EXISTS shop cascade;
DROP TABLE IF EXISTS items cascade;
DROP TABLE IF EXISTS cart cascade;
DROP TABLE IF EXISTS cart_item cascade;
DROP TABLE IF EXISTS orders cascade;
DROP TABLE IF EXISTS order_item cascade;
DROP TABLE IF EXISTS payment cascade;
DROP TABLE IF EXISTS review cascade;
DROP TABLE IF EXISTS image_file cascade;
SET foreign_key_checks = 1;

CREATE TABLE users
(
    user_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    nickname    VARCHAR(50)  NOT NULL UNIQUE,
    status      VARCHAR(30)  NOT NULL,
    address     VARCHAR(100) NOT NULL,
    create_date timestamp(6) default current_timestamp(6),
    modify_date timestamp(6) default current_timestamp(6)
) charset = utf8;

CREATE TABLE owners
(
    owner_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    create_date timestamp(6) default current_timestamp(6),
    modify_date timestamp(6) default current_timestamp(6)
) charset = utf8;

CREATE TABLE shop
(
    shop_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    owner_id    BIGINT       NOT NULL,
    create_date timestamp(6) default current_timestamp(6),
    modify_date timestamp(6) default current_timestamp(6),
    FOREIGN KEY (owner_id) REFERENCES owners (owner_id)
) charset = utf8;

CREATE TABLE image_file
(
    image_file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_name VARCHAR(100) NOT NULL,
    virtual_name  VARCHAR(100) NOT NULL,
    create_date   timestamp(6) default current_timestamp(6),
    modify_date   timestamp(6) default current_timestamp(6)
) charset = utf8;

CREATE TABLE items
(
    item_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    stock         INT          NOT NULL,
    price         INT          NOT NULL,
    description   TEXT    ,
    status        VARCHAR(30)  NOT NULL,
    category      VARCHAR(30)  NOT NULL,
    shop_id       BIGINT       NOT NULL,
    image_file_id BIGINT       NOT NULL,
    create_date   timestamp(6) default current_timestamp(6),
    modify_date   timestamp(6) default current_timestamp(6),
    FOREIGN KEY (shop_id) REFERENCES shop (shop_id),
    FOREIGN KEY (image_file_id) REFERENCES image_file (image_file_id)
) charset = utf8;

CREATE TABLE cart
(
    cart_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    create_date timestamp(6) default current_timestamp(6),
    modify_date timestamp(6) default current_timestamp(6),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
) charset = utf8;

CREATE TABLE cart_item
(
    cart_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity     INT    NOT NULL,
    cart_id      BIGINT NOT NULL,
    item_id      BIGINT NOT NULL,
    create_date  timestamp(6) default current_timestamp(6),
    modify_date  timestamp(6) default current_timestamp(6),
    FOREIGN KEY (cart_id) REFERENCES cart (cart_id),
    FOREIGN KEY (item_id) REFERENCES items (item_id)
) charset = utf8;

CREATE TABLE orders
(
    order_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_status varchar(20) NOT NULL,
    user_id      BIGINT      NOT NULL,
    create_date  timestamp(6) default current_timestamp(6),
    modify_date  timestamp(6) default current_timestamp(6),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
) charset = utf8;

CREATE TABLE order_item
(
    order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_price   INT    NOT NULL,
    quantity      INT    NOT NULL,
    order_id      BIGINT NOT NULL,
    item_id       BIGINT NOT NULL,
    create_date   timestamp(6) default current_timestamp(6),
    modify_date   timestamp(6) default current_timestamp(6),
    FOREIGN KEY (order_id) REFERENCES orders (order_id),
    FOREIGN KEY (item_id) REFERENCES items (item_id)
) charset = utf8;

CREATE TABLE payment
(
    payment_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    point       INT    NOT NULL,
    user_id     BIGINT NOT NULL,
    create_date timestamp(6) default current_timestamp(6),
    modify_date timestamp(6) default current_timestamp(6),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
) charset = utf8;

CREATE TABLE review
(
    review_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    content       varchar(200)  NOT NULL,
    score         numeric(2, 1) NOT NULL,
    review_status varchar(30)   NOT NULL,
    user_id       BIGINT        NOT NULL,
    item_id       BIGINT        NOT NULL,
    order_item_id BIGINT        NOT NULL,
    create_date   timestamp(6) default current_timestamp(6),
    modify_date   timestamp(6) default current_timestamp(6),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (item_id) REFERENCES items (item_id),
    FOREIGN KEY (order_item_id) REFERENCES order_item (order_item_id)
) charset = utf8;


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

INSERT INTO image_file (original_name, virtual_name)
VALUES ('iPhone 13 Pro', '2d5ea7b8-4d7c-470a-bd91-fbd0e4324135.jpeg'),
       ('MacBOOK Air', 'd2ade6ea-a59a-42c7-bd7f-796cc0052c32.jpeg'),
       ('나이키 운동화', 'e9cdaa48-6a47-4901-a241-5c74c082062c.jpeg'),
       ('H&M 셔츠', 'ccd7ee73-bdd2-4492-80dd-d5351fd48f0f.jpeg'),
       ('톰브라운 수트', '0b360f4d-5859-41af-95b0-63b188681e4c.jpeg'),
       ('Samsung Galaxy S21', '45248549-3bed-44a8-9413-6692474c0f61.jpeg'),
       ('Adidas Ultraboost', '25266f45-89ec-4341-a3b9-e4c71fff5b7e.jpeg'),
       ('한우 스테이크', '51bd705a-48c0-4cc4-9a68-ba727d97a82a.jpeg'),
       ('초콜릿 케이크', 'b030917f-2ad2-47dc-b8fd-90d5cff0b8ee.jpeg'),
       ('샐러드 바 세트', '350e757c-760e-4e12-b745-27aef1d9cf42.jpeg'),
       ('죄와 벌', 'ac81cf13-0bd9-49b2-b8c5-4ba2632e5adb.jpeg'),
       ('카페 아메리카노', '74adb72f-a2bb-4d39-9ff9-488b2fadd990.jpeg'),
       ('스타워즈 컬렉션', '1e6b5990-a55f-4ebd-8034-da29d31209ff.jpeg');

INSERT INTO items (name, stock, price, status, category, shop_id, create_date, modify_date, image_file_id)
VALUES ('iPhone 13 Pro', 50, 1200000, 'ACTIVE', 'ELECTRONIC', 1, now(), now(), 1),
       ('MacBOOK Air', 30, 1500000, 'ACTIVE', 'ELECTRONIC', 2, now(), now(), 2),
       ('나이키 운동화', 50, 150000, 'ACTIVE', 'CLOTHES', 3, now(), now(), 3),
       ('H&M 셔츠', 30, 30000, 'ACTIVE', 'CLOTHES', 4, now(), now(), 4),
       ('톰브라운 수트', 10, 500000, 'ACTIVE', 'CLOTHES', 5, now(), now(), 5),
       ('Samsung Galaxy S21', 45, 1000000, 'ACTIVE', 'ELECTRONIC', 6, now(), now(), 6),
       ('Adidas Ultraboost', 60, 180000, 'ACTIVE', 'CLOTHES', 7, now(), now(), 7),
       ('한우 스테이크', 15, 40000, 'ACTIVE', 'FOOD', 8, now(), now(), 8),
       ('초콜릿 케이크', 25, 25000, 'ACTIVE', 'FOOD', 9, now(), now(), 9),
       ('샐러드 바 세트', 15, 15000, 'ACTIVE', 'FOOD', 10, now(), now(), 10),
       ('죄와 벌', 35, 22000, 'ACTIVE', 'BOOK', 1, now(), now(), 11),
       ('카페 아메리카노', 100, 5000, 'ACTIVE', 'FOOD', 2, now(), now(), 12),
       ('스타워즈 컬렉션', 20, 45000, 'ACTIVE', 'ETC', 3, now(), now(), 13);
