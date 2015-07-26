package edu.hit.wilab.trec.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ansj.lucene4.AnsjAnalysis;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.hit.wilab.trec.network.Config;
import edu.hit.wilab.trec.network.GenerationQuery;
import edu.hit.wilab.trec.network.Log;

public class SearchIndex {

	private static Logger logger = Logger.getLogger(SearchIndex.class);

	private Directory directory;

	public static String indexPath = "index"; // 建立索引文件的目录

	private IndexSearcher indexSearcher = null;

	private Analyzer analyzer = null;

	public Set<String> set_pmcid = new HashSet<String>();

	public void init() {
		try {
			directory = FSDirectory.open(new File(indexPath));

			analyzer = new AnsjAnalysis();

			DirectoryReader open = DirectoryReader.open(directory);
			indexSearcher = new IndexSearcher(open);
			//LMJelinekMercer
			indexSearcher.setSimilarity(new LMJelinekMercerSimilarity(0.7f));
			//TF-IDF
//			TFIDFSimilarity tfidfSIM = new DefaultSimilarity();
//			indexSearcher.setSimilarity(tfidfSIM);
			
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public TopDocs searchArticle(List<List<String[]>> sentenceList,
			String[] fields, int number) {

		TopDocs topDocs = null;

		try {

			// String[] fields = {"title","abstract","keywords"};

			// String[] fields = {"title","abstract","keywords","body"};

			BooleanQuery bq_all = new BooleanQuery();

			for (int i = 0; i < sentenceList.size(); i++) {

				List<String[]> sentencelist_temp = sentenceList.get(i);

				BooleanQuery bq_temp = new BooleanQuery();

				for (int j = 0; j < sentencelist_temp.size(); j++) {

					String[] sentence = sentencelist_temp.get(j);

					QueryParser queryParser = new MultiFieldQueryParser(
							Version.LUCENE_40, fields, analyzer);

					Query query = queryParser.parse(sentence[0]);

					query.setBoost(Float.parseFloat(sentence[1]));

					bq_temp.add(query, Occur.MUST);

				}

				bq_all.add(bq_temp, Occur.SHOULD);

			}

			topDocs = indexSearcher.search(bq_all, null, number);

			System.out.println("搜索结果：" + topDocs.totalHits + "个！");

		} catch (IOException e) {
			System.err.println(e);
		} catch (ParseException e) {
			System.err.println(e);
		}

		return topDocs;

	}

	public TopDocs searchArticle_word(String[] word, String[] fields, int number) {

		TopDocs topDocs = null;

		try {

			// String[] fields = {"title","abstract","keywords"};

			// String[] fields = {"title","abstract","keywords","body"};

			BooleanQuery bq = new BooleanQuery();

			QueryParser queryParser = new MultiFieldQueryParser(
					Version.LUCENE_40, fields, analyzer);

			Query query = queryParser.parse(word[0]);

			query.setBoost(Float.parseFloat(word[1]));

			bq.add(query, Occur.MUST);

			topDocs = indexSearcher.search(bq, null, number);

			System.out.println("搜索结果：" + topDocs.totalHits + "个！");

		} catch (IOException e) {
			System.err.println(e);
		} catch (ParseException e) {
			System.err.println(e);
		}

		return topDocs;

	}

	public List<String[]> lookResult(TopDocs topDocs) {

		if (topDocs == null) {
			return null;
		}

		List<String[]> list_article = new ArrayList<String[]>();

		for (int i = 0; i < topDocs.scoreDocs.length; i++) {

			String[] article = new String[7];

			try {

				String pmcid = indexSearcher.doc(topDocs.scoreDocs[i].doc).get(
						"pmcid");

				if (set_pmcid.contains(pmcid)) {
					continue;
				} else {
					set_pmcid.add(pmcid);
				}

				article[0] = pmcid;
				article[1] = indexSearcher.doc(topDocs.scoreDocs[i].doc).get(
						"title");
				article[2] = indexSearcher.doc(topDocs.scoreDocs[i].doc).get(
						"abstract");
				article[3] = indexSearcher.doc(topDocs.scoreDocs[i].doc).get(
						"keywords");
				article[4] = indexSearcher.doc(topDocs.scoreDocs[i].doc).get(
						"body");
				article[5] = String.valueOf(topDocs.scoreDocs[i].score);
				article[6] = indexSearcher.doc(topDocs.scoreDocs[i].doc).get(
						"reference");

				list_article.add(article);

			} catch (IOException e) {
				System.err.println(e);
			}

		}

		return list_article;

	}

	public List<String[]> searchIndex(List<List<String[]>> sentenceList,
			String[] fields, int number) {

		SearchIndex si = new SearchIndex();

		si.init();

		long start = System.currentTimeMillis();

		List<String[]> list_article = si.lookResult(si.searchArticle(
				sentenceList, fields, number));

		long end = System.currentTimeMillis();

		System.out.println("搜索时间：" + (end - start) + "ms！");

		return list_article;

	}

	public List<String[]> searchIndex_word(String[] word, String[] fields,
			int number) {

		SearchIndex si = new SearchIndex();

		si.init();

		long start = System.currentTimeMillis();

		List<String[]> list_article = si.lookResult(si.searchArticle_word(word,
				fields, number));

		long end = System.currentTimeMillis();

		System.out.println("搜索时间：" + (end - start) + "ms！");

		return list_article;

	}

	public static void main(String[] args) {

		// 载入Log日志

		Log.loadLogProperties();

		logger.info("日志系统加载成功！");

		// 载入配置文件

		Config config = new Config();

		config.loadConf();

		logger.info("配置文件加载成功！");

		logger.info("加载查询集...");

		String[] topic_type = Config.type_diag;

		GenerationQuery gq = new GenerationQuery();
		List<List<String[]>> sentenceList = gq.query_non(Config.topic_num,
				topic_type);

		logger.info("加载查询集完成!");

		SearchIndex si = new SearchIndex();

		si.init();

		long start = System.currentTimeMillis();

		List<String[]> list_article = si.searchIndex(sentenceList, Config.fields_init,
				Config.search_init);

		long end = System.currentTimeMillis();

		System.out.println("搜索时间：" + (end - start) + "ms！");

		List<String> list_id = Test.read_list("./Trec_wordsets/qrels2014.txt",Config.topic_num.split("_")[1]);
		logger.info("正确答案共有"+list_id.size()+"个");
		
		int result_num = 0;
		
		for (String[] article : list_article) {
			if(list_id.contains(article[0])){
				result_num++;
				logger.info(article[0]);
			}
		}
		
		logger.info("Lucene挖掘到"+result_num+"个节点正确！");
	}

}
