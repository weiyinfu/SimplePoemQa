mongo -uwyf -pxxxx weiyinfu.cn/poem haha.js
mongoimport --host weiyinfu.cn -uwyf -pxxxx -dpoem -cpoem --file poem.json --jsonArray
mongoimport --host weiyinfu.cn -uwyf -pxxxx -dpoem -cauthor --file author.json --jsonArray
mongoimport --host weiyinfu.cn -uwyf -pxxxx -dpoem -cdynasty --file dynasty.json --jsonArray
mongoimport --host weiyinfu.cn -uwyf -pxxxx -dpoem -ccipai --file cipai.json --jsonArray