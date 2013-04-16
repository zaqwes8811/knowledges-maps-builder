# coding: utf-8
'''
Created on 03.04.2013

@author: кей
'''
#import nltk
from nltk.stem.porter import PorterStemmer

def fake_compressor(word):
    return word

def simple_word_splitter(sentence):
    return sentence.split(' ')


def real_english_stemmer(word):
    #print PorterStemmer().stem_word(word)
    return PorterStemmer().stem_word(word)
    
if __name__=="__main__":
    print real_english_stemmer('hellos')
    print "Done"
