# coding: utf-8
'''
Created on 02.04.2013

@author: кей
'''

def roughly_split_to_sentences(one_line):
    """ Сделано супер просто. Но реально алгоритмы не таки простые. """
    _kInsertConst = '@@@@'
    one_line_with_inserts = ''
    for at in one_line:
        one_line_with_inserts += at
        if at == '.' or at == '!' or at == '?' or at == ']':
            one_line_with_inserts += _kInsertConst
            
    # Чистый контент - набор предложений
    sentences_lst = one_line_with_inserts.split(_kInsertConst)
    return sentences_lst