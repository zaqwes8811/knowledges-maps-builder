# encoding: utf-8
# Можно делать реальные запросы и обрабатывать реальные данные
# http://docs.scipy.org/doc/scipy-0.14.0/reference/tutorial/io.html
#
# http://scikit-learn.org/stable/index.html

# 3rdparty
import requests

# sys
import json

class PathValue(object):
    def __init__(self, page, gens, pos):
        self.pageName = page
        self.genNames = gens
        self.pointPos = pos

    def assign_deserealized(self, obj):
      self.pageName = obj['pageName']
      self.genNames = obj['genNames']
      #self.pointPos = obj['pointPos']

    def __init__(self, obj):
      self.assign_deserealized(obj)


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
      result.append(PathValue(val))

    return result


class ResearchAjax(object):
  def __init__(self, url, port):
    self.server = url + ":" + str(port)

  def _build_url(self, uri):
    return self.server + uri

  def get_distribution_sync(self, arg0):
    uri = '/pkg'
    payload = {'arg0': json.dumps(arg0, default=lambda o: o.__dict__, sort_keys=True, indent=4)};
    print payload
    r = requests.get(self._build_url(uri), params=payload)
    r.raise_for_status()
    #$.get(uri, args)


def main():
  server = 'http://localhost'
  port = 8080
  ajax = AppAjax(server, port)
  user_info = ajax.get_user_summary_sync()
  print user_info

  # get distribution
  rajax = ResearchAjax(server, port)
  rajax.get_distribution_sync(user_info[0])


if __name__ == '__main__':
    main()