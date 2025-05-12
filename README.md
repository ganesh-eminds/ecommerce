# ecommerce
Sample Ecommerce Application



-- ========================
-- INSERT MOCK DATA
-- ========================

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

-- NEW

INSERT INTO coupon (
    id, code, description, discount_amount, discount_percentage,
    minimum_purchase_amount, expiry_date, max_usages_per_user,
    first_order_only, active
)
-- id,active,code,description,discount_amount,discount_percentage,expiry_date,first_order_only,max_global_usages,,
VALUES
-- Flat Discount ₹100 off
('11111111-1111-1111-1111-111111111111', 'FLAT100', '₹100 off on ₹500+', 100.0, NULL, 500.0, '2025-12-31', 100,  false, true),

-- 10% off on orders above ₹1000
('22222222-2222-2222-2222-222222222222', 'PERC10', '10% off on ₹1000+', NULL, 10.0, 1000.0, '2025-12-31', 100, false, true),

-- First order 25% off
('33333333-3333-3333-3333-333333333333', 'FIRST25', '25% off on first order', NULL, 25.0, 0.0, '2025-12-31', 1, false, true),

-- User specific ₹50 off
('44444444-4444-4444-4444-444444444444', 'USER50', '₹50 off for special user', 50.0, NULL, 100.0, '2025-12-31', 1, false, true),

-- Expired Coupon
('55555555-5555-5555-5555-555555555555', 'EXPIRED10', 'Old offer 10% off', NULL, 10.0, 100.0, '2023-12-31', 100, false, true),

-- Limited usage coupon
('66666666-6666-6666-6666-666666666666', 'LIMITED5', '₹25 off, max 5 uses', 25.0, NULL, 200.0, '2025-12-31', 5, false, true);


