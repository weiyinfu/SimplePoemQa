import requests
import re
from  pyquery import PyQuery as pq
import json

url = "http://www.meili999.com/cipai/index.html"
url2 = "http://www.meili999.com/cipai/index_2.html"
urls = []
for url in (url, url2):
    resp = requests.get(url)
    resp.encoding = 'utf8'
    html = pq(resp.text).find("#content .list")
    resp = re.findall("http://www.meili999.com/cipai/\d+/", html.html())
    urls += resp
data = []
for url in urls:
    resp = requests.get(url)
    resp.encoding = 'utf8'
    html = pq(resp.text)
    ci = dict()
    ci['title'] = html(".pageTitle").text()
    desc = html(".poem_comm p")
    txt = ""
    for p in desc:
        txt += pq(p).text().strip() + "\n"
    ci['description'] = txt
    print(ci['title'])
    data.append(ci)
json.dump(data, open("cipai.json", "w", encoding="utf8"), ensure_ascii=0, indent=1)
