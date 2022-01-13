#!/usr/bin/env python3
import requests, sys, re, base64, json
from interface import *
from bs4 import BeautifulSoup

if not (2 == len(sys.argv)):
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

session = requests.Session()
reset_app(session, url)

username = 'asdf'
password = 'asdf'
r = register(session, url, username=username, password=password)

cookie = session.cookies['session']

b64data = cookie.split('.')[0]
b64data += "=" * (len(b64data)%4) # adjust b64 padding
b64bytes = b64data.encode('utf-8')
decoded_bytes = base64.b64decode(b64bytes)

message = decoded_bytes.decode('ascii')
data = json.loads(message)
assert data["username"] == username

print(f'[+] Successfully read cookie username: {data["username"]}')
