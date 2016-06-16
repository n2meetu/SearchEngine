import java.io.*;
import java.io.IOException;
import java.util.Arrays;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;

import java.util.Map;
import java.util.HashMap;


public class ImageSearcher {
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;

	private float avgLength=1.0f;
	
	public ImageSearcher(String indexdir){
		//analyzer = new IKAnalyzer();
		analyzer = new PaodingAnalyzer();
//		QueryParser queryParser = new QueryParser(version.,"abstract",analyzer);

		try{
			reader = IndexReader.open(FSDirectory.open(new File(indexdir)));
			searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new SimpleSimilarity());
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public TopDocs searchQuery(String queryString,String field,int maxnum){
		try {
			Term term=new Term(field,queryString);

			// http://blog.csdn.net/chenghui0317/article/details/10281311
//			BooleanQuery query = new BooleanQuery();
//			Analyzer analyzer = new IKAnalyzer(false);
//			TokenStream ts = analyzer.tokenStream("", new StringReader(queryString));
//			CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
//			while (ts.incrementToken()) {
//				query.add(new SimpleQuery(new Term(field, cta.toString()), avgLeng
// th), BooleanClause.Occur.SHOULD);
//			}
//			System.out.println("LLOLL");
			Query query=new SimpleQuery(term, avgLength);
//			Query query=new SimpleMultiTermQuery(term,field,avgLength);

			query.setBoost(1.0f);
			//Weight w=searcher.createNormalizedWeight(query);
			//System.out.println(w.getClass());
			TopDocs results = searcher.search(query, maxnum);
			System.out.println(results);
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public TopDocs searchQuery(String queryString,String field,TopDocs topdocs,int maxnum){
        try {
            Term term=new Term(field,queryString);
            Query query=new SimpleQuery(term, avgLength);
            query.setBoost(1.0f);
            TopDocs results = searcher.search(query, maxnum);
            System.out.println(results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public TopDocs searchQuery(String queryString,int maxnum){
        try {
            String[] fields = new String[9];
            fields[0] = "title";
            fields[1] = "h1";
            fields[2] = "h2";
            fields[3] = "h3";
            fields[4] = "h4";
            fields[5] = "h5";
            fields[6] = "h6";
            fields[7] = "content";
            fields[8] = "localkey";
            Map<String,Float> m = new HashMap<String,Float>();
            m.put("title", 10f);
            m.put("h1", 1f);
            m.put("h2", 0.5f);
            m.put("h3", 0.8f);
            m.put("h4", 0.4f);
            m.put("h5", 0.4f);
            m.put("h6", 0.1f);
            m.put("content", 0.06f);
            m.put("localkey", 10f);
            MultiFieldQueryParser mfparser = new MultiFieldQueryParser(Version.LUCENE_35,fields, analyzer,m);
            Query query = mfparser.parse(queryString);
            TopDocs results = searcher.search(query, maxnum);
            System.out.println(results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public Document getDoc(int docID){
		try{
			return searcher.doc(docID);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void loadGlobals(String filename){
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line=reader.readLine();
			avgLength=Float.parseFloat(line);
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public float getAvg(){
		return avgLength;
	}
	
	public static void main(String[] args){
		ImageSearcher search=new ImageSearcher("forIndex/index");
		search.loadGlobals("forIndex/global.txt");
		System.out.println("avg length = "+search.getAvg());
		
		TopDocs results=search.searchQuery("太阁", "abstract", 100);
		ScoreDoc[] hits = results.scoreDocs;
		for (int i = 0; i < hits.length; i++) { // output raw format
			Document doc = search.getDoc(hits[i].doc);
			System.out.println("doc=" + hits[i].doc + " score="
					+ hits[i].score+" picPath= "+doc.get("picPath"));
		}
	}
}
