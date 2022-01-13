#!/usr/bin/env python3
import requests, sys, re
from interface import *
from bs4 import BeautifulSoup

if not (2 == len(sys.argv)):
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]
username = 'asdf'
password = 'asdf'

session = requests.Session()
reset_app(session, url)

r = register(session, url, username=username, password=password)
logout(session, url)

PASSWORD=password.upper()
r = login(session, url, username=username, password=PASSWORD)
soup = BeautifulSoup(r.text, 'html.parser')
title = soup.find_all("title")[0].string

assert title != "LoginFaceFive" # Assert valid login

r = get_profile(session, url)
soup = BeautifulSoup(r.text, 'html.parser')
inp = soup.find(id="usernameInput") # usernameInput field will have username placeholder

assert soup.find(id="usernameInput")['placeholder'] == username

print(f"[+] Successfully logged in as {username} with password {PASSWORD}!")
