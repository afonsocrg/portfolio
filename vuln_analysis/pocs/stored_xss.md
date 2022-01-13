# Vulnerability 16: Stored XSS

- Vulnerability: XSS (Stored)
- Where:
Bad input may be injected in:

 * User's name
 * User's username
 * User's about
 * User's photo (filename)
 * Post content (While creating or updating post)

Bad input may be executed in:

 * Post list (home page)
 * Friend List
 * Pending Friend Request List

Since there are no posts shown in the Friend List and Pending Friend Request List, the post content XSS won't be executed in those endpoints.

- Impact: This vulnerability allows an attacker to execute arbitrary code in the victim's browser.

This full (there are no restrictions) XSS allows the atacker to execute ANY code segment in the victim's browser. This grants many exploitation possibilities:

 * Rick Roll your friends [POC](xss_2.py)
 * Send cookies to an attacker's controlled website (it will allow the attacker to steal the victim's session)
 * Redirect to a phishing website
    * This website would be a copy of the login page and would prompt the victim's credentials
    * It would then send the credentials to the facefive server and get a session cookie
    * It would redirect the client to the facefive home page with the new cookie
 * Perform any action on the user's behalf (since we have the user's cookie)
    * Create a new post [POC](xss_1.py)
    * Accept/Send friend requests
    * ...

## Steps to reproduce

1. Write post with content `<script>alert()</script>`
2. Open homepage. It will display an alert

More generically, you can follow these steps:
1. Insert a script in one of the injectable fields mentioned above
2. Make the victim's website display your bad input (either by opening your friend request or reading your post or seeing your profile in its friends list)
3. The script will be executed in the victim's browser.


## Notes
External Server = http://nexus.rnl.tecnico.ulisboa.pt:3000
