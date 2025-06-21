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

INSERT INTO [dbo].[Role] (role_id, role_name, description)
SELECT 5, 'Manager', 'Manager role'
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[Role] WHERE role_name = 'Manager');


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
INSERT INTO [dbo].[users] (userphone, password, email, role_id, status_id)
SELECT '0123456789', '123456789', 'admin@example.com', 1, 1
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE userphone = '0123456789');

INSERT INTO [dbo].[Profile] (user_id, full_name, gender, date_of_birth, address)
SELECT u.user_id, 'Admin User', 1, '1990-01-01', '123 Admin Street'
FROM [dbo].[users] u
WHERE u.userphone = '0123456789'
  AND NOT EXISTS (SELECT 1 FROM [dbo].[Profile] WHERE user_id = u.user_id);

INSERT INTO [dbo].[users] (userphone, password, email, role_id, status_id)
SELECT '9999999999', '123456789', 'consultant@example.com', 3, 1
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE userphone = '9999999999');

INSERT INTO [dbo].[Profile] (user_id, full_name, gender, date_of_birth, address)
SELECT u.user_id, 'Consultant User', 1, '1991-01-01', '123 Admin Street'
FROM [dbo].[users] u
WHERE u.userphone = '9999999999'
  AND NOT EXISTS (SELECT 1 FROM [dbo].[Profile] WHERE user_id = u.user_id);


-- Customer user
INSERT INTO [dbo].[users] (userphone, password, email, role_id, status_id)
SELECT '0987654321', '123456789', 'customer@example.com', 2, 1
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE userphone = '0987654321');

INSERT INTO [dbo].[Profile] (user_id, full_name, gender, date_of_birth, address)
SELECT u.user_id, 'Customer User', 0, '1995-05-15', '456 Customer Avenue'
FROM [dbo].[users] u
WHERE u.userphone = '0987654321'
  AND NOT EXISTS (SELECT 1 FROM [dbo].[Profile] WHERE user_id = u.user_id);

-- Staff user
INSERT INTO [dbo].[users] (userphone, password, email, role_id, status_id)
SELECT '0369852147', '123456789', 'staff@example.com', 4, 1
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE userphone = '0369852147');

INSERT INTO [dbo].[Profile] (user_id, full_name, gender, date_of_birth, address)
SELECT u.user_id, 'Staff User', 1, '1992-08-20', '789 Staff Road'
FROM [dbo].[users] u
WHERE u.userphone = '0369852147'
  AND NOT EXISTS (SELECT 1 FROM [dbo].[Profile] WHERE user_id = u.user_id);

INSERT INTO [dbo].[users] (userphone, password, email, role_id, status_id)
SELECT '0369852000', '123456789', 'manager@example.com', 5, 1
    WHERE NOT EXISTS (SELECT 1 FROM [dbo].[users] WHERE userphone = '0369852000');

INSERT INTO [dbo].[Profile] (user_id, full_name, gender, date_of_birth, address)
SELECT u.user_id, 'Manager User', 1, '1992-08-20', '789 Staff Road'
FROM [dbo].[users] u
WHERE u.userphone = '0369852000'
  AND NOT EXISTS (SELECT 1 FROM [dbo].[Profile] WHERE user_id = u.user_id);


UPDATE [users]
SET enabled = 1
WHERE enabled IS NULL;