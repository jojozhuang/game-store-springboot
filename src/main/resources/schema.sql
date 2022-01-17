CREATE TABLE product (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(128) NOT NULL,
    price DOUBLE NOT NULL,
    image VARCHAR(1000) NOT NULL,
    PRIMARY KEY (id)
);