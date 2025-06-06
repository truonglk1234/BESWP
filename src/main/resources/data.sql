INSERT INTO [dbo].[Role] (role_id, role_name, description)
SELECT 1, 'Admin', 'Administrator role'
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Role] WHERE role_name = 'Admin');

INSERT INTO [dbo].[Role] (role_id, role_name, description)
SELECT 2, 'Customer', 'Customer role for registered users'
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Role] WHERE role_name = 'Customer');

INSERT INTO [dbo].[Role] (role_id, role_name, description)
SELECT 3, 'Consultant', 'Consultant role'
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Role] WHERE role_name = 'Consultant');

INSERT INTO [dbo].[Role] (role_id, role_name, description)
SELECT 4, 'Staff', 'Staff role'
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Role] WHERE role_name = 'Staff');


-- SỬA TÊN BẢNG Ở ĐÂY CHO USERSTATUS
INSERT INTO [dbo].[User_Status] (status_id, status_name, description)
SELECT 1, 'Active', 'User account is active'
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[User_Status] WHERE status_name = 'Active');

INSERT INTO [dbo].[User_Status] (status_id, status_name, description)
SELECT 2, 'Inactive', 'User account is inactive'
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[User_Status] WHERE status_name = 'Inactive');

INSERT INTO [dbo].[User_Status] (status_id, status_name, description)
SELECT 3, 'Pending', 'User account is pending verification/approval'
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[User_Status] WHERE status_name = 'Pending');

-- Insert sample users with their profiles
-- Admin user
INSERT INTO [dbo].[User] (userphone, password, email, role_id, status_id)
SELECT '0123456789', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'admin@example.com', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE userphone = '0123456789');

INSERT INTO [dbo].[Profile] (user_id, full_name, gender, date_of_birth, address)
SELECT u.user_id, 'Admin User', 1, '1990-01-01', '123 Admin Street'
FROM [dbo].[User] u
WHERE u.userphone = '0123456789'
AND NOT EXISTS (SELECT 1 FROM [dbo].[Profile] WHERE user_id = u.user_id);

-- Customer user
INSERT INTO [dbo].[User] (userphone, password, email, role_id, status_id)
SELECT '0987654321', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'customer@example.com', 2, 1
WHERE NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE userphone = '0987654321');

INSERT INTO [dbo].[Profile] (user_id, full_name, gender, date_of_birth, address)
SELECT u.user_id, 'Customer User', 0, '1995-05-15', '456 Customer Avenue'
FROM [dbo].[User] u
WHERE u.userphone = '0987654321'
AND NOT EXISTS (SELECT 1 FROM [dbo].[Profile] WHERE user_id = u.user_id);

-- Staff user
INSERT INTO [dbo].[User] (userphone, password, email, role_id, status_id)
SELECT '0369852147', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'staff@example.com', 4, 1
WHERE NOT EXISTS (SELECT 1 FROM [dbo].[User] WHERE userphone = '0369852147');

INSERT INTO [dbo].[Profile] (user_id, full_name, gender, date_of_birth, address)
SELECT u.user_id, 'Staff User', 1, '1992-08-20', '789 Staff Road'
FROM [dbo].[User] u
WHERE u.userphone = '0369852147'
AND NOT EXISTS (SELECT 1 FROM [dbo].[Profile] WHERE user_id = u.user_id);