# coding: utf-8

# App
from spiders._utils import parser_target_for_spider

def base_spider(target_fname):
    target_generator = parser_target_for_spider(target_fname)
    for at in target_generator:
        if at[0]:
            # Строем папку
            print at
        else:
            return at
    return '', [0,'']
        