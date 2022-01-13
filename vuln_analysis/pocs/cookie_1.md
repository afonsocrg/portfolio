# Vulnerability 14: Cookie contains user information

- Vulnerability: Sensitive information may be reachable for users
- Where: Session cookie
- Impact: Allows anyone who has the cookie to read it's data

## Steps to reproduce

1. Login and get the `session` cookie
2. Split the cookie value by `.` and get the first element
3. This element is a b64 encoded JSON. Decode it
4. Now the server information is visible by the user

## Notes

After analyzing the cookie, we found that it is split into three parts (separated by `.`).
The first one is a JSON object encoded in base64, containing data that will be later used by the server. When we tried to change it, in an attempt to impersonate another user, the server redirected us to the login page. The two other fields must be useful to check the first's integrity. We proceeded to analyze them.

The second field appears to be a timestamp, since its variation differs depending on the time between getting generated: The more time we waited, the greater the difference between these values. 

```
    25/10/2020-9:17:??: X5VCrQ
    25/10/2020-9:24:??: X5VEcA (07m: changed last 3 characters)
    25/10/2020-9:26:00: X5VEqA (02m: changed last 2 characters)
    25/10/2020-9:26:17: X5VEtw (17s: changed last 2 characters, with smaller interval)
    25/10/2020-9:27:01: X5VE5A (42s: changed last 2 characters, with bigger interval)
    25/10/2020-9:26:11: X5VE7g (10s: changed last 2 characters, with small interval)
```

Unfortunately we weren't able to find the correct encoding of this field.

Regarding the last field, we believe it is a signature/hash since it varies a lot and "randomly". It may contain just the first two fields, but it is possible to have a server's secret in it.

In the best scenario, where we would find the timestamp encoding format (if that hunch is right) and the hash (and in the case the cookie wasn't signed), we would be able to impersonate any other user, by forging valid cookies. This would represent a severe security risk.
Ideally the cookie wouldn't contain any meaningful information about the server state or the client.

[(POC)](cookie_1.py)
