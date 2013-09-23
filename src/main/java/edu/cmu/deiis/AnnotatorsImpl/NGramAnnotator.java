package edu.cmu.deiis.AnnotatorsImpl;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import edu.cmu.deiis.types.NGram;
import edu.cmu.deiis.types.Token;

public class NGramAnnotator extends JCasAnnotator_ImplBase 
{  
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException 
  {
    FSIndex tokenIndex = jcas.getAnnotationIndex(Token.type);
    Iterator tokenIter = tokenIndex.iterator();
    
    Token prevPrevToken = null;
    Token prevToken = null;
    
    while(tokenIter.hasNext())
    {
      Token qaToken = (Token) tokenIter.next();      

      int sentenceIndex = setupUnigram(jcas, qaToken);
      setupBigram(jcas, prevToken, qaToken, sentenceIndex);
      setupTrigram(jcas, prevPrevToken, prevToken, qaToken, sentenceIndex);
        
      prevPrevToken = prevToken;
      prevToken = qaToken;
    }
  }

  private int setupUnigram(JCas jcas, Token qaToken) 
  {
    NGram annotation = new NGram(jcas);
    annotation.setBegin(qaToken.getBegin());
    annotation.setEnd(qaToken.getEnd());
    annotation.setConfidence(qaToken.getConfidence());
    annotation.setCasProcessorId(NGramAnnotator.class.getName());
    annotation.setElementType(qaToken.getClass().getName());
    
    int sentenceIndex = qaToken.getSentenceId();
    FSArray elements = new FSArray(jcas,1);
    elements.set(0, qaToken);
    
    annotation.setElements(elements);
    annotation.addToIndexes();
    return sentenceIndex;
  }

  private void setupBigram(JCas jcas, Token prevToken, Token qaToken, int sentenceIndex)
  {
    NGram annotation;
    FSArray elements;
    if (prevToken != null && 
            sentenceIndex == prevToken.getSentenceId())
    {
      annotation = new NGram(jcas);
      annotation.setBegin(prevToken.getBegin());
      annotation.setEnd(qaToken.getEnd());
      
      annotation.setConfidence(qaToken.getConfidence());
      annotation.setCasProcessorId(NGramAnnotator.class.getName());
      annotation.setElementType(qaToken.getClass().getName());
      
      elements = new FSArray(jcas,2);
      elements.set(0, prevToken);
      elements.set(1, qaToken);
      
      annotation.setElements(elements);
      annotation.addToIndexes();
    }
  }
  
  private void setupTrigram(JCas jcas, Token prevPrevToken, Token prevToken, Token qaToken,
          int sentenceIndex) 
  {
    NGram annotation;
    FSArray elements;
    if (prevPrevToken != null && 
            sentenceIndex == prevPrevToken.getSentenceId())
    {
      annotation = new NGram(jcas);
      annotation.setBegin(prevPrevToken.getBegin());
      annotation.setEnd(qaToken.getEnd());
      annotation.setConfidence(qaToken.getConfidence());
      annotation.setCasProcessorId(NGramAnnotator.class.getName());
      annotation.setElementType(qaToken.getClass().getName());
      
      elements = new FSArray(jcas,3);
      elements.set(0, prevPrevToken);
      elements.set(1, prevToken);
      elements.set(2, qaToken);
 
      annotation.setElements(elements);
      annotation.addToIndexes();
    }
  }
}