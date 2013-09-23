package edu.cmu.deiis.AnnotatorsImpl;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.AnswerScore;

public class AnswerScoringAnnotator extends JCasAnnotator_ImplBase 
{
  private static final float GOLD_STANDARD_INCORRECT = 0f;
  private static final float GOLD_STANDARD_CORRECT = 1f;

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException 
  {
    FSIndex answerIndex = jcas.getAnnotationIndex(Answer.type);
    Iterator answerIter = answerIndex.iterator();

    while(answerIter.hasNext())
    {
      AnswerScore answerScore = new AnswerScore(jcas);
      Answer answer = (Answer) answerIter.next();

      answerScore.setCasProcessorId(AnswerScoringAnnotator.class.getName());
      answerScore.setBegin(answer.getBegin());
      answerScore.setEnd(answer.getEnd());
      answerScore.setAnswer(answer);
      
      if (answer.getIsCorrect())
      {
        answerScore.setScore(GOLD_STANDARD_CORRECT);
      }
      else
      {
        answerScore.setScore(GOLD_STANDARD_INCORRECT);
      }
      
      answerScore.addToIndexes();
    }
  }
}