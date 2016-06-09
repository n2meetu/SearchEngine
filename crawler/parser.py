from HTMLParser import HTMLParser
import os
 
class MyHTMLParser(HTMLParser):
    def __init__(self):
        HTMLParser.__init__(self)
        self.type = []
        self.result = []
        
    def handle_starttag(self, tag, attrs):
        self.type.append(tag.lower())
            
    def handle_endtag(self, tag):
        size = len(self.type)
        self.type = self.type[0:size-1]
            
    def handle_data(self, data):
        if "title" in self.type:
            self.result.append(data)
            return
        if len(self.result) > 3:
            return
            
        if "h1" in self.type:
            self.result.append(data)
            return
        if "h2" in self.type:
            self.result.append(data)
            return
        if "h3" in self.type:
            self.result.append(data)
            return
        if "h4" in self.type:
            self.result.append(data)
            return
        if "h5" in self.type:
            self.result.append(data)
            return
        if "h6" in self.type:
            self.result.append(data)
            return
            
    def clear(self):
        self.result = []
        
        
def transform(input, output):
    file = open(input,'r')
    html_code = file.read()
    hp = MyHTMLParser()
    hp.feed(html_code)
    hp.close()
    file = open(output,'w')
    for item in hp.result:
        file.write(item)
        file.write('\n')
            

if __name__ == "__main__":
    #transform('20110225231207437837642_.html','test_out')
    for tri in os.walk('news.tsinghua.edu.cn'):
        for file in tri[2]:
            if ".html" in file:
                str = tri[0] + '\\' + file
                outdir = 'out' + '\\' + tri[0]
                outstr = outdir + '\\' + file
                if  not os.path.exists(outdir):
                    os.makedirs(outdir)
                try:
                    transform(str, outstr)
                except:
                    pass
                    