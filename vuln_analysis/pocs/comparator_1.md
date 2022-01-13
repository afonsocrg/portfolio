# Vulnerability 15: SQL equality function is case insensitive

- Vulnerability: SQL `=` operator is case insensitive
- Impact: Allows login with invalid password

## Steps to reproduce

1. Register with username `asdf` and password `asdf`
2. Logout
3. Login with username `asdf` and password `ASDF`

[(POC)](comparator_1.py)
