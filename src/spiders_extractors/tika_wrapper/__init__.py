# coding: utf-8
""" 

Fails:
    Определение языка
"""
# Python2.7
import re
import json

# Develop
import dals.os_io.io_wrapper as dal

# Java
import java.io.BufferedInputStream as BufferedInputStream
import java.io.File as File
import java.io.FileInputStream as FileInputStream
import java.io.FileOutputStream as FileOutputStream
import java.io.IOException as IOException
import java.io.InputStream as InputStream
import java.io.PrintWriter as PrintWriter
import java.io.FileWriter as FileWriter
import java.io.BufferedWriter as BufferedWriter
import java.lang.String as String
import java.io.BufferedReader as BufferedReader
import java.io.FileReader as FileReader
import java.lang.StringBuilder as StringBuilder
import java.io.ByteArrayOutputStream as ByteArrayOutputStream
import java.io.OutputStream as OutputStream
import java.io.OutputStreamWriter as OutputStreamWriter
import java.net.URL as URL
import java.lang.System as System

# Tika
import org.apache.tika.exception.TikaException as TikaException
import org.apache.tika.metadata.Metadata as Metadata
import org.apache.tika.parser.AutoDetectParser as AutoDetectParser
import org.apache.tika.parser.ParseContext as ParseContext
import org.apache.tika.parser.Parser as Parser
import org.apache.tika.sax.BodyContentHandler as BodyContentHandler
import org.xml.sax.ContentHandler as ContentHandler
import org.xml.sax.SAXException as SAXException
import org.apache.tika.detect.DefaultDetector as DefaultDetector
import org.apache.tika.detect.Detector as Detector
import org.apache.tika.io.TikaInputStream as TikaInputStream
import org.apache.tika.Tika as Tika
import org.apache.tika.language.LanguageIdentifier as LanguageIdentifier
import org.apache.tika.language.ProfilingHandler as ProfilingHandler
#import org.apache.tika.language.ProfilingWriter
import org.apache.tika.language.ProfilingWriter as ProfilingWriter
import org.apache.tika.language.LanguageProfile as LanguageProfile
import org.apache.tika.parser.DelegatingParser as DelegatingParser
import org.apache.tika.sax.TeeContentHandler as TeeContentHandler

# App 
from filters import is_content_letter

def write_result_file(result_list, fname):
    sets = dal.get_utf8_template()
    sets['howOpen'] = 'w'
    sets['name'] = fname
    dal.list2file(sets, result_list)
    
def read_utf_txt_file(fname):
    sets = dal.get_utf8_template()
    sets['name'] = fname
    return dal.file2list(sets) 

def printer(msg):
    print msg
    
def _no_space_in_line():
    return True

