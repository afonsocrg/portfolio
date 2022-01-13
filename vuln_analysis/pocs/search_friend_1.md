# Vulnerability 12: SQL Injection in search friend form allows arbitrary read from database

- Vulnerability: SQL Injection
- Where: Search friend form field
- Impact: Allows arbitrary read from the database


## Steps to reproduce

1. Search for friend:
```
' AND 1=0
UNION 
    SELECT username, name, name, password, password 
    FROM Users #
```


## Notes
The POC demonstrates that it is possible to write an arbitrary `SELECT` query in this injection. As shown [here](profile_2.md), it is possible to read arbitrary data from the database

[POC](search_friend_1.py)
