# coding: utf-8
import requests

# sys
import json


class PathValue(object):
    def __init__(self, page, gen='Default', pos=0):
        self.pageName = page
        self.genName = gen
        self.pointPos = pos

    def assign_deserialized(self, obj):
        self.pageName = obj['pageName']
        self.genName = obj['genName']
        self.pointPos = obj['pointPos']


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


class DistributionElem(object):
    def __init__(self, elem):
        self.frequency = elem['frequency']
        self.unknown = elem['unknown']
        self.inBoundary = elem['inBoundary']


class AppAjax(object):
    def __init__(self, url, port):
        self.server = url + ":" + str(port)

    def _build_url(self, uri):
        return self.server + uri

    def get_user_summary_sync(self):
        uri = '/user_summary'
        r = requests.get(self._build_url(uri))
        r.raise_for_status()

        tmp = r.json()
        result = []
        for val in tmp:
            result.append(UserInfoValue(val))
        return result


class ResearchAjax(object):
    def __init__(self, url, port):
        self.server = url + ":" + str(port)

    def get_lengths_sentences(self, arg0):
        # FIXME: запрос длины предложений - статистика длин
        url = '/get_lengths_sentences'
        payload = {'arg0': json.dumps(arg0, default=lambda o: o.__dict__, sort_keys=True)}
        r = requests.get(self._build_url(url), params=payload)
        r.raise_for_status()

        return r.json()

    def _build_url(self, uri):
        return self.server + uri

    def get_distribution_sync(self, arg0):
        url = '/research/get_distribution'
        payload = {'arg0': json.dumps(arg0, default=lambda o: o.__dict__, sort_keys=True)}
        r = requests.get(self._build_url(url), params=payload)
        r.raise_for_status()

        tmp = r.json()
        result = []
        for val in tmp:
            result.append(DistributionElem(val))
        return result

    def get_pure_distribution(self, arg0):
        d = self.get_distribution_sync(arg0)
        frequencies = []
        for i, elem in enumerate(d):
            freq = elem.frequency
            frequencies.append(freq)
        return frequencies

    def create_or_replace_page(self):
        # Просто через post
        test_file = '../test_data/etalon.srt'
        with open(test_file, "r") as file_handle:
            data = file_handle.read()

        url = '/research/accept_text'

        payload = {'name': self.get_research_page_name(), 'text': data}
        r = requests.post(self._build_url(url), data=json.dumps(payload))
        r.raise_for_status()

    @staticmethod
    def get_research_page_name():
        return 'research_page'
