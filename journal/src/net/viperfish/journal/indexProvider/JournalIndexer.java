package net.viperfish.journal.indexProvider;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.jsoup.Jsoup;

import net.viperfish.journal.framework.Journal;
import net.viperfish.utils.index.LuceneIndexer;

/**
 * the built in indexer that uses Apache Lucene indexer for indexing
 * 
 * @author sdai
 *
 */
public class JournalIndexer extends LuceneIndexer<Journal> {

	private Directory dir;
	private final DateFormat df;

	public JournalIndexer() {
		df = new SimpleDateFormat("EEE dd MM yyyy hh mm aa");
	}

	protected String parseJournal(Journal j) {
		String textOnly = Jsoup.parse(j.getContent()).text();
		String content = df.format(j.getDate()) + " " + j.getSubject() + " " + textOnly;
		return content;
	}

	@Override
	public void add(Journal toAdd) {
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

	@Override
	protected Directory getDir() {
		if (dir == null) {
			dir = new RAMDirectory();
		}
		return dir;
	}

	@Override
	public boolean isMemoryBased() {
		return true;
	}

}
