from HTMLParser import HTMLParser
import os
 
class MyHTMLParser(HTMLParser):
    def __init__(self):
        HTMLParser.__init__(self)
        self.type = []
        self.result = {}
        self.list = ['title','h1','h2','h3','h4','h5','h6']
        for item in self.list:
            self.result[item] = ''
        
    def handle_starttag(self, tag, attrs):
        self.type.append(tag.lower())
            
    def handle_endtag(self, tag):
        size = len(self.type)
        self.type = self.type[0:size-1]
            
    def handle_data(self, data):
        for item in self.list:
            if item in self.type:
                self.result[item] = self.result[item] + data
            
    def clear(self):
        self.result = []
        
    def link(self):
        res = ''
        for item in self.list:
            res = res + item + '="' + self.result[item] +'" '
        return res
        
        
def transform(input):
    file = open(input,'r')
    html_code = file.read()
    hp = MyHTMLParser()
    hp.feed(html_code)
    hp.close()
    return hp.link()
    
def do_transform(input, id):
    str = transform(input)
    res = '<pic id="' + str(id) + '" ' + str + 'locate="' + input + '" />\n'
    return res

if __name__ == "__main__":
    #transform('20110225231207437837642_.html','test_out')
    res = ""
    id = 0
    for tri in os.walk('news.tsinghua.edu.cn'):
        for file in tri[2]:
            if ".html" in file or ".jsp" in file:
                str = tri[0] + '\\' + file
                try:
                    res = res + do_transform(str,id)
                    id = id + 1
                except:
                    pass
    res = '<?xml version="1.0" encoding="utf-8"?>\n<pics>\n<category name="sogou">\n' + res + '</category>\n</pics>'
    file = open('out.xml','w')
    file.write(res)
                    