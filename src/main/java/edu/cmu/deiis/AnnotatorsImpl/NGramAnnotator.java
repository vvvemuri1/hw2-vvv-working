package edu.cmu.deiis.AnnotatorsImpl;

import java.util.Iterator;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import edu.cmu.deiis.types.NGram;
import edu.cmu.deiis.types.Token;

/**
 * The system will annotate 1-, 2- and 3-grams of consecutive tokens. 
 * @author Vinay Vyas Vemuri <vvv@andrew.cmu.edu>
 */
public class NGramAnnotator extends JCasAnnotator_ImplBase 
{  
  /**
   * Annotates 1-, 2- and 3-grams in a sentence.
   * @param jcas JCas object that provides access to the CAS.
   */
  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException 
  {
    FSIndex tokenIndex = jcas.getAnnotationIndex(Token.type);
    Iterator tokenIter = tokenIndex.iterator();
    
    Token prevPrevToken = null;
    Token prevToken = null;
    
    while(tokenIter.hasNext())
    {
      Token sentenceToken = (Token) tokenIter.next();      

      int sentenceIndex = setupUnigram(jcas, sentenceToken);
      setupBigram(jcas, prevToken, sentenceToken, sentenceIndex);
      setupTrigram(jcas, prevPrevToken, prevToken, sentenceToken, sentenceIndex);
        
      prevPrevToken = prevToken;
      prevToken = sentenceToken;
    }
  }

  /**
   * Creates a unigram annotation and adds it to the CAS
   * @param jcas JCas object that provides access to the CAS.
   * @param sentenceToken A Question/Answer token which is part of the Unigram
   * @return Index of the sentence to which the Unigram belongs.
   */
  private int setupUnigram(JCas jcas, Token sentenceToken) 
  {
    NGram annotation = new NGram(jcas);
    annotation.setBegin(sentenceToken.getBegin());
    annotation.setEnd(sentenceToken.getEnd());
    annotation.setConfidence(sentenceToken.getConfidence());
    annotation.setCasProcessorId(NGramAnnotator.class.getName());
    annotation.setElementType(sentenceToken.getClass().getName());
    
    int sentenceIndex = sentenceToken.getSentenceId();
    FSArray elements = new FSArray(jcas,1);
    elements.set(0, sentenceToken);
    
    annotation.setElements(elements);
    annotation.addToIndexes();
    return sentenceIndex;
  }

  /**
   * Creates a bigram annotation and adds it to the CAS
   * @param jcas JCas object that provides access to the CAS.
   * @param prevToken The first token of the Bigram
   * @param sentenceToken The second token of the Bigram
   * @param sentenceIndex Index of the sentence to which the Bigram belongs.
   */
  private void setupBigram(JCas jcas, Token prevToken, Token sentenceToken, int sentenceIndex)
  {
    NGram annotation;
    FSArray elements;
    if (prevToken != null && 
            sentenceIndex == prevToken.getSentenceId())
    {
      annotation = new NGram(jcas);
      annotation.setBegin(prevToken.getBegin());
      annotation.setEnd(sentenceToken.getEnd());
      
      annotation.setConfidence(sentenceToken.getConfidence());
      annotation.setCasProcessorId(NGramAnnotator.class.getName());
      annotation.setElementType(sentenceToken.getClass().getName());
      
      elements = new FSArray(jcas,2);
      elements.set(0, prevToken);
      elements.set(1, sentenceToken);
      
      annotation.setElements(elements);
      annotation.addToIndexes();
    }
  }
  
  /**
   * Creates a trigram annotation and adds it to the CAS.
   * @param jcas JCas object that provides access to the CAS.
   * @param prevPrevToken The first token of the Trigram.
   * @param prevToken The second token of the Trigram.
   * @param sentenceToken The third token of the Trigram.
   * @param sentenceIndex Index of the sentence to which the Trigram belongs.
   */
  private void setupTrigram(JCas jcas, Token prevPrevToken, Token prevToken, Token sentenceToken,
          int sentenceIndex) 
  {
    NGram annotation;
    FSArray elements;
    if (prevPrevToken != null && 
            sentenceIndex == prevPrevToken.getSentenceId())
    {
      annotation = new NGram(jcas);
      annotation.setBegin(prevPrevToken.getBegin());
      annotation.setEnd(sentenceToken.getEnd());
      annotation.setConfidence(sentenceToken.getConfidence());
      annotation.setCasProcessorId(NGramAnnotator.class.getName());
      annotation.setElementType(sentenceToken.getClass().getName());
      
      elements = new FSArray(jcas,3);
      elements.set(0, prevPrevToken);
      elements.set(1, prevToken);
      elements.set(2, sentenceToken);
 
      annotation.setElements(elements);
      annotation.addToIndexes();
    }
  }
}