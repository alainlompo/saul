package edu.illinois.cs.cogcomp.saulexamples.nlp.DataModelTests

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.{ Sentence, TextAnnotation }
import edu.illinois.cs.cogcomp.saulexamples.data.{ Document, DocumentReader }
import edu.illinois.cs.cogcomp.saulexamples.nlp.sensors

import scala.collection.JavaConversions._
/** Created by Parisa on 10/4/15.
  */
object graphPopulationKeys {

  def main(args: Array[String]): Unit = {
    val dat: List[Document] = new DocumentReader("./data/20newsToy/train").docs.toList.slice(1, 3)
    val taList = dat.map(x => sensors.curator(x))
    val sentenceList = taList.flatMap(x => x.sentences())
    modelWithKeys.document populate taList
    modelWithKeys.sentence populate sentenceList

    val x1 = modelWithKeys.getFromRelation[Sentence, TextAnnotation](sentenceList.head)
    val x2 = modelWithKeys.getFromRelation[TextAnnotation, Sentence](taList.head)

    println(s"x1.size = ${x1.size}")
    println(s"x2.size = ${x2.size}")

    print("finished")

  }
}