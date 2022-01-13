import requests

def log(s):
    print(s)

def reset_app(s, url, logging=True):
    if logging:
        log("[+] Resetting app")
    init_url = f"{url}/init"
    r = s.get(init_url)
    return r


def register(s, url, username="", password="", logging=True):
    if logging:
        log(f'[+] Registering user "{username}" with password "{password}"')
    register_url = f"{url}/register"
    data = {
        'username': username,
        'password': password
    }
    r = s.post(register_url, data=data)
    return r

def login(s, url, username="", password="", logging=True):
    if logging:
        log(f'[+] Logging in as "{username}" with password "{password}"')
    login_url = f"{url}/login"
    data = {
        "username": username,
        "password": password
    }

    r = s.post(login_url, data=data)
    return r

def logout(s, url, logging=True):
    if logging:
        log(f'[+] Logging out')
    logout_url = f"{url}/logout"
    r = s.get(logout_url)
    return r

def get_home(s, url, logging=True):
    if logging:
        log(f'[+] Getting home page')
    r = s.get(url)
    return r

def get_profile(s, url, logging=True):
    if logging:
        log(f'[+] Getting profile page')
    profile_url = f"{url}/profile"
    r = s.get(profile_url)
    return r

def update_profile(s, url, name="", password="", new_password="", about="", photo=False, logging=True):
    if logging:
        log(f'''[+] Updating profile
    name="{name}"
    password="{password}"
    new_password="{new_password}"
    about="{about}"
    photo="{photo}"
    ''')
    update_profile_url = f"{url}/update_profile"
    data = {
        'name': name,
        'about': about,
        'newpassword': new_password,
        'currentpassword': password
    }

    files = {
        'photo': open(photo, 'rb') if photo else ""
    }

    r = s.post(update_profile_url, data=data, files=files)
    return r

def add_friend(s, url, username="", logging=True):
    if logging:
        log(f'[+] Sending friend request to "{username}"')
    add_friend_url = f"{url}/request_friend"
    data = {
        'username': username
    }
    r = s.post(add_friend_url, data=data)
    return r

def accept_friend(s, url, username="", logging=True):
    if logging:
        log(f'[+] Accepting friend request from "{username}"')
    pending_requests_url = f"{url}/pending_requests"
    data = {
        'username': username
    }
    r = s.post(pending_requests_url, data=data)
    return r

def search_friend(s, url, friend_username="", logging=True):
    if logging:
        log(f'[+] Searching for friend with username "{friend_username}"')
    search_friend_url = f"{url}/friends" 
    params = {
        'search': friend_username
    }
    r = s.get(search_friend_url,  params=params)
    return r

def create_post(s, url, content="", privacy="", logging=True):
    if logging:
        log(f'[+] Creating {privacy} post with content "{content}"')
    create_post_url = f"{url}/create_post"
    data = {
        'content': content,
        'type': privacy
    }
    r = s.post(create_post_url, data=data)
    return r

def get_edit_post(s, url, post_id="", logging=True):
    if logging:
        log(f'[+] Getting edit post #{post_id}')
    edit_post_url = f"{url}/edit_post"
    params = {
        'id': post_id
    }
    r = s.get(edit_post_url, params=params)
    return r

def send_edit_post(s, url, post_id="", content="", privacy="", logging=True):
    if logging:
        log(f'[+] Editing post #{post_id} with content "{content}" ({privacy})')
    edit_post_url = f"{url}/edit_post"
    data = {
        'id': post_id,
        'content': content,
        'type': privacy
    }
    r = s.post(edit_post_url, data=data)
    return r

