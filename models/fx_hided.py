'''
Created on 19.07.2013

@author: кей
'''


def get_near_uniform_iterative(code_book, size_code_book, max_value, ranges=None):
    """ Precond: Массив отсортирован и элементы уникальны по id. """
    value = random.random()*max_value
    n = size_code_book
    while n != 1: 
        one_size = n/2  # Округляется в меньшую
        two_size = n-one_size
        A = ranges[:one_size]
        B = ranges[one_size:]
        if find_tuple(A, one_size, value):
        #if find(A, one_size, value):
            ranges = A
            n = one_size
        else: 
            ranges = B
            n = two_size
       
    return ranges[0][2]
    #return ranges[0].get_id()

            
def get_near_uniform_linear(code_book, count_points, max_value, ranges=None, tmp=None):
    value = random.random()*max_value
    vector = 0
    for code in code_book:
        if value <= code:
            break
        vector += 1
    return vector