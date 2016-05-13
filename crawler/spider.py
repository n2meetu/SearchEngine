import re
import urllib2
import time
import os 

class Spider:
	# current url
	current = ""
	stack = []
	failedList = []
	hashMap = {}

	downloadPath = './mirrors/'

	banned_files = ['css','jsp','mp4','xls']

	pHref = re.compile(r'href="(?P<href>[^"]*)"');
	headers = {"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/600.5.17 (KHTML, like Gecko) Version/8.0.5 Safari/600.5.17"}

	def __init__(self, seed):
		self.current = seed;
		self.stack.append(seed)
		self.hashMap[seed]=True
		pass

	def filter(self,link):
		if(link[0] != 'h' or link.split('.')[-1] in self.banned_files or 'lib.tsinghua' in link or '166.111.120.' in link):
			return False
		return True

	def isRelativePath(self,link):
                if "cic.tsinghua." in link:
                        return True
                elif "info.tsinghua." in link:
                        return True
                else:
                        return False

	def extractLinks(self, text):
		# print "GetLink"
		# p = self.pHref;
		p = re.compile(r'href=\"(?P<href>[^\"]+)\"');
		# print p.findall(text)
		# return p.match(text).group('href')
		#print p.findall(text)
		return p.findall(text)


	def decompose(self,url):
		print url
		request = urllib2.Request(url,headers=self.headers);
		try:
			text = urllib2.urlopen(request,timeout = 3).read();

		except Exception,e:
			self.failedList.append(url)
			return
		# text = urllib2.urlopen(url).read()
		# print text
		time.sleep(0.2)

		# print text
		# text = 'window.location.href="/publish/thunews/index.html",href="http://www.baidu.com"'
		blocks = url[7:].split('/')
		base = 'http://' + blocks[0]

		# create dirs & write the file
		# case a good file
		# find its type first
		filename = blocks[-1]
		ext = filename.split('.')[-1]
		path = ''
		if (len(filename)==0 or 'com' in ext or 'cn' in ext):
			filename = 'index.html'
			if(url[-1]=='/'):
				path = url[7:-1]
			else:
				path = url[7:]
		else:
			# print "rfind:"+url[7:]
			pos = url.rfind('/')
			# print pos
			if(pos == -1):
				print 'pos=-1:' + url
			else:
				path = url[7:pos]
		links = self.extractLinks(text)
		print "++++Base:"+base
		for link in links:
			if(link[0]=='/'):
				print link
				link = base + link
			link = link.split(';')[0]
            
			if(self.filter(link) == True and self.isRelativePath(link) == True and 'tsinghua' in link and self.hashMap.get(link,False) == False):
				self.hashMap[link] = True
				self.stack.append(link)
		try:
			if(os.path.exists(self.downloadPath + path)):
				pass
			else:
				os.makedirs(self.downloadPath + path)

			# print "Path:"+ path + '/'
			# print "File:"+ filename
			with open(self.downloadPath + path + '/' + filename,'w') as file:
				# for line in text:
				file.writelines(text);
		except Exception,e:
			self.failedList.append(url)
			return
		

	def save(self):
		with open('stack.list','w') as file:
			for line in self.stack:
				file.write(line + '\n')
		with open('fault.list','w') as file:
			for line in self.failedList:
				file.write(line + '\n')
		pass

	def run(self):
		cnt = 0;
		url = ""
		while(len(self.stack)>0):
			print cnt
			url = self.stack.pop()
			# url = self.stack[0]
			# del self.stack[0]
			self.decompose(url)
			cnt += 1;
			if(cnt % 5000 == 0):
				self.save()
		self.save()



if __name__ == "__main__":
	seed = "http://postinfo.tsinghua.edu.cn/f/bangongtongzhi/more?field_bgtz_fl_tid=All"

	spider = Spider(seed);

	spider.run()
