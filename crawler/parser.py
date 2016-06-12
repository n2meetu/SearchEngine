#coding=utf-8

from HTMLParser import HTMLParser
import re
import chardet
import os
 
class MyHTMLParser(HTMLParser):
    def __init__(self):
        HTMLParser.__init__(self)
        self.type = []
        self.result = {}
        self.list = ['title','h1','h2','h3','h4','h5','h6']
        for item in self.list:
            self.result[item] = ''
        self.result['keyword'] = ''
        self.result['content'] = ''
        self.code = 'utf-8'
        self.code_change = 0
        
    def handle_starttag(self, tag, attrs):
        self.type.append(tag.lower())
        if tag == 'meta':
            dic = dict(attrs)
            if 'name' in dic:
                if dic['name'] == 'keyword' or dic['name'] == 'keywords':
                    self.result['keyword'] = dic['content']
            if 'http-equiv' in dic:
                if dic['http-equiv'] == "Content-Type":
                    content = dic['content']
                    self.code = content.split('charset=')[1]
                    self.code_change = 1
            
    def handle_endtag(self, tag):
        size = len(self.type)
        self.type = self.type[0:size-1]
            
    def handle_data(self, data):
        if 'script' in self.type:
            return
        if '"' in data:
            return
        for item in self.list:
            if item in self.type:
                self.result[item] = self.result[item] + data
        if len(self.result['content']) < 1000:
            data_strip = data.strip()
            if len(data_strip)>1:
                self.result['content'] = self.result['content'] + " " + data_strip
            
    def clear(self):
        self.result = []
        
    def link(self):
        res = ''
        for item in self.list:
            res = res + item + '="' + self.result[item] +'" '
        res = res + 'keyword="' + self.result['keyword'] +'" '
        res = res + 'content="' + self.result['content'] +'" '
        if self.code_change == 0:
            self.code = chardet.detect(res)['encoding']
        if self.code != 'utf-8':
            res = res.decode(self.code)
        return res
        
        
def transform(input):
    file = open(input,'r')
    html_code = file.read()
    #code = chardet.detect(html_code)['encoding']
    #html_code = html_code.decode('utf-8').encode(code)
    hp = MyHTMLParser()
    hp.feed(html_code)
    hp.close()
    return hp.link()
    
def do_transform(input, id):
    res = transform(input)
    res = '<pic id="' + str(id) + '" ' + res + 'locate="' + input + '" />\n'
    return res

def do_work():
    write_file = open('D:\\learn4\\searchEngine\\hw\\final\\all_xml4.xml','w')
    res = '<?xml version="1.0" encoding="utf-8"?>\n<pics>\n<category name="sogou">\n'
    write_file.write(res)
    id = 0
    for tri in os.walk('mirror'):
        for file in tri[2]:
            if ".html" in file or ".jsp" in file or ".php" in file:
                path = tri[0] + '\\' + file
                try:
                    res = do_transform(path,id)
                    write_file.write(res)
                    id = id + 1
                except:
                    pass
    res = '</category>\n</pics>'
    write_file.write(res)
    
    
def do_work_pdf():
    res = ""
    id = 0
    for tri in os.walk('news.tsinghua.edu.cn'):
        for file in tri[2]:
            if ".pdf" in file:
                path = tri[0] + '\\' + file
                try:
                    res = res + do_transform(path,id)
                    id = id + 1
                except:
                    pass
    res = '<?xml version="1.0" encoding="utf-8"?>\n<pics>\n<category name="sogou">\n' + res + '</category>\n</pics>'
    file = open('out_pdf.xml','w')
    file.write(res)
    
def do_work_doc():
    res = ""
    id = 0
    for tri in os.walk(''):
        for file in tri[2]:
            if ".doc" in file:
                path = tri[0] + '\\' + file
                try:
                    res = res + do_transform(path,id)
                    id = id + 1
                except:
                    pass
    res = '<?xml version="1.0" encoding="utf-8"?>\n<pics>\n<category name="sogou">\n' + res + '</category>\n</pics>'
    file = open('out_doc.xml','w')
    file.write(res)
    
if __name__ == "__main__":
    do_work()
                    