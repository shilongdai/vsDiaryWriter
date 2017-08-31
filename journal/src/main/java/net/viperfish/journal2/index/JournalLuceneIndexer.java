package net.viperfish.journal2.index;

import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalIndexer;
import org.apache.lucene.store.FSDirectory;

/**
 * the built in indexer that uses Apache Lucene indexer for indexing
 *
 * @author sdai
 *
 */
public class JournalLuceneIndexer implements JournalIndexer {

    private Directory dir;
    private final DateFormat df;
    private Path indexDir;

    public JournalLuceneIndexer(Path p) {
        df = new SimpleDateFormat("EEE dd MM yyyy hh mm aa");
        this.indexDir = p;
    }

    protected String parseJournal(Journal j) {
        String textOnly = "";//Jsoup.parse(j.getContent()).text();
        String content = df.format(j.getTimestamp()) + " " + j.getSubject() + " " + textOnly;
        return content;
    }

    @Override
    public void add(Journal toAdd) {
        System.err.println("Adding Index");
        Document doc = new Document();
        IndexWriter writer = getWriter();
        doc.add(new StringField("id", toAdd.getId().toString(), Field.Store.YES));
        doc.add(new TextField("content", parseJournal(toAdd), Field.Store.NO));
        try {
            writer.addDocument(doc);
            writer.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean contains(Long id) {
        IndexReader reader = null;
        try {
            reader = getReader();
            IndexSearcher searcher = new IndexSearcher(reader);
            Query q = new QueryParser("id", new StandardAnalyzer()).parse(id.toString());
            TopDocs result = searcher.search(q, 1);
            ScoreDoc[] hits = result.scoreDocs;
            if (hits.length != 0) {
                return true;
            }
            return false;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IndexNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Directory getDir() throws IOException {
        if (dir == null) {
            dir = FSDirectory.open(indexDir);
        }
        return dir;
    }

    private QueryParser parser;

    /*
	 * (non-Javadoc)
	 * 
	 * @see net.viperfish.utils.index.Indexer#search(java.lang.String)
     */
    @Override
    public Iterable<Long> search(String query) {
        IndexReader reader = null;
        try {
            reader = getReader();
            IndexSearcher searcher = new IndexSearcher(reader);
            Query q = getParser().parse(convertQuery(query));
            TopDocs result = searcher.search(q, 50);
            ScoreDoc[] hits = result.scoreDocs;
            List<Long> p = new LinkedList<Long>();
            for (ScoreDoc i : hits) {
                Document doc = searcher.doc(i.doc);
                p.add(Long.parseLong(doc.getField("id").stringValue()));
            }
            return p;
        } catch (ParseException e) {
            return new LinkedList<Long>();
        } catch (IndexNotFoundException e) {
            return new LinkedList<Long>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see net.viperfish.utils.index.Indexer#delete(java.lang.Long)
     */
    @Override
    public void delete(Long id) {
        IndexWriter writer = getWriter();
        try {
            writer.deleteDocuments(new Term("id", id.toString()));
            writer.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see net.viperfish.utils.index.Indexer#clear()
     */
    @Override
    public void clear() {
        IndexWriter writer = getWriter();
        try {
            writer.deleteAll();
            writer.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private IndexWriter getWriter() {
        IndexWriter writer = null;
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        try {
            writer = new IndexWriter(getDir(), conf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer;
    }

    private IndexReader getReader() throws IndexNotFoundException {
        try {
            return DirectoryReader.open(getDir());
        } catch (IndexNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private QueryParser getParser() {
        if (parser == null) {
            parser = new QueryParser("content", new StandardAnalyzer());
        }
        return parser;
    }

    private String convertQuery(String raw) {
        String[] temp = raw.split(" ");
        String result = new String();
        result = temp[0];
        for (int i = 1; i < temp.length; i++) {
            result = result + " AND " + temp[i];
        }
        return result;
    }

}
