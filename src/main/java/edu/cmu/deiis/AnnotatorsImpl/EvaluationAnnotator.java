package edu.cmu.deiis.AnnotatorsImpl;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.AnswerScore;
import edu.cmu.deiis.types.Evaluation;

public class EvaluationAnnotator extends JCasAnnotator_ImplBase
{
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException 
  {
    FSIndex answerScoreIndex = jcas.getAnnotationIndex(AnswerScore.type);
    Iterator answerScoreIter = answerScoreIndex.iterator();
    
    int answerStart = 0;
    int answerEnd = 0;
    
    int correct = 0;
    int i = 0;
    FSArray answers = new FSArray(jcas, answerScoreIndex.size());
    
    while(answerScoreIter.hasNext())
    {
      AnswerScore answerScore = (AnswerScore) answerScoreIter.next();
      Answer answer = (Answer) answerScore.getAnswer();
      
      if (i == 0)
      {
        answerStart = answer.getBegin();
      }
      else if (i == answerScoreIndex.size() - 1)
      {
        answerEnd = answer.getEnd();
      }
      
      answers.set(i++, answer);
      if (answer.getIsCorrect())
      {
        correct++;
      }
    }
    
    Evaluation annotation = new Evaluation(jcas);
    annotation.setBegin(answerStart);
    annotation.setEnd(answerEnd);
    annotation.setCasProcessorId(EvaluationAnnotator.class.getName());
    annotation.setConfidence(1.0f);
    
    // Sort answers
    sortAnswers(answerScoreIndex, answers);    
    annotation.setSortedAnswers(answers);

    // Compute Precision
    float precision = ((float)correct)/(answers.size());
    annotation.setPrecision(precision);
    
    annotation.addToIndexes();
  }

  private void sortAnswers(FSIndex answerScoreIndex, FSArray answers) 
  {
    /*for (int j = 0; j < answerScoreIndex.size() - 1; j++)
    {
      AnswerScore max = (Answer) answers.get(j);
      int maxIndex = j;
      
      for (int k = j + 1; k < answerScoreIndex.size() - 1; k++)
      {
        AnswerScore current = (AnswerScore)answers.get(k);
        if ((current).getScore() > max.getScore())
        {
          max = current;
          maxIndex = k;
        }
      }

      Answer temp = (Answer)answers.get(j);
      answers.set(j, max);
      answers.set(maxIndex, temp);
    }*/
  }
}
