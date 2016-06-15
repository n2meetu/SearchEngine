from bs4 import BeautifulSoup

with open('imagesearch.jsp') as file:
    text = file.read()
soup = BeautifulSoup(text,"lxml")
text = soup.prettify()
print(text)