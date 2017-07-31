## 目录结构说明  
* controller：使用SpringBoot处理HTTP请求；使用HttpClient发送HTTP请求；实现微信接口层
* data.index：建立索引
* data.po：数据PO对象
* data.model：模型层，控制数据的读写
* data.mongo：读取mongo数据库
* haha：未归类
* query：查询处理包，这是最核心的包

## resources目录说明
* how.txt：用法，开场白
* myconfig.properties：自定义配置
* re.txt：正则表达式，没用上
* 细节.txt：同义词词典

## 设计说明
整个系统就相当于一个数据展示界面，不涉及对数据库的写操作  
任何系统，能够处理的事情在类别上是有限的、可枚举的。  
本系统将Query分为如下几类：
* AuthorQuery：查询作者
* CipaiQuery：查询词牌
* DynastyQuery：查询朝代
* HowQuery：查询用法
* PoemQuery：查询诗歌

每类Query都包含handle()函数用来处理请求，Query是一个接口，包含handle()函数。

因为PoemQuery的handle函数过于复杂，所以新建一个PoemQueryHandler类来处理  
PoemQuery类型的请求。

QueryHandler是沟通查询处理器和用户请求的桥梁，是query包唯一的向外的接口，此类  
负责调用query包中的各个模块。

当请求来临时，QueryHandler接受userid，query两个字符串类型的对象。  
然后QueryHandler调用QueryParser解析用户请求query，进行分词处理。  
分词结果用TermList表示，每个词是一个Term。  
对于分词结果，调用QueryClassifier对查询进行分类，得到PoemQuery或者  
AuthorQuery等Query类型的对象。QueryClassifier将Query类型的对象返回给  
QueryHandler，QueryHandler调用Query的handle()方法处理请求。

本系统比较复杂和核心的地方就是QueryParser（查询解析器），需要把用户的查询进行分词。  
比如用户说：“背背李白的静夜思”，解析成：<李白,诗人> <静夜思,题目>，忽略“背背”和“的”  
这样的字。