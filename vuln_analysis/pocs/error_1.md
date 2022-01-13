# Vulnerability 19: Error Handling of the input fields allows to get information from the system

- Vulnerability: Bad Error Handling
- Where: 
    - Add Friend input field
    - Search My Friends
    - New and Edit Post
    - Login
    - Register 
    - Update profile -> any input field except `password`
- Impact: Allows to get information from the system

## Steps to reproduce

1. Add Friend:
    1.1. Insert in the add friend input field the following payload: `ssofadmin' #`
2. It is able to see that the database management system is MySQL
3. Update Profile
    3.1. Insert in the name input field the following payload: `' WHERE username='ssofadmin' #`
2. It is able to see that the name of the databse is 'facefivedb' and that the column username of table Users is a foreign key in username2 of Friends table.

[(POC)](error_1.py)
