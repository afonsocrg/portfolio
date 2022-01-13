# Vulnerability 7: SQL injection in URL parameter

- Vulnerability: SQL injection
- Where: `id` parameter in request URL
- Impact: Allows user to read arbitrary data from the database

## Steps to reproduce

1. Get to "<url>/edit\_post?id=' and 1=0 union select 1,2,password,4,5,6 from Users where username = 'ssofadmin"

## Notes 
Note that we can change the column and table we are reading from.
It is mandatory that the query returns 1 row only. Otherwise the server will output an error
There are other easier ways to read arbitrary data from the database, but this way still allows us to do so.
We can build more complex queries (as we did [here](profile_2.md)) to read more rows/columns

[(POC)](edit_post_2.py)
