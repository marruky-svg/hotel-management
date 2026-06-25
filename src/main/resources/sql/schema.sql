CREATE TABLE roles
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
);

CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    active     BOOLEAN   DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE user_roles
(
    user_id INT REFERENCES users (id),
    role_id INT REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);



CREATE TABLE people
(
    id         SERIAL PRIMARY KEY,
    user_id    INT REFERENCES users (id),
    name       VARCHAR(150) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    contact    VARCHAR(20),
    nif        VARCHAR(20)  NOT NULL UNIQUE,
    type       VARCHAR(30)  NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

ALTER TABLE people
    ADD CONSTRAINT chk_people_type
        CHECK ( type IN ('CLIENT', 'VIP_CLIENT', 'ENTERPRISE_CLIENT', 'RECEPTIONIST', 'MANAGER'));

CREATE TABLE clients
(
    id             SERIAL PRIMARY KEY,
    person_id      INT NOT NULL REFERENCES people (id),
    loyalty_points INT           DEFAULT 0,
    enterprise     VARCHAR(150),
    discount       DECIMAL(5, 2) DEFAULT 0.00
);

ALTER TABLE clients
    DROP COLUMN discount;

CREATE TABLE staff
(
    id        SERIAL PRIMARY KEY,
    person_id INT            NOT NULL REFERENCES people (id),
    position  VARCHAR(30)    NOT NULL,
    salary    DECIMAL(10, 2) NOT NULL,
    shift     VARCHAR(20)
);

CREATE TABLE rooms
(
    id          SERIAL PRIMARY KEY,
    number      VARCHAR(10)    NOT NULL UNIQUE,
    type        VARCHAR(30)    NOT NULL,
    night_price DECIMAL(10, 2) NOT NULL,
    capacity    INT            NOT NULL,
    available   BOOLEAN DEFAULT TRUE,
    description VARCHAR(300),
    floor       INT
);


CREATE TABLE reservations
(
    id             SERIAL PRIMARY KEY,
    client_id      INT  NOT NULL REFERENCES clients (id),
    room_id        INT  NOT NULL REFERENCES rooms (id),
    staff_id       INT REFERENCES staff (id),
    check_in_date  DATE NOT NULL,
    check_out_date DATE NOT NULL,
    num_nights     INT GENERATED ALWAYS AS (check_out_date - check_in_date) STORED,
    state          VARCHAR(20) DEFAULT 'PENDING',
    observations   VARCHAR(500),
    created_at     TIMESTAMP   DEFAULT now()
);
ALTER TABLE reservations
    ADD CONSTRAINT chk_reservations_state
        CHECK (state IN ('PEDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED'));

ALTER TABLE reservations
    ALTER COLUMN staff_id DROP NOT NULL;


CREATE TABLE services
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100)   NOT NULL,
    type        VARCHAR(30)    NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    description VARCHAR(300)
);

CREATE TABLE services_reservations
(
    id         SERIAL PRIMARY KEY,
    reserve_id INT REFERENCES reservations (id),
    service_id INT REFERENCES services (id),
    amount     INT       DEFAULT 1,
    order_date TIMESTAMP DEFAULT now()
);

CREATE TABLE room_services
(
    id            SERIAL PRIMARY KEY,
    service_id    INT NOT NULL REFERENCES services (id),
    delivery_time TIMESTAMP,
    room_number   VARCHAR(15)
);

CREATE TABLE invoices
(
    id             SERIAL PRIMARY KEY,
    reserve_id     INT            NOT NULL REFERENCES reservations (id) UNIQUE,
    total_rooms    DECIMAL(10, 2) NOT NULL,
    total_services DECIMAL(10, 2) DEFAULT 0.00,
    discount       DECIMAL(10, 2) DEFAULT 0.00,
    total_final    DECIMAL(10, 2) NOT NULL,
    issued_at      TIMESTAMP      DEFAULT now()
);

CREATE TABLE payments
(
    id         SERIAL PRIMARY KEY,
    invoice_id INT            NOT NULL REFERENCES invoices (id),
    type       VARCHAR(20)    NOT NULL,
    price      DECIMAL(10, 2) NOT NULL,
    state      VARCHAR(20) DEFAULT 'PENDING',
    reference  VARCHAR(100),
    paid_at    TIMESTAMP
);

SELECT *
FROM users;
SELECT *
FROM people;
SELECT *
FROM clients;
SELECT *
FROM staff;
SELECT *
FROM rooms;

SELECT id, number, available FROM rooms WHERE id = 1;

INSERT INTO services (name, type, price, description) VALUES
                                                          ('Spa Session', 'SPA', 50.00, 'Relaxing spa session'),
                                                          ('Laundry', 'LAUNDRY', 10.00, 'Laundry service'),
                                                          ('Airport Transfer', 'AIRPORT_TRANSFER', 30.00, 'Transfer to airport');

INSERT INTO reservations (client_id, room_id, check_in_date, check_out_date, state)
VALUES (1, 1, CURRENT_DATE, CURRENT_DATE + 2, 'CONFIRMED');


SELECT conname, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'reservations'::regclass;

SELECT pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'reservations'::regclass
  AND conname = 'chk_reservations_state';



TRUNCATE TABLE services_reservations, invoices, payments,
    reservations, clients, staff, people,
    users, rooms, services RESTART IDENTITY CASCADE;