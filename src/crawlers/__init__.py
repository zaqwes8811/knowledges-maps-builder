# coding: utf-8

# App
import app_utils as util

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
    for dir in roots:
        if not os.path.exists(dir):
            roots.remove(dir)
            rpt.append('Root dir no found - '+dir+'. Path removed from target.')
            
    # Доствпность игнорируемых папок
    ignored = target[kKeyIgnoredDir]
    for dir in ignored:
        if not os.path.exists(dir):
            ignored.remove(dir)
            rpt.append('Ignored dir no found - '+dir+'. Path removed from target.')
            
    for dir in ignored:         
        # игнорируемый путь должен исходит из одного из корней
        content = False
        for root in roots:
            if root in dir:
                content = True
            
        if not content:    
            rpt.append('Ignored dir int root path - '+dir+'. Path removed from target.')
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
            
        elif key == kKeyTargetExts:
            list_ext = value.split(',')
            target[key] = list(util.remove_fandb_spaces_in_tuple(tuple(list_ext)))
        elif key == 'index name':
            target[key] = util.remove_forward_and_back_spaces(value)
        elif key == kKeyIgnoredDir:
            if key not in target: 
                target[key] = []
            target[key].append(value)
        else:
            print 'No used'
    
    map(process_one_line, list_without_comments)
    return target