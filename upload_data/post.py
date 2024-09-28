import pickle
import json
import asyncio
from aiohttp import ClientSession
from tqdm import tqdm

with open('result.pkl', 'rb') as file:
    result = pickle.load(file)

url = 'http://localhost:8080/game-shop/add-game/'

headers = {'Content-Type': 'application/json', 'accept': '*/*'}

async def send_request(session, record, semaphore):
    async with semaphore:
        async with session.post(url, json=record, headers=headers, timeout=30) as response:
            return await response.text()

async def main():
    async with ClientSession() as session:
        semaphore = asyncio.Semaphore(45) #change count thread
        tasks = []
        for record in tqdm(result):
            task = asyncio.ensure_future(send_request(session, record, semaphore))
            tasks.append(task)
        responses = await asyncio.gather(*tasks)
        print(responses)  
        for response in tqdm(responses):
            try:
                print(response)
            except Exception as e:
                print(e)

await main()