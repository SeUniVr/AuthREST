import json

# Authentication details
auth_details = {
    "name": "X-API-Key",
    "value": "0A25B9DDABAB458EB1555AE340CE0695",
    "in": "header",
    "duration": 6000
}

# Print the JSON string
print(json.dumps(auth_details))
