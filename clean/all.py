import json
import re
from pprint import pprint

poem = json.load(open('poem.json', encoding='utf8'))
cipai = json.load(open('cipai.json', encoding='utf8'))
author = json.load(open('author.json', encoding='utf8'))
dynasty = json.load(open('dynasty.json', encoding='utf8'))


def handle_cipai():
    cnt = 0
    for i in cipai:
        for j in poem:
            if j['title'].startswith(i['name']):
                cnt += 1
                j['cipai'] = i['name']
    print(cnt)
    json.dump(poem, open("poem.json", "w", encoding='utf8'), ensure_ascii=0)


def handle_dynasty():
    for i in dynasty:
        s = i['description']
        a = s.split("\n")
        ss = ""
        for j in a:
            ss += j.strip() + "\n"
        print(ss)
        i['description'] = ss
    json.dump(dynasty, open("dynasty.json", 'w', encoding='utf8'), ensure_ascii=0, indent=2)


def first_sentence():
    cnt = 0
    for i in poem:
        first = re.search("[\u4e00-\u9ffff]+[^\u4e00-\u9fff]", i['content']).group()
        first = first[:-1]
        if first in i['title'] and ('_' not in i['title'] and '【' not in i['title']) and 'cipai' in i:
            print(i['cipai'], i['title'], '======', i['content'])
            cnt += 1
    print(cnt)

