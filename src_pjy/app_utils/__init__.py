# coding: utf-8
import nltk.data
import dals

if __name__=='__main__':
    print 'Done'
    
    text = """ Punkt knows that the periods in Mr. Smith and Johann S. Bach
    do not mark sentence boundaries.  And sometimes sentences
    can start with non-capitalized words.  i is a good variable
    name.
    """
    #import nltk
    #nltk.download()
    
     
    tokenizer = nltk.data.load('nltk:tokenizers/punkt/english.pickle')
    tokenizer.tokenize('Hello.  This is a test.  It works!')
""" 
sent_detector = nltk.data.load('tokenizers/punkt/english.pickle')

print '\n-----\n'.join(sent_detector.tokenize(text.strip()))"""