class TextExtractorFromOdtDocPdf(object):
    """ 
    Извлекает и очищает текст из файла формата *.pdf, *.doc, *.docx, *.odt
    
    
    TODO(zaqwes): Сделать фильтры
        - в предложении только пробелы или только цифры
    """
    _outputstream = None
    _context = None
    _detector = None
    _parser = None
    _metadata = None
    _extractedText = None
    
    def __init__(self):
        self._context = ParseContext()
        self._detector = DefaultDetector()
        self._parser = AutoDetectParser(self._detector)
        self._context.set(Parser, self._parser)
        self._outputstream = ByteArrayOutputStream()
        self._metadata = Metadata()
        
    _enabled_extentons = ['doc', 'docx', 'pdf', 'odt']
        
    def process(self, ifname, tmp_file_root):
        """ 
        Присутствует фильта входных файлов, что упрощает обработку папок с разнородными
        файлами.
        
        TODO(zaqwes): Используется детектор языков от Tika, хотя детектирует правильно, 
            но выдает, что недостаточно. Возможно можно заменить на другую библитеку
        
        Postcond.:
            Текстовой файл с полностью снятым форматированием. Язык не определен.
            
        Trouble:
            - Если программа содержит код
        """
        def get_fname_for_save(fname):
            fname = fname.replace('\\','/')
            only_fname = fname.split('/')[-1]
            ofname = tmp_file_root+'/'+only_fname+'.txt'
            return ofname
        
        print 'Processing file : ', ifname
        def file_is_enabled(fname):
            extention = fname.split('.')[-1]
            if extention in self._enabled_extentons:
                return True
            return False
            
        if not file_is_enabled(ifname):
            return None, 1, 'File must *.doc, *.docx, *.pdf, *.odt. File skipped.'
        
        input_var = None
        
        ofname = get_fname_for_save(ifname)  # Файл временный, он же выходной
        out_file = File(ofname)  # Врядли выкенет исключение
        try:
            # TODO(zaqwes): не очень понятно, что здесь происходит
            url = URL
            file_obj = File(ifname);
            if file_obj.isFile():
                url = file_obj.toURI().toURL();
            else:
                url = URL(ifname);
            # TODO(zaqwes): не очень понятно, что здесь происходит
              
            # Начинаем обработку
            metadata = {'url':ifname}
            
            result_utf8 = ['metadata','']  # формат строгий!!
              
            # На данный момент сохраняем в промежуточный файл на диске, но можно и ускорить
            # например, через отображение на память
            input_var = TikaInputStream.get(url, self._metadata);
            handler = BodyContentHandler(FileOutputStream(out_file))#self._outputstream);
            self._parser.parse(input_var, handler, self._metadata, self._context);
            
            # Преобразуем в unicode
            java_in = BufferedReader(FileReader(ofname))
            
            writer = ProfilingWriter();
            def purge_line(one_line):
                if not is_content_letter(one_line):
                    return None
                result = one_line.replace('\t','')
                return result
            
            while True:
                s = String()
                s = java_in.readLine()
                if s == None:
                    break
                
                # Строку нужно подчистить
                one_line = unicode(str(s), 'utf-8')
                one_line = purge_line(one_line)
                if one_line:
                    result_utf8.append(one_line)
                    writer.append(one_line);
                
            identifier = writer.getLanguage();
            lang = identifier.getLanguage()
            metadata['lang'] = lang
            #print identifier.isReasonablyCertain()  # Всегда False
            #System.out.println(identifier.getLanguage());
            result_utf8[0] = json.dumps(metadata)
            # Сохраняем результат 
            write_result_file(result_utf8, ofname)
        
        except IOException as e:
            err_code = 1
            err_msg = 'Error: io.'
            e.printStackTrace()
            return ofname, err_code, err_msg
        except TikaException as e:
            # Отключит обработку? Нет не отключит, т.к. исключение поймано
            e.printStackTrace()
        except SAXException as e:
            e.printStackTrace()
        finally:
            if input_var:
                try:
                    input_var.close()
                except IOException as e:
                    e.printStackTrace();
                    
        # Подводим итоги
        return ((ofname, lang), 0, '')
        
        
    # TODO(zaqwes): русский язык определить не может    
    def parse_with_lang_detection(self, filename):
        """url = URL  # Отрывок не работает
        file = File(filename);
        if file.isFile():
          url = file.toURI().toURL();
        else:
          url = URL(filename);
          
        input = TikaInputStream.get(url, self._metadata);
        handler = BodyContentHandler(self._outputstream);
        
        profiler = ProfilingHandler(self._outputstream);
        #tee = TeeContentHandler(handler, profiler);
        #tee = TeeContentHandler(handler);
        
        self._parser.parse(input, profiler, self._metadata, self._context);
        identifier = profiler.getLanguage();
        if identifier.isReasonablyCertain():
           # metadata.set(Metadata.LANGUAGE, 
            print identifier.getLanguage()#);
            """
            
        pass
        
        # Детектирует неправильно
        profile = LanguageProfile(
            "несложно догадаться, реально угнетающих  ");
        identifier = LanguageIdentifier(profile);
        System.out.println(identifier.getLanguage());
        
        writer = ProfilingWriter();
        writer.append("несложно догадаться, реально угнетающих ситуаций встречается ");
        writer.append("несложно встречается ");
        writer.append("несложно догадаться,  ");
        writer.append("несложно догадаться, реально угнетающих ситуаций встречается ");
        identifier = writer.getLanguage();
        System.out.println(identifier.getLanguage());

        
    def print_string(self):
        extractedText = self._outputstream.toString();
        print extractedText
        

