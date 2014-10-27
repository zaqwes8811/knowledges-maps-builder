# encoding: utf-8
# Можно делать реальные запросы и обрабатывать реальные данные
# http://docs.scipy.org/doc/scipy-0.14.0/reference/tutorial/io.html
#
# http://scikit-learn.org/stable/index.html

import requests

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

  def get_user_summary_sync(self):
    uri = '/user_summary'
    r = requests.get(self.server + uri)
    r.raise_for_status()
    tmp = r.json()
    
    result = []
    for val in tmp:
      result.append(PathValue(val))

    return result


class ResearchAjax(object):
  def __init__(self, url, port):
    self.server = url + ":" + str(port)


def main():
  server = 'http://localhost'
  port = 8080
  ajax = AppAjax(server, port)
  ajax.get_user_summary_sync()


if __name__ == '__main__':
    main()