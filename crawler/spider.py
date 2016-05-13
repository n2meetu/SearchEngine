import re
import urllib2
import time
import os 

class Spider:
	# current url
	current = ""
	stack = []
	failedList = []
	linkTable = []
	id2Link = [""];
	hashMap = {}
	linkCnt = 1;

	downloadPath = './mirrors/'

	banned_files = ['css','jsp','mp4','xls','jpg','JPG','png','PNG','bmp','BMP']

	pHref = re.compile(r'href="(?P<href>[^"]*)"');
	headers = {"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/600.5.17 (KHTML, like Gecko) Version/8.0.5 Safari/600.5.17"}

	def __init__(self, seed):
		self.current = seed;
		self.stack.append(seed)
		self.hashMap[seed] = 1
		self.id2Link.append(seed)
		self.linkCnt+=1;
		pass

	def filter(self,link):
		if(link[0] != 'h' or link.split('.')[-1] in self.banned_files or 'lib.tsinghua' in link or '166.111.120.' in link):
			return False
		return True

	def isRelativePath(self,link):
		return True

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


		currentID = self.hashMap[url]
		pointTo = []

		# text = urllib2.urlopen(url).read()
		# print text
		time.sleep(0.1)

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
		# print "++++Base:"+base
		for link in links:
			if(link[0]=='/'):
				# print link
				link = base + link
			link = link.split(';')[0]
            
			if(self.filter(link) == True and self.isRelativePath(link) == True and 'tsinghua' in link and self.hashMap.get(link,0) == 0):
				self.hashMap[link] = self.linkCnt;
				self.stack.append(link);
				self.linkCnt+=1;
				self.id2Link.append(link)
			
			if(self.hashMap.get(link,0) != 0):
				pointTo.append(self.hashMap[link])


		self.linkTable.append(pointTo)

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
		with open('id2Url.txt','w') as file:
			for line in self.id2Link:
				file.write(line + '\n')
		with open('link_table.txt','w') as file:
			for line in self.linkTable:
				file.write(str(line)+'\n')
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
			if(cnt % 500 == 0):
				self.save()
		self.save()



if __name__ == "__main__":
	seed = "http://news.tsinghua.edu.cn"

	spider = Spider(seed);

	spider.run()
