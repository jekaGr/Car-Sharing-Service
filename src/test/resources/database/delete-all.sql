DELETE FROM rentals WHERE user_id = 3;
DELETE FROM cars WHERE model IN ('X6', 'X5', 'Golf');
DELETE FROM users_roles WHERE user_id = 3;
DELETE FROM users WHERE id = 3;