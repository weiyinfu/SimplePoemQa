import json
from  pprint import pprint
import random
import re

data = json.load(open("poem.json", encoding='utf8'))
authors = json.load(open("author.json", encoding='utf8'))


def format_content(s):
    s = re.sub('\n', '', s)
    ans = ''
    sep = '。！？，'
    cnt = 0
    for i in s:
        ans += i
        if i in sep:
            cnt += 1
            if cnt % 2 == 0:
                ans += '\n'
    return ans.strip()


def lv_shi():
    for i in data:
        temp = i['content']
        s = i['content'].strip()
        s = re.sub('\n|(\(.*?\))', '', s)
        s = re.split('[^\u4e00-\u9fa5]', s)[:-1]
        same_len = all([len(j) == len(s[0]) for j in s])
        sentence_cnt = len(s)
        if (sentence_cnt == 4 or sentence_cnt == 8) and same_len:
            i['content'] = format_content(i['content'])
    save_all()


def save_all():
    json.dump(data, open("poem.json", 'w', encoding='utf8'), ensure_ascii=0)


def search_cipai():
    for i in data:
        temp = i['content']
        s = i['content'].strip()
        s = re.sub('\n|(\(.*?\))', '', s)
        s = re.split('[^\u4e00-\u9fa5]', s)[:-1]
        same_len = all([len(j) == len(s[0]) for j in s])
        sentence_cnt = len(s)
        if (sentence_cnt == 4 or sentence_cnt == 8) and same_len:
            i['content'] = format_content(i['content'])


def view():
    while 1:
        i = data[random.randint(0, len(data) - 1)]
        pprint(i)
        input()


def tongji():
    cnt = dict()
    for i in authors:
        if i['name'] in cnt:
            raise Exception(json.dumps(i, ensure_ascii=0, indent=2))
        else:
            cnt[i['name']] = 0
    not_have=set()
    for i in data:
        if i['author'] not in cnt:
            not_have.add(i['author'])
            # print(i['author'],i['id'])
    pprint(not_have)
for i in data:
    if i['title']=='梦游天姥吟留别':
        print(i)
    if 'dynasty' not in i:
        print("==")

