import requests
from pyquery import PyQuery as jq
import re
import json

ar = []
for i in range(1, 3132):
    url = "http://shige.laiyo.com/zuozhe_%dA1.aspx" % i
    print("requesting", url)
    resp = requests.get(url)
    print(resp.status_code, "response over")
    if "网页发生错误" in resp.text:
        print("没有", i, "这首诗")
        continue
    resp.encoding = "utf8"
    html = jq(resp.text)
    img = re.search("http://img.gushiwen.org/authorImg/.*?.jpg", resp.text)
    if img:
        img = img.group()
    name = html(".left .title h1 span").text().strip()
    name = name[:name.index("的")]
    desc = html(".left .sons").eq(0).find(".cont").text().strip()
    author = {"id": i, "img": img, "description": desc, 'name': name}
    ar.append(author)
    # print(json.dumps(author, indent=1, ensure_ascii=0))
    # input()
json.dump(ar, open("author2.json", "w", encoding="utf8"), ensure_ascii=0, indent=1)
