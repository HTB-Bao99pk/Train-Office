select * from users

UPDATE users
SET role = 'ADMIN'
WHERE role = 'Customer' AND email = 'anhnhu@gmail.com';