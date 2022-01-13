# Vulnerability 6: User controlled parameter allows to read other user's posts

- Vulnerability: URL Parameter Tampering
- Where: `id` parameter in request URL
- Impact: Allows user to read other user's public and private posts

## Steps to reproduce

1. Get to '<url>/edit\_post?id=<x>', where x is the id of the post you want to view/edit

## Limitations
The post id must exist in order to the exploit to work

[(POC)](edit_post_1.py)
