import java.io.*;
import java.util.*;


import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.w3c.dom.*;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRefHash.MaxBytesLengthExceededException;
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
		analyzer = new IKAnalyzer();
		//analyzer = new PaodingAnalyzer();
//    	try{
//    		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
//    		Directory dir = FSDirectory.open(new File(indexDir));
//    		indexWriter = new IndexWriter(dir,iwc);
//    		indexWriter.setSimilarity(new SimpleSimilarity());
//    	}catch(IOException e){
//    		e.printStackTrace();
//    	}
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

	public void generateDict(String filename)
	{
		try{
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = documentBuilderFactory.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(new File(filename));
			PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("token1.txt")),true);
			PrintWriter pw2 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("token2.txt")),true);
			NodeList nodeList = doc.getElementsByTagName("pic");
			for(int i=0;i<nodeList.getLength();i++){
				Node node=nodeList.item(i);
				NamedNodeMap map=node.getAttributes();
				Node locate=map.getNamedItem("locate");
				Node titleClass=map.getNamedItem("title");
				Node h1Class=map.getNamedItem("h1");
				Node h2Class=map.getNamedItem("h2");
				Node h3Class=map.getNamedItem("h3");
				Node h4Class=map.getNamedItem("h4");
				Node h5Class=map.getNamedItem("h5");
				Node h6Class=map.getNamedItem("h6");
				Node keywordClass=map.getNamedItem("keyword");
				Node contentClass=map.getNamedItem("content");
				String titleStr = titleClass.getNodeValue();
				String contentStr = contentClass.getNodeValue();

				TokenStream ts1 = analyzer.tokenStream("", new StringReader(titleStr));
				TokenStream ts2 = analyzer.tokenStream("", new StringReader(contentStr));
				CharTermAttribute cta1 = ts1.getAttribute(CharTermAttribute.class);
				CharTermAttribute cta2 = ts1.getAttribute(CharTermAttribute.class);
				while (ts1.incrementToken()) {
					pw1.println(cta1.toString());
				}
				while (ts2.incrementToken()) {
					pw2.println(cta2.toString());
				}
//				System.out.println(ts1.next());
				if(i%10000==0){
					System.out.println("process "+i);
				}

			}
			pw1.close();
			pw2.close();
		}
		catch(Exception e)
		{

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
				Node titleClass=map.getNamedItem("title");
				Node h1Class=map.getNamedItem("h1");
				Node h2Class=map.getNamedItem("h2");
				Node h3Class=map.getNamedItem("h3");
				Node h4Class=map.getNamedItem("h4");
				Node h5Class=map.getNamedItem("h5");
				Node h6Class=map.getNamedItem("h6");
				Node keywordClass=map.getNamedItem("keyword");
				Node contentClass=map.getNamedItem("content");
				String titleStr = titleClass.getNodeValue();
				String[] titleStrs;
				//if(titleStrs.length >0 && !locate.getNodeValue().contains(".pdf"))
				//	titleStr = titleStrs[0];
//				if(locate.getNodeValue().contains(".pdf"))
//				{
//					titleStrs = titleStr.split("\\.|,");
//					if(titleStrs.length >0)
//						titleStr = Max(titleStrs);
//				}
//				else
//				{
//					titleStrs = titleStr.split(" ");
//					if(titleStrs.length >0)
//						titleStr = titleStrs[0];
//				}
				String h1Str = h1Class.getNodeValue();
				String h2Str = h2Class.getNodeValue();
				String h3Str = h3Class.getNodeValue();
				String h4Str = h4Class.getNodeValue();
				String h5Str = h5Class.getNodeValue();
				String h6Str = h6Class.getNodeValue();
				String keywordStr = keywordClass.getNodeValue();
				String contentStr = contentClass.getNodeValue();
				contentStr.replaceAll("..", "");
				Document document  =   new  Document();
				Field PicPathField  =   new  Field( "picPath" ,locate.getNodeValue(),Field.Store.YES, Field.Index.ANALYZED);
				//Field abstractField  =   new  Field( "abstract" ,absString,Field.Store.YES, Field.Index.ANALYZED);
				Field titleField  =   new  Field( "title" ,titleStr,Field.Store.YES, Field.Index.ANALYZED);
				Field h1Field  =   new  Field( "h1" ,h1Str,Field.Store.YES, Field.Index.ANALYZED);
				Field h2Field  =   new  Field( "h2" ,h2Str,Field.Store.YES, Field.Index.ANALYZED);
				Field h3Field  =   new  Field( "h3" ,h3Str,Field.Store.YES, Field.Index.ANALYZED);
				Field h4Field  =   new  Field( "h4" ,h4Str,Field.Store.YES, Field.Index.ANALYZED);
				Field h5Field  =   new  Field( "h5" ,h5Str,Field.Store.YES, Field.Index.ANALYZED);
				Field h6Field  =   new  Field( "h6" ,h6Str,Field.Store.YES, Field.Index.ANALYZED);
				Field keywordField  =   new  Field( "keyword" ,keywordStr,Field.Store.YES, Field.Index.ANALYZED);
				Field contentField  =   new  Field( "content" ,contentStr,Field.Store.YES, Field.Index.ANALYZED);
				//averageLength += absString.length();
				titleLength += titleStr.length();
				h1Length += h1Str.length();
				h2Length += h2Str.length();
				h3Length += h3Str.length();
				h4Length += h4Str.length();
				h5Length += h5Str.length();
				h6Length += h6Str.length();
				keywordLength += keywordStr.length();
				contentLength += contentStr.length();
				document.add(PicPathField);
				document.add(titleField);
				document.add(h1Field);
				document.add(h2Field);
				document.add(h3Field);
				document.add(h4Field);
				document.add(h5Field);
				document.add(h6Field);
				document.add(keywordField);
				document.add(contentField);
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
		indexer.generateDict("D:\\learn4\\searchEngine\\hw\\final\\change.xml");
//		indexer.indexSpecialFile("D:\\learn4\\searchEngine\\hw\\final\\change.xml");
//		indexer.saveGlobals("forIndex/global.txt");

	}
}
