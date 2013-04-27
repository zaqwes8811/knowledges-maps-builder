# coding: utf-8
# Sys
import os#.path.exists

# App
import app_utils as util

# crosscuttings
import crosscuttings.tools as tools 

kKeyTargetExts = 'extention list'
kKeyIgnoredDir = 'ignored dir'
kKeyRoot = 'root'

def check_crawler_target(target):
    rpt = []
    # Проверяем задание
    # Разрешенные расширения
    path_to_settings = "App/Spider extractor/auto_detected_extention"
    auto_processed_ext = tools.get_app_cfg_by_path(path_to_settings)
    target_ext = target[kKeyTargetExts]
    for ext in target_ext:
        if ext not in auto_processed_ext:
            rpt.append('File with this extention not automatically processed - *.'+ext)
            rpt.append('  Extention removed. If need then convert file to '+
                       '/'.join(auto_processed_ext))
            target_ext.remove(ext)
            
    # Доствпность корней
    roots = target[kKeyRoot]
    for diro in roots:
        if not os.path.exists(diro):
            roots.remove(diro)
            rpt.append('Root dir no found - '+diro+'. Path removed from target.')
            
    # Доствпность игнорируемых папок
    ignored = target[kKeyIgnoredDir]
    for diro in ignored:
        if not os.path.exists(diro):
            ignored.remove(diro)
            rpt.append('Ignored dir no found - '+diro+'. Path removed from target.')
            
    for diro in ignored:         
        # игнорируемый путь должен исходит из одного из корней
        content = False
        for root in roots:
            if root in diro:
                content = True
            
        if not content:    
            rpt.append('Ignored dir int root path - '+diro+'. Path removed from target.')
    return rpt

def get_target_object(raw_target):
    list_without_comments = []
    for line in raw_target:
        tmp_line = util.remove_forward_and_back_spaces(line.split('#')[0])
        if tmp_line:
            list_without_comments.append(tmp_line)
    
    # TODO(zaqwes): очистить от комментов
    target = {}
    def process_one_line(line):
        ptr = line.find(':')
        key = util.remove_forward_and_back_spaces(line[0:ptr])
        value = util.remove_forward_and_back_spaces(line[ptr+1:])
        if key == kKeyRoot:
            if key not in target: 
                target[key] = []
            target[key].append(value)
            target[key] = list(set(target[key]))
            
        elif key == kKeyTargetExts:
            list_ext = value.split(',')
            target[key] = list(util.remove_fandb_spaces_in_tuple(tuple(list_ext)))
        elif key == 'index name':
            target[key] = util.remove_forward_and_back_spaces(value)
        elif key == kKeyIgnoredDir:
            if key not in target: 
                target[key] = []
            target[key].append(value)
            target[key] = list(set(target[key]))
        else:
            print 'No used'
    
    map(process_one_line, list_without_comments)
    return target