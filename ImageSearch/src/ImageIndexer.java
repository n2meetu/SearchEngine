import java.io.*;
import java.util.*;


import org.w3c.dom.*;   
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import net.paoding.analysis.analyzer.PaodingAnalyzer;

import javax.xml.parsers.*; 

public class ImageIndexer {
	private Analyzer analyzer; 
    private IndexWriter indexWriter;
    private float titleLength=1.0f;
    private float h1Length=1.0f;
    private float h2Length=1.0f;
    private float h3Length=1.0f;
    private float h4Length=1.0f;
    private float h5Length=1.0f;
    private float h6Length=1.0f;
    private float keywordLength=1.0f;
    private float contentLength=1.0f;
    
    public ImageIndexer(String indexDir){
    	//analyzer = new IKAnalyzer();
    	analyzer = new PaodingAnalyzer();
    	try{
    		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
    		Directory dir = FSDirectory.open(new File(indexDir));
    		indexWriter = new IndexWriter(dir,iwc);
    		indexWriter.setSimilarity(new SimpleSimilarity());
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
    
    // Output the average length
    public void saveGlobals(String filename){
    	try{
    		PrintWriter pw=new PrintWriter(new File(filename));
    		//pw.println(averageLength);
    		pw.println(titleLength);
    		pw.println(h1Length);
    		pw.println(h2Length);
    		pw.println(h3Length);
    		pw.println(h4Length);
    		pw.println(h5Length);
    		pw.println(h6Length);
    		pw.println(keywordLength);
    		pw.println(contentLength);
    		pw.close();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
	
	/** 
	 * <p>
	 * index sogou.xml 
	 * 
	 */
	public void indexSpecialFile(String filename){
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();   
			DocumentBuilder db = dbf.newDocumentBuilder();    
			org.w3c.dom.Document doc = db.parse(new File(filename));
			NodeList nodeList = doc.getElementsByTagName("pic");
			for(int i=0;i<nodeList.getLength();i++){
				Node node=nodeList.item(i);
				NamedNodeMap map=node.getAttributes();
				Node locate=map.getNamedItem("locate");
				Node bigClass=map.getNamedItem("bigClass");
				Node smallClass=map.getNamedItem("smallClass");
				Node query=map.getNamedItem("query");
				String absString=bigClass.getNodeValue()+" "+smallClass.getNodeValue()+" "+query.getNodeValue();
				Document document  =   new  Document();
				Field PicPathField  =   new  Field( "picPath" ,locate.getNodeValue(),Field.Store.YES, Field.Index.NO);
				Field abstractField  =   new  Field( "abstract" ,absString,Field.Store.YES, Field.Index.ANALYZED);
				//averageLength += absString.length();
				titleLength += absString.length();
			    h1Length += absString.length();
			    h2Length += absString.length();
			    h3Length += absString.length();
			    h4Length += absString.length();
			    h5Length += absString.length();
			    h6Length += absString.length();
			    keywordLength += absString.length();
			    contentLength += absString.length();
				document.add(PicPathField);
				document.add(abstractField);
				indexWriter.addDocument(document);
				if(i%10000==0){
					System.out.println("process "+i);
				}
				//TODO: add other fields such as html title or html content 
				
			}
			//averageLength /= indexWriter.numDocs();
			titleLength /= indexWriter.numDocs();
		    h1Length /= indexWriter.numDocs();
		    h2Length /= indexWriter.numDocs();
		    h3Length /= indexWriter.numDocs();
		    h4Length /= indexWriter.numDocs();
		    h5Length /= indexWriter.numDocs();
		    h6Length /= indexWriter.numDocs();
		    keywordLength /= indexWriter.numDocs();
		    contentLength /= indexWriter.numDocs();
			//System.out.println("average length = "+titleLength);
		    System.out.println("average length = "+titleLength);
		    System.out.println("average length = "+h1Length);
		    System.out.println("average length = "+h2Length);
		    System.out.println("average length = "+h3Length);
		    System.out.println("average length = "+h4Length);
		    System.out.println("average length = "+h5Length);
		    System.out.println("average length = "+h6Length);
		    System.out.println("average length = "+keywordLength);
		    System.out.println("average length = "+contentLength);
			System.out.println("total "+indexWriter.numDocs()+" documents");
			indexWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		System.out.println("ImageIndexer.java::main()");
		ImageIndexer indexer=new ImageIndexer("forIndex/index");
		indexer.indexSpecialFile("input/sogou-utf8.xml");
		indexer.saveGlobals("forIndex/global.txt");
	}
}
