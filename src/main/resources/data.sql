

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