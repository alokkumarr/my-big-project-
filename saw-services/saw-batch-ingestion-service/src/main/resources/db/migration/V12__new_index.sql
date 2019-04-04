DROP INDEX IF EXISTS bis_file_name_index ON bis_file_logs;
CREATE INDEX bis_file_name_index ON bis_file_logs(bis_file_name(1000));
