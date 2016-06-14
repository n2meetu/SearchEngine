import java.io.File;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.opengis.metadata.maintenance.MaintenanceInformation;
import org.xml.sax.SAXException;

import jj2000.j2k.util.StringFormatException;

public class FileTest {
 
 public static void main(String[] args) throws Exception {
    
    String filePath = "D:\\learn4\\searchEngine\\hw\\final\\news.tsinghua.edu.cn";
    getFiles(filePath);
 } 
 /*
  * 通过递归得到某一路径下所有的目录及其文件
  */
 static void getFiles(String filePath)throws TikaException,IOException{
	 File root = new File(filePath);
	 File[] files = root.listFiles();
	 for(File file:files){     
		 if(file.isDirectory()){
			 getFiles(file.getAbsolutePath());
		 }
		 else{
			 //System.out.println("显示"+filePath+"下所有子目录"+file.getAbsolutePath());
			 String filename = file.getAbsolutePath();
			 String info;
			 if(filename.contains(".pdf"))
			 {
				 System.out.println(filename);
				 info = getMainInfo(filename);
			 }
			 else if(filename.contains(".doc"))
			 {
				 System.out.println(filename);
				 info = getMainInfo(filename);
			 }
			 else if(filename.contains(".xls"))
			 {
				 System.out.println(filename);
				 info = getMainInfo(filename);
			 }
			 String title;
			 String h1,h2,h3,h4,h5,h6;
			 String item = "<pic id=\"0\" title=\""+title+"\" h1=\""+h1+"\" h2=\""+h2+"\" h3=\""+h3+"\" h4=\""+h4+"\" h5=\""+h5+"\" h6=\""+h6+"\" keyword=\""+""+"\" locate=\""+filename+"\" />";
		 }     
	 }
 }
 
 static String getMainInfo(String filePath)throws TikaException,IOException {
	 File file = new File(filePath);
	 Tika tika = new Tika();
	 String filecontent = tika.parseToString(file);
	 return filecontent;
 }
}

