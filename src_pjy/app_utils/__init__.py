# coding: utf-8
import nltk.data
import dals



# Std
import re
import dals.os_io.io_wrapper as dal

def write_result_file(result_list, fname):
    sets = dal.get_utf8_template()
    sets['howOpen'] = 'w'
    sets['name'] = fname
    dal.list2file(sets, result_list)
    
def read_utf_txt_file(fname):
    sets = dal.get_utf8_template()
    sets['name'] = fname
    return dal.file2list(sets) 



def remove_forward_and_back_spaces(line):
    if line:
        return re.sub("^\s+|\n|\r|\s+$", '', line)
    else:
        return None
    
def remove_fandb_spaces_in_tuple(src):
    tmp = []
    for at in src:
        tmp.append(remove_forward_and_back_spaces(at))
        
    return tuple(tmp)

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