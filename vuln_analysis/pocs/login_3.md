# Vulnerability 3: SQL Injection in login form allows to check table columns

- Vulnerability: SQL Injection
- Where: `username` in login form
- Impact: Allows infering the database schema

## Steps to reproduce

1. Insert `username` = `' or 1 = 1 UNION SELECT NULL, NULL, NULL, NULL, {colname} from {tablename}#` and `password` = `{any}` in login form
2. If the response contains a SQL error, then the column does not exist, otherwise the column exists

[(POC)](login_3.py)
