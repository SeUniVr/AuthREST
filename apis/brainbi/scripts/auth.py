import json

# Get token from https://developers.google.com/oauthplayground/
# WARNING: DO NOT USE YOUR PERSONAL GOOGLE ACCOUNT: RESTTESTGEN WILL ERASE YOUR DATA IN GOOGLE DRIVE!!!

token = "eyJpdiI6InRhcWs5Yk9wZWRQc2hJY0U4d3UyK3c9PSIsInZhbHVlIjoic0VkRDJIcnlFakJyY1l4dWh1akNyTzA0TDYzT2FxU2dHN0FZbllKWFRMUDRaRVNSYmV1ajczbUI1dG9sYmVOcldZKythalBwZDZtYXlhT01WeXpwRXAycEE1dWZWMFZTNjFmSElKcjBSdTRxdkY3WFZyWGtOTnVkQ3Nyb2xEUVUiLCJtYWMiOiI3ZTlhMDQwOTRjOTJlZmMwNjNiMzk2MzMxMThiNDhmMzlhYzljY2Y3NGFjM2Q3Yjc5MmZhZDY0MTAxZjZkM2VlIiwidGFnIjoiIn0="

rtg_info = {
    "name": "Authorization",
    "value": "Bearer " + token,
    "in": "header",
    "duration": 600
}

print(json.dumps(rtg_info))