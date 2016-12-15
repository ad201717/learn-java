## java内存映射文件 MappedByteBuffer

### 优势：
1. 提供了java有可能达到的最快IO
2. 关键优势是操作系统负责真正的写入，即使程序在刚刚写入内存之后就挂掉，操作系统仍会将内存中的数据写入文件
3. 共享内存，可以被多个进程同时访问，起到低时延的共享内存的作用

### example
1. simpleOpMappedByteBuffer 内存映射文件的基本API使用
2. 普通读写与MappedByteBuffer读写的简单速度对比
    * 总字节数 10<sup>9</sup> byte
    * 普通顺序写:                23,560ms
    * mappedByteBuffer顺序写:     1,281ms
    * 普通顺序读:                14,577ms
    * mappedByteBuffer顺序读:       49ms
