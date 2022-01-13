# Vulnerability 4: SQL Injection in name form allows to change user's own username and to change the password of another user

- Vulnerability: SQL Injection
- Where: Update profile -> any input field except `password`
- Impact: Allows to change all the information (username, name, password, about) from any user


## Steps to reproduce

1. Update user's profile:
  1.1. Change one of the fields (except password) to the following payload
    `{new_attacker_name}', username='{new_attacker_username}' where username='{attacker_username'#`
2. Logout (We need to get a new cookie to the new username)
3. Login as the new username with the same password (If not changed in the /update\_profile)
4. Update user's profile:
  4.1. Change one of the fields (except password) to the following payload:
    `', username='{victim_username}', password='{new_password}' where username='{victim_username}'#`
5. Logout 
6. Login as the victim username with the new password 


## Limitations
This exploit won't work when changing the username if the user has already written a post, accepted, received or sent friend requests.
This happens because the username is a primary key and will be referenced by other tables if the above situations. The database is configured to restrict the query if the foreign key constraint is violated


## Notes
It is also possible to inject any script in other user's profile (name, username, photo or about), triggering an XSS vulnerability in the victim's browser.

[(POC)](profile_1.py)
