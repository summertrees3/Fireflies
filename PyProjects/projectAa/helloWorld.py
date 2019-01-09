# print("hello world")

# print(abs(-1))

# def add(x, y, f):
#     return f(x) + f(y)
# print(add(-3, 5, abs))


# 多行注释
'''
from functools import reduce
# def fn(x,y):
#     return x * 10 + y
def char2num(s):
    digits={'0': 0, '1': 1, '2': 2, '3': 3, '4': 4, '5': 5, '6': 6, '7': 7, '8': 8, '9': 9}
    return digits[s]
print(reduce(lambda x, y: x * 10 + y,map(char2num,'13579')))
'''

# def is_odd(n):
#     return n % 2 == 1
# print(list(filter(is_odd, [1, 2, 4, 5, 6, 9, 10, 15])))
#
#
# def not_empty(s):
#     return s and s.strip()
# print(list(filter(not_empty, ['A', '', 'B', None, 'C', '  '])))
#
#
# print(sorted(['bob', 'about', 'Zoo', 'Credit'], key=str.lower, reverse=True))


# L = [('Bob', 75), ('Adam', 92), ('Bart', 66), ('Lisa', 88)]
# def by_name(t):
#     return t[0].upper()
# print(sorted(L, key=by_name))
# def by_score(t):
#     return -t[1]
# print(sorted(L, key=by_score, reverse=False))


# print(int('12345', base=8))
#
# import functools
#
# int2 = functools.partial(int, base=2)
# print(int2('1000000'))


# import sys
# from enum import Enum
# Month = Enum('Month', ('Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aut', 'Sep', 'Oct', 'Nov', 'Dec'))
# for name, member in Month.__members__.items():
#     print(name, '=>', member, ',', member.value)


# try:
#     print('try...')
#     r = 10 / 0
#     print('result:', r)
# except ZeroDivisionError as e:
#     print('except:', e)
# finally:
#     print('finally...')
# print('END')


# f = open('D:/testFile/person.txt', 'r')
# print(f.read())
# f.close()
#
# with open('D:/testFile/person.txt', 'r') as f1:
#     for line in f1.readlines():
#         print(line.strip())
#     # print(f1.read())


# fw = open('D:/testFile/fileWrite.txt', 'w')
# fw.write('hello,')
# fw.write('world!')
# fw.close()
#
# with open('D:/testFile/fileWrite.txt', 'w') as fw1:
#     # 传入'a'为追加
#     fw1.write('Hello,World!')


# from io import StringIO
#
# s = StringIO()
# s.write('hello')
# s.write(' alice')
# print(s.getvalue())


# from io import BytesIO
#
# b = BytesIO()
# b.write('中文'.encode('utf-8'))
# print(b.getvalue())


# import os
#
# # 操作系统类型
# print(os.name)
# # 操作系统中定义的环境变量
# print(os.environ)
# # 查看当前目录的绝对路径
# print(os.path.abspath('.'))
# print(os.path.join('D:/PyProjects', 'test'))
# print(os.path.split('D:/PyProjects/test'))
# # 删除目录
# # os.rmdir('D:/PyProjects/test')
# # 创建目录
# # os.mkdir('D:/PyProjects/test')
# # 列出当前目录下的所有目录
# # [x for x in os.listdir('.') if os.path.isdir(x)]
# # 列出所有的.py文件
# # [x for x in os.listdir('.') if os.path.isfile(x) and os.path.splitext(x)[1] == '.py']
#
# import shutil
#
# shutil.copyfile()


# import pickle
#
# d = dict(name='Bob', age=20, score=88)
# print(pickle.dumps(d))


# import json
# d = dict(name='Bob', age=20, score=88)
# print(json.dumps(d))
#
# json_str = '{"age": 20, "score": 88, "name": "Bob"}'
# print(json.loads(json_str))


# import json
# class Student(object):
#     def __init__(self, name, age, score):
#         self.name = name
#         self.age = age
#         self.score = score
# s = Student('Bob', 20, 88)
# print(json.dumps(s, default=lambda obj: obj.__dict__))
#
# def dict2student(d):
#     return Student(d['name'], d['age'], d['score'])
# json_str = '{"age": 20, "score": 88, "name": "Bob"}'
# print(json.loads(json_str, object_hook=dict2student))
#
# obj = dict(name='小明', age=20)
# s = json.dumps(obj, ensure_ascii=True)
# print(s)


