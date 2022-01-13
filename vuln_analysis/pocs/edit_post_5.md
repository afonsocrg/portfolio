# Vulnerability 10: User controlled parameter allows to edit other user's posts

- Vulnerability: Non validation of user input
- Where: `id` parameter in edit post form
- Impact: Allows user to edit other user's posts

## Steps to reproduce

1. Get to '<url>/edit\_post?id=<x>', where x is the id of the post you want to view/edit.
2. Change the post's contents (make it available to everyone)
3. Save the changes

[(POC)](edit_post_5.py)
