# Vulnerability 9: SQL injection in update post form allows to edit other users' posts

- Vulnerability: SQL injection
- Where: `content` field
- Impact: Allows user to change other users' posts

## Steps to reproduce

1. Change post content to `EXPOSED', type='Public', author='ssofadmin'#`
2. Every post will have its content, author and type changed


## Notes

This vulnerability also allows the attacker to insert a `js` script in other users' posts, making it possible to execute arbitrary code in the victim's browser (as described [here](stored_xss.md)).


[(POC)](edit_post_4.py)