# # 进程
# import os
# print('Process (%s) start...' % os.getpid())
# pid = os.fork()
# if pid == 0:
#     print('I am child process (%s) and my parent is %s.' % (os.getpid(), os.getppid()))
# else:
#     print('I (%s) just created a child process (%s).' % (os.getpid().pid))


# from multiprocessing import Process
# import os
# def run_proc(name):
#     print('Run child process %s (%s)...' % (name, os.getpid()))
# if __name__ == '__main__':
#     print('Parent process %s.' % os.getpid())
#     p = Process(target=run_proc, args=('test',))
#     print('Child process will start.')
#     p.start()
#     p.join()
#     print('Child process end.')


# 进程池Pool
# from multiprocessing import Pool
# import os, time, random
# def long_time_task(name):
#     print('Run task %s (%s)...' % (name, os.getpid()))
#     start = time.time()
#     time.sleep(random.random() * 3)
#     end = time.time()
#     print('Task %s runs %0.2f seconds.' % (name, (end - start)))
# if __name__ == '__main__':
#     print('Parent process %s.' % os.getpid())
#     p = Pool(5)
#     for i in range(4):
#         p.apply_async(long_time_task, args=(i,))
#     print('Waiting for all subprocesses done...')
#     p.close()
#     p.join()
#     print('All subprocesses done.')


# 子进程
# import subprocess
# print('$ nslookup www.python.org')
# r = subprocess.call(['nslookup', 'www.python.org'])
# print('Exit code:', r)


# from multiprocessing import Process, Queue
# import os, time, random
# # 写数据进程
# def write(q):
#     print('Process to write: %s' % os.getpid())
#     for value in ['A', 'B', 'C']:
#         print('Put %s to queue...' % value)
#         q.put(value)
#         time.sleep(random.random())
# # 读数据进程
# def read(q):
#     print('Process ro read: %s' % os.getpid())
#     while True:
#         value = q.get(True)
#         print('Get %s from queue.' % value)
# if __name__ == '__main__':
#     # 父进程创建Queue，并传给各个子进程
#     q = Queue()
#     pw = Process(target=write, args=(q,))
#     pr = Process(target=read, args=(q,))
#     # 启动子进程写入
#     pw.start()
#     # 启动子进程读取
#     pr.start()
#     # 等待pw结束
#     pw.join()
#     # pr进程里是死循环，无法等待期结束，只能强行终止
#     pr.terminate()


# 多线程
# import time, threading
# def loop():
#     print('thread %s is running...' % threading.current_thread().name)
#     n = 0
#     while n < 5:
#         n = n + 1
#         print('thread %s >>> %s' % (threading.current_thread().name, n))
#         time.sleep(1)
#     print('thread %s ended.' % threading.current_thread().name)
# # print('thread %s is running...' % threading.current_thread().name)
# t = threading.Thread(target=loop, name='LoopThread')
# t.start()
# t.join()
# # print('thread %s ended.' % threading.current_thread().name)


# from datetime import datetime
# now=datetime.now()
# print(now)
# dt=datetime(2018,5,1,11,1)
# print(dt)
# ts=dt.timestamp()
# print(ts)
# print(datetime.fromtimestamp(ts))
# print(datetime.utcfromtimestamp(ts))


# 摘要算法
# import hashlib
# # 128 bit字节，一个32位的16进制字符串
# md5 = hashlib.md5()
# md5.update('how to use md5 in '.encode('utf-8'))
# md5.update('python hashlib?'.encode('utf-8'))
# print(md5.hexdigest())
# # 160 bit字节，一个40位的16进制字符串
# sha1 = hashlib.sha1()
# sha1.update('how to use sha1 in '.encode('utf-8'))
# sha1.update('python hashlib?'.encode('utf-8'))
# print(sha1.hexdigest())


# 斐波那契数列
# def Fibonacci(num):
#     if num == 1 or num == 2:
#         return 1
#     elif num == 0:
#         return 0
#     else:
#         return Fibonacci(num-1) + Fibonacci(num - 2)
# def main():
#     num = int(input("请输入斐波那契的位数:"))
#     result = Fibonacci(num)
#     print("第%d位斐波那契数的值为%d"%(num, result))
# if __name__ == "__main__":
#     main()


# 阶乘
def factorial(num):
    if num == 1 or num == 0:
        return 1
    else:
        return num *factorial(num-1)
def main():
    num = int(input("请输入需要求阶乘的整数:"))
    result = factorial(num)
    print("%d的阶乘为%d"%(num, result))
    pass
if __name__ == '__main__':
    main()


