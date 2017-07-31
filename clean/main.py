import json
from pprint import pprint

data = json.load(open("poem.json", encoding='utf8'))


def get_json_members():
    x = set()
    for i in data:
        for k in i:
            x.add(k)
    print(len(x))
    pprint(x)


def save_all():
    json.dump(data, open("poem.json", 'w', encoding='utf8'), ensure_ascii=0)


def by_author():
    for i in data:
        if '乐府' in i['author']:
            i['src'] = i['author']
            i['author'] = '佚名'


def strange_titles():
    import re
    cnt = 0
    for i in data:
        res = re.search('[^\u4e00-\u9fff]', i['title'])
        if res:
            print(i['title'])
            cnt += 1
    print(cnt)


def dynasty():
    for i in data:
        if i['title']=='静夜思':
            print(i)
            break


dynasty()
