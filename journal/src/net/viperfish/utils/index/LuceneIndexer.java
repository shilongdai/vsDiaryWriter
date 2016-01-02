package net.viperfish.utils.index;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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

public abstract class LuceneIndexer<T> implements Indexer<T> {

	private QueryParser parser;

	/* (non-Javadoc)
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

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see net.viperfish.utils.index.Indexer#add(T)
	 */
	@Override
	public abstract void add(T toAdd);

	/* (non-Javadoc)
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

	protected abstract Directory getDir();

	protected IndexWriter getWriter() {
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

	protected IndexReader getReader() throws IndexNotFoundException {
		try {
			return DirectoryReader.open(getDir());
		} catch (IndexNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected QueryParser getParser() {
		if (parser == null) {
			parser = new QueryParser("content", new StandardAnalyzer());
		}
		return parser;
	}

	protected String convertQuery(String raw) {
		String[] temp = raw.split(" ");
		String result = new String();
		result = temp[0] + "~";
		for (int i = 1; i < temp.length; i++) {
			result = result + " AND " + temp[i] + "~";
		}
		return result;
	}

}
