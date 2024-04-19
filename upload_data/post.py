import pickle
import json
import requests
from tqdm import tqdm
from concurrent.futures import ThreadPoolExecutor

with open('result.pkl', 'rb') as file:
    result = pickle.load(file)

url = 'http://localhost:8080/game-shop/add-game/'

headers = {'Content-Type': 'application/json', 'accept': '*/*'}

def send_request(record):
    json_data = json.dumps(record)
    response = requests.post(url, data=json_data, headers=headers)
    return response

pool = ThreadPoolExecutor(max_workers=5)

for page in tqdm(pool.map(send_request, result)):
    try:
        page
    except Exception as e:
        print(e)
