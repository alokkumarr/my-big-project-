-- Remove root access from outside of localhost
DELETE FROM mysql.user WHERE User='root'
  AND Host NOT IN ('localhost', '127.0.0.1', '::1');
-- Remove anonymous access
DELETE FROM mysql.user WHERE User='';
-- Remove test database
DELETE FROM mysql.db WHERE Db='test' OR Db='test_%';
-- Ensure privilege changes take effect
FLUSH PRIVILEGES;

-- Initialize SAW Security database
CREATE DATABASE saw_security;
