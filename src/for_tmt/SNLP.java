package for_tmt;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.List;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class SNLP {

	File f;
	Annotation document;

	public void work(String id, String tok, String filename) throws IOException {
		f = new File("data/snlp");
		if (!f.exists())
			f.mkdirs();
		f = new File("data/snlp/"+filename);
		if (!f.exists())
			f.createNewFile();

		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		document = new Annotation(tok);

		// run all Annotators on this text
		pipeline.annotate(document);
		writeFile(id);
	}

	public void writeFile(String id) {

		if (f.canWrite())
			try (PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter(f, true)))) {
				List<CoreMap> sentences = document
						.get(SentencesAnnotation.class);
				out.print(id+", ");
				for (CoreMap sentence : sentences) {
					// traversing the words in the current sentence
					// a CoreLabel is a CoreMap with additional token-specific
					// methods
					for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
						// this is the text of the token
						String word = token.get(TextAnnotation.class);
						
						word = word.replaceAll("[\\pP‘’“”`'.:> <\",]", " "); 
						out.print(word + " ");

					}
				}
				out.println("");

			} catch (IOException e) {
				e.printStackTrace();
				// exception handling left as an exercise for the reader
			}

	}
}
