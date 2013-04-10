# coding: utf-8
'''
Created on 10.04.2013

@author: кей
'''
import unittest


class Test(unittest.TestCase):


    def testName(self):
        print "Hello"


if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.testName']
    unittest.main()