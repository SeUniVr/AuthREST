import json

# Get token from https://developers.google.com/oauthplayground/
# WARNING: DO NOT USE YOUR PERSONAL GOOGLE ACCOUNT: RESTTESTGEN WILL ERASE YOUR DATA IN GOOGLE DRIVE!!!

token = "eyJhbGciOiJIUzUxMiJ9.eyJsb2dpbl91c2VyX2tleSI6ImRhODM2YjlhLWZhMjUtNDg2Mi05OGFmLWQ5YzljMDQ4MDdkYyJ9.zESjhk6dLHQRnZ7pYcONdsI4BGH_QlhzP-P8adXdT7Yi0kpUA6WLflYlKn010H4dxi-vM1zN03U08Z7fXJ-Xug"

rtg_info = {
    "name": "Authorization",
    "value": "Bearer " + token,
    "in": "header",
    "duration": 600
}

print(json.dumps(rtg_info))