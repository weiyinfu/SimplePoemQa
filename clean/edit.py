import json
from pprint import pprint
from md_json import md2json, json2md
import os

data = json.load(open('poem.json', encoding='utf8'))
ma = dict()
for i in range(len(data)):
    ma[int(data[i]['id'])] = i


def tofiles():
    cnt = 0
    for i in data:
        if i['title'] == '':
            json2md(i,
                    open('c:/users/weidiao/desktop/uni/' + i['title'].replace('/', '').replace('\n', '') + str(
                        cnt) + '.md',
                         'w',
                         encoding='utf8'))
            cnt += 1
    print(cnt)


def fromfile():
    d = 'c:/users/weidiao/desktop/uni/'
    for i in os.listdir(d):
        p = md2json(open(d + i, encoding='utf8'))
        data[ma[int(p['id'])]] = p


def remove():
    global data
    ans = []
    for i in data:
        if 'delete' not in i:
            ans.append(i)
    data = ans


def save():
    json.dump(data, open("poem.json", 'w', encoding='utf8'))


fromfile()
save()
