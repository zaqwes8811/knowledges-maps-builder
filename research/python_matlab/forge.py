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
import http_bridge

# 3rd party
import matplotlib.pyplot as plt
import numpy as np
import sklearn.cluster

# sys
import random
import unittest

def plot_distribution(d):
    # to NumPy arrays
    disabled = []
    x_disabled = []
    all_points = []
    x_all_points = []
    active = []
    x_active = np.array([], dtype=np.uint32)
    for i, elem in enumerate(d):
        all_points.append(elem.frequency)
        x_all_points.append(i)

        if not elem.inBoundary:
            disabled.append(elem.frequency)
            x_disabled.append(i)
        else:
            active.append(elem.frequency)
            x_active = np.append(x_active, i)  # FIXME: bad!

    # Processing
    plt.plot(x_all_points, all_points, '-', x_disabled, disabled, 'o')
    #plt.plot(-1 * x_active, active)
    plt.grid(True)
    plt.show()


def unroll_distribution(d):
    X = []
    Y = []
    for f, importance in enumerate(d):
        for j in range(importance):
            X.append([f])
            Y.append(random.gauss(0, 0.1))
    return np.array(X), np.array(Y)


def cluster_only_by_freq(_d, estimator, n_clusters):
    """
        Note: очень узкий первый и широкий второй кластеры, если два
        хотя дальше дробит кажется нормально. И все равно как-то не адекватно.
    """
    d_work = np.array(_d).reshape(len(_d), 1)
    v = estimator.fit_predict(d_work)
    for j in range(n_clusters):
        print np.where(v == j)


def cluster_kmeans(d):
    """
        Note: Смысла почти нет. Пусть кластера 3. Распределение делится почти поровну по числу кластеров.
        Второй кластер состоит из слов в 2 и 1 слово, а третий в 1 слово. Нужен лучшей фильтр
    """
    # Lloyd - это алгоритмы решения, а не алгоритм обучения, похоже
    #  Clustering - kMean
    # Expand data for training
    # FIXME: а может расщеплять не нужно, а так скромить кластеризатору?
    X, Y = unroll_distribution(d)

    # http://www.slideshare.net/SarahGuido/estimator-clustering-with-scikitlearn
    # распределят почти равномерно - сложное распределение - похоже кластеризатор считает его почти равномерным
    # да, пик есть, но это мало меняет, он узкий и не может сильно сузить кластер
    n_clusters = 3
    estimator = sklearn.cluster.KMeans(k=n_clusters, max_iter=300)
    x_active = np.array(X, dtype=np.uint64)  # positions

    np.random.shuffle(x_active)  # для кластеризации похоже не важно

    # FIXME: имена кластеров перемешаны!
    assignments = estimator.fit_predict(x_active)

    for j in range(n_clusters):
        z = np.where(assignments == j)
        x = x_active[z].ravel()
        y = (np.ones(x.shape) + np.random.laplace(size=x.shape)).ravel()
        plt.plot(x, y, 'o')  # FIXME: bad!


class TestSequenceFunctions(unittest.TestCase):

    def setUp(self):
        # Http part
        server = 'http://localhost'
        port = 8080
        self.ajax = http_bridge.ResearchAjax(server, port)
        self.ajax.create_or_replace_page()
        self.arg0 = http_bridge.PathValue(self.ajax.get_research_page_name())

    def test_base(self):
        d = self.ajax.get_distribution_sync(self.arg0)
        plot_distribution(d)

    def test_clustering(self):
        d = self.ajax.get_pure_distribution(self.arg0)

        # K-means
        cluster_kmeans(d)

        # Nearest Neighbors version

        # Visuality
        #plt.grid(True)
        #plt.show()

        #cluster_only_by_freq(d, estimator, n_clusters)

    def test_extract_sent_length(self):
        ls = self.ajax.get_lengths_sentences(self.arg0)
        ls.sort()

