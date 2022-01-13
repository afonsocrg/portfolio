# Vulnerability Analysis (September-November 2020)

This project consists of a black-box vulnerability analysis of a toy web application desingned for this purpose.
The vulnerabilities are listed below, and each contains a description and Proof of Concept in the respective link.

Group Image available [here](http://56cb2c11fc0868ed95fe9739052b47bc7770cb37b263113029d198840253.project.ssof.rnl.tecnico.ulisboa.pt/)

### Login Vulnerabilities
- **Login Vulnerabilities**: 
  - Vulnerability 1: SQL Injection in login form allows to login as any user [(link)](pocs/login_1.md)
  - Vulnerability 2: SQL Injection in login form allows to check if table exists in database [(link)](pocs/login_2.md)
  - Vulnerability 3: SQL Injection in login form allows to check table columns [(link)](pocs/login_3.md)


- **Profile Vulnerabilities**:
  - Vulnerability 4: SQL Injection in name form allows to change user's own username and to change the password of another user [(link)](pocs/profile_1.md)
  - Vulnerability 5: SQL Injection in name form allows arbitrary read from database [(link)](pocs/profile_2.md)


- **Post Vulnerabilities**
  - Vulnerability 6: User controlled parameter allows to read other user's posts [(link)](pocs/edit_post_1.md)
  - Vulnerability 7: SQL injection in URL parameter [(link)](pocs/edit_post_2.md)
  - Vulnerability 8: SQL injection in update post form allows to change any comment [(link)](pocs/edit_post_3.md)
  - Vulnerability 9: SQL injection in update post form allows to edit other users' posts [(link)](pocs/edit_post_4.md)
  - Vulnerability 10: User controlled parameter allows to edit other users' posts [(link)](pocs/edit_post_5.md)
  - Vulnerability 11: SQL Injection allows to infer any database value[(link)](pocs/create_post_1.md)

- **Friends Vulnerabilities**
  - Vulnerability 12: SQL Injection in search friend form allows arbitrary read from database [(link)](pocs/search_friend_1.md)

- **Other Vulnerabilities**
  - Vulnerability 13: Usage of insecure Protocol [(link)](pocs/http_1.md)
  - Vulnerability 14: Cookie contains user information [(link)](pocs/cookie_1.md)
  - Vulnerability 15: SQL equality funcion is case insensitive [(link)](pocs/comparator_1.md)
  - Vulnerability 16: Stored XSS [(link)](pocs/stored_xss.md)
  - Vulnerability 17: Reflected XSS [(link)](pocs/reflected_xss.md)
  - Vulnerability 18: Unrestricted File Upload [(link)](bad_input.md)
  - Vulnerability 19: Error Handling of the input fields allows to get information from the system [(link)](pocs/error_1.md)
  