def doc_to_txt(ipdf_file, otxt_file):
    """ 
    
    Returns:
        Нефильтрованный текстовой файл в формате utf-8
    """
    err_code = 0
    err_msg = ''
    tmp_file = ipdf_file+'.tmp'
    istr = None
    ostream = None
    try:
        istr = BufferedInputStream(FileInputStream(File(ipdf_file)));
        parser = AutoDetectParser();
        ostream = FileOutputStream(tmp_file)
        handler = BodyContentHandler(System.out)#ostream)
        
        metadata = Metadata();
        
        # Выдает в stdout text
        parser.parse(istr, handler, metadata, ParseContext());
        
        for name in metadata.names():
           value = metadata.get(name);
           System.out.println("Metadata Name:  " + name);
           System.out.println("Metadata Value: " + value);

        
            # Преобразуем в unicode
        java_in = BufferedReader(FileReader(tmp_file))
        s = String()
        result_utf8 = []
        while True:
            s = java_in.readLine()
            if s == None:
                break
            result_utf8.append(unicode(str(s), 'utf-8'))
            #print s
            #result_utf8.append(s)
    
        sets = dal.get_utf8_template()
        sets['howOpen'] = 'w'
        sets['name'] = otxt_file
        dal.list2file(sets, result_utf8)
       
    except IOException as e:
        err_code = 1
        err_msg = 'Error: io.'
    except TikaException as e:
        e.printStackTrace()
    except SAXException as e:
        e.printStackTrace()
    finally:
        if ostream:
            try:
                istr.close();
            except IOException as e:
                e.printStackTrace();
        if istr:
            try:
                istr.close();
            except IOException as e:
                e.printStackTrace();
    return err_code, err_msg

def pdf_cp1251_to_text_utf8(ipdf_file, otxt_file):
    """ 
    
    Returns:
        Нефильтрованный текстовой файл в формате utf-8
    """
    err_code = 0
    err_msg = ''
    tmp_file = ipdf_file+'.tmp'
    istr = None
    ostream = None
    try:
        istr = BufferedInputStream(FileInputStream(File(ipdf_file)));
        parser = AutoDetectParser();
        ostream = FileOutputStream(tmp_file)
        handler = BodyContentHandler(ostream)
        
        metadata = Metadata();
        
        # Выдает в stdout text
        parser.parse(istr, handler, metadata, ParseContext());
        
            # Преобразуем в unicode
        java_in = BufferedReader(FileReader(tmp_file))
        s = String()
        result_utf8 = []
        while True:
            s = java_in.readLine()
            if s == None:
                break
            result_utf8.append(unicode(str(s), 'utf-8'))
            #print s
            #result_utf8.append(s)
    
        sets = dal.get_utf8_template()
        sets['howOpen'] = 'w'
        sets['name'] = otxt_file
        dal.list2file(sets, result_utf8)
       
    except IOException as e:
        err_code = 1
        err_msg = 'Error: io.'
    except TikaException as e:
        e.printStackTrace()
    except SAXException as e:
        e.printStackTrace()
    finally:
        if ostream:
            try:
                istr.close();
            except IOException as e:
                e.printStackTrace();
        if istr:
            try:
                istr.close();
            except IOException as e:
                e.printStackTrace();
    return err_code, err_msg


if __name__=='__main__':
    extractor = TextExtractor()
    extractor.parse_with_lang_detection('t.pdf')
    extractor.process('t.pdf')
    extractor.print_string()
    print
    print 'Done'