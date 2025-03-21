INSERT INTO users (id, email, password, first_name, last_name)
VALUES (3, 'email@email.com', 'password', 'firstName', 'lastName');

INSERT INTO users_roles(user_id, role_id)
VALUES (3, 2);

INSERT INTO cars (id,model, brand, type, inventory, daily_fee)
VALUES (1,'X6', 'BMW', 'SEDAN', 5, 100),
       (2,'X5', 'BMW', 'SUV', 3, 130),
       (3,'Golf', 'Volkswagen', 'HATCHBACK', 10, 60);

INSERT INTO rentals (id,rental_date, return_date, actual_return_date, car_id, user_id)
VALUES
    (1,'2025-03-16', '2025-03-20', NULL, 1, 3),
    (2,'2025-03-16', '2025-03-16', '2025-03-16', 2, 3),
    (3,'2025-03-16', '2025-03-27', NULL, 3, 3);