def json2md(i, f):
    for j in i:
        f.write('##' + j + '\n')
        f.write(str(i[j]) + "\n")


def md2json(f):
    poem = dict()
    now = ''
    now_key = ''
    for i in f:
        if i.startswith('##'):
            if now_key:
                poem[now_key] = now.strip()
            now_key = i.strip('#').strip()
            if now_key in poem:
                print(now_key, '重复', 'md2json')
                exit(-1)
            now = ''
        else:
            now += i
    poem[now_key] = now.strip()
    return poem
