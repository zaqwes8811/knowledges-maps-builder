#java -jar D:\work-libs\jython.jar %1
import sys
sys.path.append('./src')
sys.path.append('./src/py_libs')

# Other
import uasio.os_io.io_wrapper as dal

# App
import business.analysers.run as runner


def main():
    pass

if __name__=='__main__':
    main()


