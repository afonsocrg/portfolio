# Vulnerability 8: SQL injection in update post form allows arbitrary read

- Vulnerability: SQL injection
- Where: `content` field
- Impact: Allows user to read arbitrary value from the database

## Steps to reproduce

1. Change post content to `', content=(SELECT password from Users WHERE username='ssofadmin')#`
2. The result will appear in the content of our post

As we did [here](update_profile_2.py), we can write more complex queries to read arbitrary values from the database

[(POC)](edit_post_3.py)
