package edu.illinois.cs.cogcomp.lbjava.nlp.seg;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;
import edu.illinois.cs.cogcomp.lbjava.util.ClassUtils;

import java.util.Vector;


/**
  * This class may be used to produce a detailed report of the <i>segment by
  * segment</i> performance of a given classifier on given labeled testing
  * data.  Segment by segment performance is computed by using a specified
  * {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token} classifier to induce the predicted segments, and then
  * computing precision, recall, and F<sub>1</sub> measures on those segments.
  * A predicted segment is judged as different than a labeled segment if the
  * two segments start or end at different {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s, or if they have
  * different types.
  *
  * <p> It is assumed that both of the specified {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token} classifiers
  * (one giving labels and the other giving predictions) produce discrete
  * predicitions of the form <code>B-<i>type</i></code>,
  * <code>I-<i>type</i></code>, and <code>O</code> to represent the beginning
  * of a segment of type <i>type</i>, a token inside a segment of type
  * <i>type</i>, and a token outside of any segment respectively.
  *
  * <p> It is also assumed that the specified {@link edu.illinois.cs.cogcomp.lbjava.parse.Parser}
  * produces {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s linked to each other via the <code>previous</code>
  * and <code>next</code> fields inherited from
  * {@link edu.illinois.cs.cogcomp.lbjava.parse.LinkedChild}.  In order to invoke this class as a
  * program on the command line, it must also be the case that the parser
  * implements a constructor with a single <code>String</code> argument.
  *
  * <h4>Command Line Usage</h4>
  * <blockquote><code>
  *   java edu.illinois.cs.cogcomp.lbjava.nlp.seg.BIOTester &lt;classifier&gt; &lt;labeler&gt;
  *                               &lt;parser&gt; &lt;test file&gt;
  * </code></blockquote>
  *
  * <h3>Input</h3>
  * The first three arguments must be fully qualified class names.  The fourth
  * is the name of a file containing labeled testing data to be parsed by the
  * parser.
  *
  * <h3>Output</h3>
  * The output is generated by the {@link edu.illinois.cs.cogcomp.lbjava.classify.TestDiscrete} class.
  *
  * @author Nick Rizzolo
 **/
public class BIOTester
{
  /** A BIO classifier that classifies {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s. */
  protected Classifier classifier;
  /** A BIO classifier that produces the true labels of the {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s. */
  protected Classifier labeler;
  /** A parser that produces {@link edu.illinois.cs.cogcomp.lbjava.nlp.seg.Token}s. */
  protected Parser parser;


  /**
    * Initializing constructor.
    *
    * @param c  The value for {@link #classifier}.
    * @param l  The value for {@link #labeler}.
    * @param p  The value for {@link #parser}.
   **/
  public BIOTester(Classifier c, Classifier l, Parser p) {
    classifier = c;
    labeler = l;
    parser = p;
  }


  /**
    * This method runs the tester, packaging the results in a
    * <code>TestDiscrete</code> object.
    *
    * @return The performance results.
   **/
  public TestDiscrete test() {
    TestDiscrete results = new TestDiscrete();
    results.addNull("O");

    for (Token t = (Token) parser.next(); t != null;
         t = (Token) parser.next()) {
      Vector vector = new Vector();
      for (; t.next != null; t = (Token) parser.next()) vector.add(t);
      vector.add(t);

      int N = vector.size();
      String[] predictions = new String[N], labels = new String[N];

      for (int i = 0; i < N; ++i) {
        predictions[i] = classifier.discreteValue(vector.get(i));
        labels[i] = labeler.discreteValue(vector.get(i));
      }

      for (int i = 0; i < N; ++i) {
        String p = "O", l = "O";
        int pEnd = -1, lEnd = -1;

        if (predictions[i].startsWith("B-")
            || predictions[i].startsWith("I-")
               && (i == 0
                   || !predictions[i - 1]
                       .endsWith(predictions[i].substring(2)))) {
          p = predictions[i].substring(2);
          pEnd = i;
          while (pEnd + 1 < N && predictions[pEnd + 1].equals("I-" + p))
            ++pEnd;
        }

        if (labels[i].startsWith("B-")
            || labels[i].startsWith("I-")
               && (i == 0 || !labels[i - 1].endsWith(labels[i].substring(2))))
        {
          l = labels[i].substring(2);
          lEnd = i;
          while (lEnd + 1 < N && labels[lEnd + 1].equals("I-" + l)) ++lEnd;
        }

        if (!p.equals("O") || !l.equals("O")) {
          if (pEnd == lEnd) results.reportPrediction(p, l);
          else {
            if (!p.equals("O")) results.reportPrediction(p, "O");
            if (!l.equals("O")) results.reportPrediction("O", l);
          }
        }
      }
    }

    return results;
  }


  /**
    * The command line program simply instantiates an object of this class and
    * calls its {@link #test()} method.
   **/
  public static void main(String[] args) {
    String classifierName = null;
    String labelerName = null;
    String parserName = null;
    String inputFile = null;

    try {
      classifierName = args[0];
      labelerName = args[1];
      parserName = args[2];
      inputFile = args[3];
      if (args.length > 4) throw new Exception();
    }
    catch (Exception e) {
      System.err.println(
"usage: java edu.illinois.cs.cogcomp.lbjava.nlp.seg.BIOTester <classifier> <labeler> <parser> <test file>");
      System.exit(1);
    }

    Classifier classifier = ClassUtils.getClassifier(classifierName);
    Classifier labeler = ClassUtils.getClassifier(labelerName);
    Parser parser =
      ClassUtils.getParser(parserName, new Class[]{String.class},
              new String[]{inputFile});

    new BIOTester(classifier, labeler, parser)
      .test().printPerformance(System.out);
  }
}

