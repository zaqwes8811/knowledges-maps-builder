
import copy

class PathValue(object):
    def __init__(self, page, gen='Default', pos=0):
        self.pageName = page
        self.genName = gen
        self.pointPos = pos

    def assign_deserialized(self, obj):
        self.pageName = obj['pageName']
        self.genName = obj['genName']
        self.pointPos = obj['pointPos']

    def clone(self):
        return copy.copy(self)

    def set_position(self, pos):
        self.pointPos = pos


class UserInfoValue(object):
    def __init__(self, obj):
        self.pageName = None
        self.genNames = None
        self.pointPos = None
        self.assign_deserialized(obj)

    def assign_deserialized(self, obj):
        self.pageName = obj['pageName']
        self.genNames = obj['genNames']
        # self.pointPos = obj['pointPos']  # no exist. was bug coupled with it


class NGramData(object):
    def __init__(self, o):
        self.pointPos = o['pointPos']
        self.word = o['word']
        self.sentences = o['sentences']

    def get_position(self):
        return self.pointPos

    def clone(self):
        return copy.copy(self)

class DistributionElem(object):
    def __init__(self, elem):
        self.frequency = elem['frequency']
        self.unknown = elem['unknown']
        self.inBoundary = elem['inBoundary']

