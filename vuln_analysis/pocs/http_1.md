# Vulnerability 13: Usage of insecure Protocol

- Vulnerability: Insecure Protocol
- Impact: Allows an attacker to sniff the communication. It will be able to steal cookies and highjack the session. It is even possible to capture the user credentials

## Steps to reproduce

1. Log in or Register as a new user
2. Check the cookie the server assigned to you (Simulates stealing the cookie)
3. In a new browser, access the FaceFive home page
4. Create a new cookie in that domain, called `session`
5. Set the copied value to the new cookie value
6. Access `/profile` endpoint

[(POC)](http_1.py)
