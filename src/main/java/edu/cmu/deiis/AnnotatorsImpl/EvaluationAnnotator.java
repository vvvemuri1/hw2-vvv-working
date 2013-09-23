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
    
    FSArray answerScores = new FSArray(jcas, answerScoreIndex.size());
    
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
      
      answerScores.set(i++, answerScore);
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
    FSArray answers = sortAnswers(jcas, answerScoreIndex, answerScores);    
    annotation.setSortedAnswers(answers);

    // Compute Precision
    float precision = ((float)correct)/(answerScores.size());
    annotation.setPrecision(precision);
    
    annotation.addToIndexes();
  }

  private FSArray sortAnswers(JCas jcas, FSIndex answerScoreIndex, FSArray answerScores) 
  {  
	FSArray answers = new FSArray(jcas, answerScoreIndex.size());
	  
    for (int j = 0; j < answerScoreIndex.size(); j++)
    {
      AnswerScore max = (AnswerScore) answerScores.get(j);
      int maxIndex = j;
      
      for (int k = j + 1; k < answerScoreIndex.size() - 1; k++)
      {
        AnswerScore current = (AnswerScore) answerScores.get(k);
        if ((current).getScore() > max.getScore())
        {
          max = current;
          maxIndex = k;
        }
      }

      AnswerScore temp = (AnswerScore)answerScores.get(j);
      answerScores.set(j, max);
      answerScores.set(maxIndex, temp);
    }
    
    for (int j = 0; j < answerScoreIndex.size(); j++)
    {
    	AnswerScore score = (AnswerScore)(answerScores.get(j));
    	answers.set(j, score.getAnswer());
    }
    
    return answers;
  }
}
