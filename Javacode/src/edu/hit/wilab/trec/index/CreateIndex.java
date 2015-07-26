package edu.hit.wilab.trec.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.ansj.lucene4.AnsjAnalysis;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogByteSizeMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

public class CreateIndex {

	private Directory directory;

	public static String indexPath = "index"; // 建立索引文件的目录

	private Analyzer analyzer = null;

	private IndexWriter indexWriter;

	public static void main(String[] args) {

		CreateIndex createIndex = new CreateIndex();
		createIndex.init();

		createIndex.logicXML(args[0]);

	}

	public void init() {
		try {
			analyzer = new AnsjAnalysis();
			directory = FSDirectory.open(new File(indexPath));
			indexWriter = getIndexWriter(directory);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public void logicXML(String folderName) {
		try {
			List<String> list_folder = File_Data.traverseFolder("./dataset/"
					+ folderName);
			for (String folder : list_folder) {

				List<String> list_subfile = File_Data
						.traverseFolder("./dataset/" + folderName + "/"
								+ folder);

				List<Object[]> list_content = new ArrayList<Object[]>();

				for (String subfile : list_subfile) {

					XMLFile xml = new XMLFile();

					List<String> list_temp = xml.parserXML("./dataset/"
							+ folderName + "/" + folder + "/" + subfile);
					
					if(list_temp == null){
						continue;
					}
					
					Object[] object = new Object[2];
					object[0] = list_temp;
					object[1] = subfile.substring(0, subfile.length() - 5);

					list_content.add(object);

				}

				createIndex(list_content);

				System.out.println("./dataset/" + folderName + "/" + folder);

			}

			closeWriter();

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void createIndex(List<Object[]> list) throws IOException {

		for (Object[] object : list) {
			List<String> list_content = (List<String>) object[0];
			String pmcid = (String) object[1];
			Document document = new Document();
			// System.out.println("pmcid::"+pmcid);
			StringField sf_pmcid = new StringField("pmcid", pmcid, Store.YES);
			sf_pmcid.setBoost(1.0f);
			document.add(sf_pmcid);
			// System.out.println(list_content.get(0));
			TextField sf_title = new TextField("title", list_content.get(1),
					Store.YES);
			sf_title.setBoost(1.0f);
			document.add(sf_title);
			// System.out.println(list_content.get(1));
			TextField sf_abstract = new TextField("abstract",
					list_content.get(2), Store.YES);
			sf_abstract.setBoost(1.0f);
			document.add(sf_abstract);
			// System.out.println(list_content.get(2));
			TextField sf_keywords = new TextField("keywords",
					list_content.get(3), Store.YES);
			sf_keywords.setBoost(1.0f);
			document.add(sf_keywords);
			// System.out.println(list_content.get(3));
			TextField sf_reference = new TextField("reference", list_content.get(4),
					Store.YES);
			sf_reference.setBoost(1.0f);
			document.add(sf_reference);
			// System.out.println(list_content.get(4));
			TextField sf_body = new TextField("body", list_content.get(5),
					Store.YES);
			sf_body.setBoost(1.0f);
			document.add(sf_body);
			// System.out.println(list_content.get(4));
			indexWriter.addDocument(document);

		}

		indexWriter.commit();

	}

	/**
	 * 获得indexwriter对象
	 * 
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	public IndexWriter getIndexWriter(Directory dir) {
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_40,
				analyzer);

		LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
		// 达到Config.mergeFactor个文件时就和合并
		mergePolicy.setMergeFactor(200);
		iwc.setMergePolicy(mergePolicy);
		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			System.err.println(e);
		}

		return indexWriter;
	}

	/**
	 * 关闭indexwriter对象
	 * 
	 * @throws Exception
	 */
	public void closeWriter() {
		if (indexWriter != null) {
			try {
				indexWriter.close();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

}
