# encoding: utf-8
# Можно делать реальные запросы и обрабатывать реальные данные
# http://docs.scipy.org/doc/scipy-0.14.0/reference/tutorial/io.html
#
# http://scikit-learn.org/stable/index.html
#
# Conf.: субтитры/без предобработки/без глобального фильтра
# FIXME: глобальные проблемы. Нужно ужать распределение, но лучше без стемминга.
#   Можно например кластеризовать на 3. И из второго и третьего кластера брать только слова, кот. составляют триграммы
#   с первым.
#
# Conf: субтитры/без предобработки/с глобальным фильтром - много реальнее!
# FIXME: могут остаться только единицы и двойки - ооочень это вероятно
#
# FIXME: слов как правило много у которых частота 1-2, для искажения нужно добавить параметры
#   что есть вокруг. слово одно, значит предложение тоже одно, и биграм максимум 2.
#   Пусть длина предложения. Нужно не для всех слов. если частые, то еще больше искривит. Но нужно
#   посмотреть как выглядит распределение длин предложений, если оно равном. то смысла нет.
#   Есть - предложения, можно n-граммы посчитать, какие-то метрики для предложения, для всего тектса (есть ли смысл?)
#
# FIXME: bias vs variance
#
# FIXME: feature - может быть сделать типа предсказания - т.е. зависит от пердыдущих - будет прерываться
#
# Похоже лучше сделат окно скользящее по контексту, пока субтиты еще ничего, но дальше...
# Или лушче расширяющееся окно. как сам читал. Сразу много загружать нет смысла
# Сохраняем частоты всего текста, просто фильтруем
#
# FIXME: MAIN TROUBLE: очень плохое распределение.
# - добавить инфы для искажения малых - мало что изменилось
# - эквалайзинг - как-то не совсем ясно как
# - перемещающаяся граница - пока кажется лучшее решение

# app
import _code_store as code
import http_bridge
import protocol as pro

# 3rd party
import matplotlib.pyplot as plt

# sys
import unittest
import time


class TestSequenceFunctions(unittest.TestCase):
    def setUp(self):
        # Http part
        server = 'http://localhost'
        port = 8080
        self.research_ajax = http_bridge.ResearchAjax(server, port)
        #self.research_ajax.create_or_replace_page()
        time.sleep(2)
        self.arg0 = pro.PathValue(self.research_ajax.get_research_page_name())

        self.app_ajax = http_bridge.AppAjax(server, port)

    def plot_distribution(self):
        d = self.research_ajax.get_distribution_sync(self.arg0)
        code.plot_distribution(d)

    def accept_ngram_data(self):
        r = self.app_ajax.get_item(self.arg0)
        #self.assertIsNotNone(r)

        new_arg0 = self.arg0.clone()
        new_arg0.set_position(r.get_position())
        return new_arg0

    # TESTs
    def test_base(self):
        self.plot_distribution()

    def test_clustering(self):
        d = self.research_ajax.get_pure_distribution(self.arg0)

        # K-means
        code.cluster_kmeans(d)
        # Visuality
        #plt.grid(True)
        #plt.show()

    def test_nn_clustering(self):
        # Nearest Neighbors version
        pass

    def test_pure_clustering(self):
        pass
        #cluster_only_by_freq(d, estimator, n_clusters)

    def test_extract_sent_length(self):
        ls = self.research_ajax.get_lengths_sentences(self.arg0)
        ls.sort()

    def test_accept_word_data(self):
        self.accept_ngram_data()

    def test_boundary_expand(self):
        self.plot_distribution()

        if False:
            for j in range(3):
                for i in range(50):
                    arg0 = self.accept_ngram_data()
                    self.app_ajax.mark_known(arg0)

                self.plot_distribution()
        plt.show()

    def test_equalizer(self):
        r = self.research_ajax.dev_equalizer()
        print r



