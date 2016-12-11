代码说明：
server：接收来自client的消息，解密之后再加密返回给client
client：接收来自user的消息，加密之后传输给server；读取server传输的消息，解密之后传输给user
user：读取键盘输入，发送给client，然后获得client的返回消息，将消息打印出来。

执行方法：
运行run.bat即可，会弹出三个java的窗口，分别表示server，client和user。

BUG说明：
1. server、client运行之后，关闭client的窗口，server也会关闭
2. client读取server返回的消息，发送给user之后，user读消息时能够读到正确的字节数，但是除了第一次，
之后几次打印的都是空的