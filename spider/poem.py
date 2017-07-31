from pprint import pprint

import requests
from pyquery import PyQuery as jq
import re
import json


def filt(s):
    if not s: return None
    s = re.sub("<br.*?>", "\n", s)
    s = re.sub("&.*?;", "", s)
    s = re.sub("<.*?>", "", s)
    s = s.strip()
    return s


def part(son):
    partName = son.find(".cont p").eq(0).text()
    return {partName: filt(jq(son).find(".cont").html())}


def mainPart(son):
    cont = son(".cont")
    source = son(".source")
    title = cont("p").eq(0).text().strip()
    preface = None
    dynasty = source("a").eq(1).text()
    author = source('a').eq(0).text()
    poemContent = ""
    for i in cont("#contson p"):
        span = jq(i).find('span')
        if span:
            preface = filt(span.html())
        else:
            poemContent += filt(jq(i).html()) + "\n"
    poemContent = poemContent.strip()
    if not poemContent:
        poemContent = filt(son("#contson").html())
    return {'title': title,
            'content': poemContent,
            'preface': preface,
            'dynasty': dynasty,
            'author': author
            }


def parseHtml(html):
    sons = html(".sons")
    d = mainPart(sons.eq(0))
    related = []
    for i in range(1, sons.size()):
        if sons.eq(i).attr('id'):
            related.append(int(sons.eq(i).attr('id')[4:]))
            print(related[-1], '=====')
            print(sons.eq(i))
            input()
        else:
            d = {**d, **part(sons.eq(i))}
    d = {**d, "related": related}
    return d


ar = []
for i in range(1, 73225):
    url = "http://shige.laiyo.com/view_%s.aspx" % i
    print("requesting", url)
    resp = requests.get(url)
    print(resp.status_code, "response")
    resp.encoding = "utf8"
    if "网页发生错误" in resp.text:
        print("没有", i, "这首诗")
        continue
    open("haha.html", 'w',encoding='utf8').write(resp.text)
    html = jq(resp.text)
    poem = {'id': i, **parseHtml(html)}
    ar.append(poem)
    pprint(poem)
    input()
json.dump(ar, open("poem.json", "w", encoding='utf8'), ensure_ascii=0, indent=1)
