-- Active: 1717998486542@@127.0.0.1@3306@hotelRoom


-------------------
------测试连接
-------------------
show tables;

-------------------
------创建库
-------------------
CREATE IF NOT EXISTS DATABASE hotelRoom ;

-------------------
------创建房间表
-------------------
DROP TABLE IF EXISTS rooms;

CREATE TABLE rooms (
    id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    location VARCHAR(30) NOT NULL,
    type VARCHAR(30) NOT NULL,
    area FLOAT NOT NULL COMMENT 'Area in square meters',
    airConditioner BOOLEAN DEFAULT TRUE,
    waterHeater BOOLEAN DEFAULT TRUE,
    status TEXT CHECK (
        status IN (
            'Vacant',
            'Occupied',
            'Under maintenance',
            'To be cleaned'
        )
    ),
    price DOUBLE NOT NULL
);
INSERT INTO rooms (location, type, area, airConditioner, waterHeater, status, price) VALUES
    ('Main Building', 'King Bed', 25, TRUE, TRUE, 'Vacant', 100.00),
    ('Main Building', 'Single Bed', 15, TRUE, FALSE, 'Vacant', 40.00),
    ('Main Building', 'Double Bed', 20, TRUE, TRUE, 'Vacant', 75.00),
    ('Annex Building', 'Presidential Suite', 100, TRUE, TRUE, 'Vacant', 500.00),
    ('Main Building', 'King Bed', 25, TRUE, TRUE, 'Occupied', 100.00),
    ('Main Building', 'Single Bed', 15, TRUE, TRUE, 'Under maintenance', 50.00),
    ('Main Building', 'Double Bed', 20, TRUE, TRUE, 'To be cleaned', 75.00),
    ('Annex Building', 'Presidential Suite', 100, TRUE, TRUE, 'Vacant', 500.00),
    ('Main Building', 'King Bed', 25, TRUE, TRUE, 'Vacant', 100.00),
    ('Main Building', 'Single Bed', 15, TRUE, TRUE, 'Vacant', 50.00),
    ('Main Building', 'Double Bed', 20, TRUE, TRUE, 'Vacant', 75.00),
    ('Annex Building', 'Presidential Suite', 100, TRUE, TRUE, 'Vacant', 500.00);




-------------------
------服务表
-------------------
DROP TABLE IF EXISTS services;

CREATE TABLE services (
    id TINYINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    price DOUBLE NOT NULL
);

INSERT INTO services (name, description, price) VALUES
    ('Room Service', 'Order meals and drinks to be delivered to your room.', 15.00),
    ('Laundry Service', 'Have your clothes washed, dried, and pressed.', 10.00),
    ('Spa Treatments', 'Relax and rejuvenate with a variety of massage and beauty treatments.', 50.00),
    ('Airport Transfer', 'Book a comfortable and reliable airport transfer service.', 30.00),
    ('Concierge Service', 'Get personalized assistance with reservations, tickets, and local recommendations.', 0.00),
    ('Babysitting', 'Enjoy a night out while your children are cared for by our experienced babysitters.', 25.00),
    ('In-Room Dining', 'Enjoy a romantic dinner or a private meal experience in your room.', 40.00),
    ('Pet Sitting', 'Our pet sitters will provide loving care for your furry friend while you are away.', 15.00),
    ('Late Checkout', 'Enjoy an extended stay in your room with a late checkout option.', 30.00),
    ('Breakfast in Bed', 'Wake up to a delicious breakfast delivered right to your bed.', 12.00),
    ('Minibar Service', 'Stock your minibar with your favorite drinks and snacks.', 10.00),
    ('Housekeeping', 'Enjoy daily housekeeping services to keep your room tidy and comfortable.', 0.00),
    ('Valet Parking', 'Leave your car in our care with our valet parking service.', 20.00),
    ('Fitness Center Access', 'Stay active with access to our fully equipped fitness center.', 10.00),
    ('Swimming Pool Access', 'Relax and cool off in our refreshing swimming pool.', 0.00);





-------------------
------顾客表
-------------------
DROP TABLE IF EXISTS customers;

CREATE TABLE customers (
    id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL,
    gender VARCHAR(6) NOT NULL CHECK (gender IN ('Male', 'Female')),
    idNumber CHAR(18) NOT NULL UNIQUE CHECK (LENGTH(idNumber) = 18),
    phoneNumber CHAR(11) NOT NULL UNIQUE CHECK (
    LENGTH(phoneNumber) = 18 and not phoneNumber REGEXP '[^0-9]')
);

INSERT INTO customers (id, name, gender, idNumber, phoneNumber) VALUES
    (1, 'John Doe', 'Male', '123456789012345678', '1234567890'),
    (2, 'Jane Smith', 'Female', '987654321012345678', '9876543210'),
    (3, 'Robert Jones', 'Male', '111111111111111111', '1111111111'),
    (4, 'Mary Brown', 'Female', '222222222222222222', '2222222222');




-------------------
------交易表
-------------------
DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    paymentType VARCHAR(20) NOT NULL CHECK (
        paymentType IN ('wechat','alipay','cash','bankCard')
    ),
    paymentDescription TEXT,
    paymentTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    customerId SMALLINT UNSIGNED NOT NULL,
    FOREIGN KEY (customerId) REFERENCES customers (id) ON UPDATE CASCADE ON DELETE CASCADE,
    amountDue DOUBLE NOT NULL,
    amountPaid DOUBLE NOT NULL
);

INSERT INTO transactions (paymentType, paymentDescription, customerId, amountDue, amountPaid) VALUES
    ('wechat', 'Room Service order #123', 1, 50.00, 50.00),
    ('alipay', 'Spa treatment for two', 2, 100.00, 100.00),
    ('cash', 'Late checkout fee', 3, 30.00, 30.00),
    ('bankCard', 'Airport transfer', 4, 35.00, 35.00),
    ('wechat', 'Breakfast in bed', 1, 12.00, 12.00),
    ('alipay', 'Laundry service', 2, 15.00, 15.00);



-------------------
------入住表
-------------------

DROP TABLE IF EXISTS checkIns;


CREATE TABLE checkIns (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    customerId SMALLINT UNSIGNED,
    roomId SMALLINT UNSIGNED UNIQUE,
    checkInTime DATETIME,
    checkOutTime DATETIME,
    FOREIGN KEY (customerId) REFERENCES customers (id) ON UPDATE CASCADE ON DELETE SET NULL,
    FOREIGN KEY (roomId) REFERENCES rooms (id) ON UPDATE CASCADE ON DELETE SET NULL
);


INSERT INTO checkIns (customerId, roomId, checkInTime, checkOutTime) VALUES
    (2, 3, '2023-10-27 16:30:00', '2023-10-29 10:00:00'),
    (3, 2, '2023-10-28 11:00:00', '2023-10-30 14:00:00');



-------------------
--触发器1,用于校验金额
-------------------

DROP TRIGGER IF EXISTS checkAmountsBeforeInsertUpdate;
CREATE TRIGGER amountsVerifier
BEFORE INSERT ON transactions
FOR EACH ROW
BEGIN
    IF NEW.amountDue > NEW.amountPaid THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Error: amountDue cannot be greater than amountPaid';
    END IF;
END