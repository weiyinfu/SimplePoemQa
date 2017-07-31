import requests
from pyquery import PyQuery as jq
import re
import json

ar = []
for i in range(1, 774):
    href = "http://shige.laiyo.com/leixing_%dA%d.aspx"
    url = href % (i, 1)
    print("requesting", url)
    resp = requests.get(url)
    print("response over", resp.status_code)
    resp.encoding = "utf8"
    html = jq(resp.text)
    category = html('.left .title h1 span').text()
    poems = []
    for j in range(1, 0xffffff):
        url = href % (i, j)
        print("requesting", url)
        resp = requests.get(url)
        print("response over", resp.status_code)
        shige = re.findall("#shige\d+", resp.text)
        if not shige:
            break
        for k in shige:
            poems.append(int(re.search("\d+", k).group()))
    d = {"name": category, "poems": poems}
    ar.append(d)
json.dump(ar, open("type.json", "w", encoding="utf8"), indent=1, ensure_ascii=0)
