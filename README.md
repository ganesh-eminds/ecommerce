# ecommerce
Sample Ecommerce Application

-- ROLES
INSERT INTO roles (id, name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'ROLE_USER'),
    ('00000000-0000-0000-0000-000000000002', 'ROLE_ADMIN');

-- USERS
INSERT INTO users (
    id, first_name, username, email, password, enabled, payment_order_ids, order_ids, balance
) VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Alice',
    'alice123',
    'alice@example.com',
    'password123',
    true,
    ARRAY['88888888-8888-8888-8888-888888888888'::UUID],
    ARRAY['99999999-9999-9999-9999-999999999999'::UUID],
    100000.0
);

-- USER_ROLES
INSERT INTO user_roles (user_id, role_id) VALUES
    ('11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000001');


INSERT INTO coupon (id, code, description, discount_amount, min_order_value, first_order_only, expiry_date, usage_limit, total_uses)
VALUES (gen_random_uuid(), 'WELCOME50', '₹50 off on first order', 50, 100, true, NOW() + INTERVAL '7 days', 1, 0);

INSERT INTO coupon (id, code, description, discount_percent, max_discount, min_order_value, expiry_date, usage_limit, total_uses, first_order_only)
VALUES (gen_random_uuid(), 'SAVE20', '20% off up to ₹200', 20, 200, 500, NOW() + INTERVAL '10 days', 100, 0, false);


