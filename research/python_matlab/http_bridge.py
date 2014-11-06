# coding: utf-8

import protocol as pro

#
import requests

# sys
import json


class AppAjax(object):
    def __init__(self, url, port):
        self.server = url + ":" + str(port)

    def pack_arg0(self, arg0):
        payload = {'arg0': json.dumps(arg0, default=lambda o: o.__dict__, sort_keys=True)}
        return payload

    def _build_url(self, uri):
        return self.server + uri

    def get_user_summary_sync(self):
        uri = '/user_summary'
        r = requests.get(self._build_url(uri))
        r.raise_for_status()

        tmp = r.json()
        result = []
        for val in tmp:
            result.append(pro.UserInfoValue(val))
        return result

    def get_item(self, arg0):
        url = '/pkg'
        payload = self.pack_arg0(arg0)
        r = requests.get(self._build_url(url), params=payload)
        r.raise_for_status()

        return pro.NGramData(r.json())

    def mark_known(self, arg0):
        url = '/know_it'
        #payload = self.pack_arg0(arg0)
        r = requests.put(self._build_url(url), data=json.dumps(arg0, default=lambda o: o.__dict__, sort_keys=True))
        r.raise_for_status()


class ResearchAjax(object):
    def __init__(self, url, port):
        self.server = url + ":" + str(port)

    def pack_arg0(self, arg0):
        payload = {'arg0': json.dumps(arg0, default=lambda o: o.__dict__, sort_keys=True)}
        return payload

    def get_lengths_sentences(self, arg0):
        # FIXME: запрос длины предложений - статистика длин
        url = '/get_lengths_sentences'
        payload = self.pack_arg0(arg0)
        r = requests.get(self._build_url(url), params=payload)
        r.raise_for_status()

        return r.json()

    def _build_url(self, uri):
        return self.server + uri

    def get_distribution_sync(self, arg0):
        url = '/research/get_distribution'
        payload = self.pack_arg0(arg0)
        r = requests.get(self._build_url(url), params=payload)
        r.raise_for_status()

        tmp = r.json()
        result = []
        for val in tmp:
            result.append(pro.DistributionElem(val))
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
        test_file ='../test_data/lor.txt' #'../test_data/etalon.srt'
        with open(test_file, "r") as file_handle:
            data = file_handle.read()

        url = '/research/accept_text'

        payload = {'name': self.get_research_page_name(), 'text': data}
        r = requests.post(self._build_url(url), data=json.dumps(payload))
        r.raise_for_status()

    @staticmethod
    def get_research_page_name():
        return 'research_page'
