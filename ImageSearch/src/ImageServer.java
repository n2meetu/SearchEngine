import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.util.*;

import java.math.*;
import java.net.*;
import java.io.*;


public class ImageServer extends HttpServlet{
	public static final int PAGE_RESULT=10;
	public static final String indexDir="forIndex";
	public static final String picDir="";
	private ImageSearcher search=null;
	private SpellChecker spellChecker;
	private SimpleHTMLFormatter simpleHTMLFormatter;

	public ImageServer(){
		super();
		search=new ImageSearcher(new String(indexDir+"/index"));
		search.loadGlobals(new String(indexDir+"/global.txt"));


		simpleHTMLFormatter = new SimpleHTMLFormatter("<span style=\"color:red;\">","</span>");


//		highlighter = new Highlighter(simpleHTMLFormatter,)

		Directory spellCheckDir;
		IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35));
		try {
			spellCheckDir = FSDirectory.open(new File("./SpellChecker"));
			System.out.println(spellCheckDir.toString());
			spellChecker = new SpellChecker(spellCheckDir);
			PlainTextDictionary plainTextDictionary = new PlainTextDictionary(new File("./SpellChecker/spellchecker.txt"));
			spellChecker.indexDictionary(plainTextDictionary,config,false);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public ScoreDoc[] showList(ScoreDoc[] results,int page){
		if(results==null || results.length<(page-1)*PAGE_RESULT){
			return null;
		}
		int start=Math.max((page-1)*PAGE_RESULT, 0);
		int docnum=Math.min(results.length-start,PAGE_RESULT);
		ScoreDoc[] ret=new ScoreDoc[docnum];
		for(int i=0;i<docnum;i++){
			ret[i]=results[start+i];
		}
		return ret;
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		String queryString=request.getParameter("query");
		String pageString=request.getParameter("page");
		int page=1;
		if(pageString!=null){
			page=Integer.parseInt(pageString);
		}
		if(queryString==null){
			System.out.println("null query");
			//request.getRequestDispatcher("/Image.jsp").forward(request, response);
		}else{
			System.out.println(queryString);
			System.out.println(URLDecoder.decode(queryString,"utf-8"));
			System.out.println(URLDecoder.decode(queryString,"gb2312"));


			String[] tags=null;
			String[] paths=null;
			String[] highlightTags = null;

			String[] rs = null;
			if(!spellChecker.exist(queryString))
			{
				rs=spellChecker.suggestSimilar(queryString, 5);
				System.out.println("Suggestion:"+ Arrays.toString(rs));
			}

			TopDocs results=search.searchQuery(queryString, "abstract", 100);

			Analyzer analyzer = new PaodingAnalyzer();
			QueryParser queryParser = new QueryParser(Version.LUCENE_35,"abstract",analyzer);
			try {
				Query query = queryParser.parse(queryString);
				QueryScorer scorer = new QueryScorer(query);
				Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
				Highlighter highlighter = new Highlighter(simpleHTMLFormatter,scorer);
				highlighter.setTextFragmenter(fragmenter);

				if (results != null) {
					ScoreDoc[] hits = showList(results.scoreDocs, page);
					if (hits != null) {
						tags = new String[hits.length];
						paths = new String[hits.length];
						highlightTags = new String[hits.length];
						for (int i = 0; i < hits.length && i < PAGE_RESULT; i++) {
							Document doc = search.getDoc(hits[i].doc);
							System.out.println("doc=" + hits[i].doc + " score="
									+ hits[i].score + " picPath= "
									+ doc.get("picPath")+ " tag= "+doc.get("abstract"));
							tags[i] = doc.get("abstract");
							paths[i] = picDir + doc.get("picPath");

							String value =doc.get("abstract");
							if (value != null) {
								TokenStream tokenStream = analyzer.tokenStream("abstract", new StringReader(value));
								String highlightText = highlighter.getBestFragment(tokenStream, value);
//								str=str+str1;
								highlightTags[i] = highlightText;
								System.out.println("Highlight:"+highlightText);
							}
						}

					} else {
						System.out.println("page null");
					}
				}
				else
				{
					System.out.println("result null");
				}
				request.setAttribute("currentQuery",queryString);
				request.setAttribute("currentPage", page);
				request.setAttribute("imgTags", tags);
				request.setAttribute("imgPaths", paths);
				request.setAttribute("highlightTags",highlightTags);
//			request.setAttribute("suggestions",Arrays.toString(rs));
				// # SpellChecker!
				request.setAttribute("suggestions",rs);
				request.getRequestDispatcher("/imageshow.jsp").forward(request,
						response);

			}
			catch (Exception e)
			{

			}




		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}
}
