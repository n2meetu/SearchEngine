/**
 * Created by apple on 4/23/16.
 */
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.Explanation.*;
import org.apache.lucene.util.*;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SimpleMultiTermQuery extends MultiTermQuery {
    protected Term term;
    protected String field;
    protected float avgLength;

    private class SimpleFilteredTermEnum extends FilteredTermEnum{
        Analyzer analyzer;
        List<Term> termList;
        int pointer;
        public SimpleFilteredTermEnum(Term term,String field)
        {
            pointer = 0;
            termList = new ArrayList<Term>();
            try {
                Analyzer analyzer = new IKAnalyzer(false);
                TokenStream ts = analyzer.tokenStream("", new StringReader(term.text()));
                CharTermAttribute cta = ts.getAttribute(CharTermAttribute.class);
                while (ts.incrementToken()) {
                    termList.add(new Term(field, cta.toString()));
                }
            }
            catch(java.io.IOException e)
            {
                // Do nothing
            }
        }


        @Override
        protected boolean termCompare(Term term) {
            return term.text().equals(termList.get(pointer).text());
        }

        @Override
        public float difference() {
            return 0;
        }

        @Override
        protected boolean endEnum()
        {
            if(pointer<termList.size())
                return false;
            else
                return true;
        }

        @Override
        public boolean next() throws IOException
        {
            pointer++;
            if(pointer<termList.size())
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        @Override
        public Term term()
        {
            if(pointer<termList.size())
                return termList.get(pointer);
            else
                return null;
        }
    }

    private class MyRewriteMethod extends MultiTermQuery.RewriteMethod
    {

        @Override
        public Query rewrite(IndexReader indexReader, MultiTermQuery multiTermQuery) throws IOException {
            BooleanQuery query = new BooleanQuery();
            SimpleFilteredTermEnum termEnum = new SimpleFilteredTermEnum(term,field);
            while(!termEnum.endEnum())
            {
                query.add(new SimpleQuery(termEnum.term(), avgLength), BooleanClause.Occur.SHOULD);
                termEnum.next();
            }
            return query;
        }
    }


    public SimpleMultiTermQuery(Term term,String field,float avgLength) {
        this.term = term;
        this.field = field;
        this.avgLength = avgLength;
//        String text = term.text();
        setRewriteMethod(new MyRewriteMethod());
    }



    protected FilteredTermEnum getEnum(IndexReader reader) throws IOException {
        return new SimpleFilteredTermEnum(term,field);
    }

    public Term getTerm() {
        return this.term;
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if(!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }

        buffer.append(this.term.text());
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }
}
