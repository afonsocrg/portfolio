# Vulnerability 2: SQL Injection in login form allows to check if table exists in database

- Vulnerability: SQL Injection
- Where: `username` in login form
- Impact: Allows testing the existance of a database table

## Steps to reproduce

1. Insert `username` = `"' UNION SELECT * from {tablename}#"` and `password` = `{any}` in login form
2. If `{tablename}` is not a valid table, it will be shown an error saying that the table does not exist, otherwise, it will be shown a message saying that exists

[(POC)](login_2.py)
