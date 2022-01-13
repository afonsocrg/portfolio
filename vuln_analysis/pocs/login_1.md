# Vulnerability 1: SQL Injection in login form allows to login as any user

- Vulnerability: SQL Injection
- Where: `username` in login form
- Impact: Allows reading any user's information. Allows to edit user's posts content and privacy scope. Allows to see and manage friends

## Steps to reproduce

1. Insert `username` = `{username}'#` and `password` = `{any}` in login form
2. Access `Profile (admin)`
3. We can access any information from the logged in user
4. We can edit user's existing posts content and privacy scope
5. We can see and manage friends

[(POC)](login_1.py)
