# coding: utf-8
# Other
import yaml

def get_app_cfg():
    f = open('app-cfgs/spider_cfg.yaml', 'r')
    configuration = yaml.load(f)
    f.close()
    return configuration

